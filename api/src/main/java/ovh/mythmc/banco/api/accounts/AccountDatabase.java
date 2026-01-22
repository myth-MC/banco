package ovh.mythmc.banco.api.accounts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;

import lombok.NoArgsConstructor;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.database.MySQLConnectionSource;
import ovh.mythmc.banco.api.accounts.database.SQLiteConnectionSource;
import ovh.mythmc.banco.api.logger.LoggerWrapper;
import ovh.mythmc.banco.api.scheduler.BancoScheduler;

/**
 * Manages account database operations including caching, persistence, and automatic backups.
 * <p>
 * This class handles all database interactions for accounts, including:
 * <ul>
 *   <li>Account creation, retrieval, and deletion</li>
 *   <li>Automatic caching of frequently accessed accounts</li>
 *   <li>Periodic automatic saving of cached accounts</li>
 *   <li>Database backup functionality</li>
 *   <li>Database schema upgrades</li>
 * </ul>
 * </p>
 *
 * @since 1.0.0
 */
@NoArgsConstructor
public final class AccountDatabase {

    private final ScheduledExecutorService asyncScheduler = Executors.newScheduledThreadPool(1);

    private static final String BACKUP_SUFFIX_PREFIX = "backup";
    private static final String DATABASE_LOG_PREFIX = "[database] ";

    private volatile Dao<Account, UUID> accountsDao;
    private volatile JdbcConnectionSource connectionSource;
    private final Map<AccountIdentifierKey, Account> cache = new ConcurrentHashMap<>();
    private final Set<AccountIdentifierKey> accountIdentifierCache = ConcurrentHashMap.newKeySet();

    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    private boolean firstBoot = false;
    private String path;

    private final LoggerWrapper logger = new LoggerWrapper() {
        @Override
        public void info(final String message, final Object... args) {
            Banco.get().getLogger().info(DATABASE_LOG_PREFIX + message, args);
        }

        @Override
        public void warn(final String message, final Object... args) {
            Banco.get().getLogger().warn(DATABASE_LOG_PREFIX + message, args);
        }

        @Override
        public void error(final String message, final Object... args) {
            Banco.get().getLogger().error(DATABASE_LOG_PREFIX + message, args);
        }
    };

    /**
     * Initializes the database connection and sets up the account table.
     *
     * @param path the file path for SQLite databases (ignored for MySQL)
     * @throws SQLException if database initialization fails
     * @throws IllegalStateException if already initialized
     */
    public void initialize(@NotNull String path) throws SQLException {
        if (!initialized.compareAndSet(false, true)) {
            throw new IllegalStateException("AccountDatabase has already been initialized");
        }

        if (shutdown.get()) {
            throw new IllegalStateException("Cannot initialize a shutdown database");
        }

        try {
            this.connectionSource = switch (Banco.get().getSettings().get().getDatabase().getType()) {
                case SQLITE -> new SQLiteConnectionSource(path);
                case MYSQL -> new MySQLConnectionSource();
            };

            TableUtils.createTableIfNotExists(connectionSource, Account.class);
            this.accountsDao = DaoManager.createDao(connectionSource, Account.class);

            this.path = path;

            final var databaseConfig = Banco.get().getSettings().get().getDatabase();
            this.firstBoot = !databaseConfig.isInitialized() && databaseConfig.getDatabaseVersion() == 0;

            performBackup(BACKUP_SUFFIX_PREFIX);
            upgrade();

            scheduleAutoSaver();

            final List<Account> allAccounts = get();
            final int accountCount = allAccounts != null ? allAccounts.size() : 0;
            Banco.get().getLogger().debug("Loaded {} accounts (using V3 format)", accountCount);

            if (allAccounts != null) {
                accountIdentifierCache.addAll(allAccounts.stream()
                    .map(Account::getIdentifier)
                    .toList());
            }

            databaseConfig.setDatabaseInitialized();
        } catch (SQLException e) {
            initialized.set(false);
            logger.error("Failed to initialize database: {}", e);
            throw e;
        }
    }

    /**
     * Ensures the DAO is available and the connection is active.
     *
     * @return the account DAO
     * @throws IllegalStateException if the database is not initialized
     */
    @NotNull
    private Dao<Account, UUID> getDao() {
        if (!initialized.get()) {
            throw new IllegalStateException("Database has not been initialized");
        }

        if (shutdown.get()) {
            throw new IllegalStateException("Database has been shut down");
        }

        try {
            if (connectionSource != null) {
                connectionSource.initialize(); // Reopen connection if necessary
            }
        } catch (SQLException e) {
            logger.error("Failed to initialize connection: {}", e);
            // Continue with existing DAO if available
        }

        if (accountsDao == null) {
            throw new IllegalStateException("Account DAO is not available");
        }

        return accountsDao;
    }

    /**
     * Creates a backup of the database file.
     *
     * @param differentiator the suffix to append to the backup filename
     */
    public void performBackup(@NotNull String differentiator) {
        if (path == null || path.isEmpty()) {
            logger.warn("Cannot create backup: database path is not set");
            return;
        }

        final File file = new File(path);
        if (!file.exists()) {
            logger.debug("Database file does not exist, skipping backup");
            return;
        }

        final File backupFile = new File(path + "." + differentiator);

        try {
            Files.deleteIfExists(backupFile.toPath());
            Files.copy(file.toPath(), new FileOutputStream(backupFile));
            logger.debug("Created database backup: {}", backupFile.getName());
        } catch (IOException e) {
            logger.error("Failed to create database backup: {}", e);
        }
    }

    /**
     * Legacy method name for backward compatibility.
     *
     * @param differentiator the suffix to append to the backup filename
     * @deprecated Use {@link #performBackup(String)} instead
     */
    @Deprecated
    public void backup(@NotNull String differentiator) {
        performBackup(differentiator);
    }

    /**
     * Shuts down the database, saving all pending changes.
     */
    public void shutdown() {
        if (!shutdown.compareAndSet(false, true)) {
            return; // Already shut down
        }

        try {
            updateAllDatabaseEntries();
        } finally {
            asyncScheduler.shutdown();
            try {
                if (!asyncScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    asyncScheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                asyncScheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }

            if (connectionSource != null) {
                try {
                    connectionSource.close();
                } catch (Exception e) {
                    logger.error("Error closing database connection: {}", e);
                }
            }
        }
    }

    /**
     * Creates a new account in the database.
     *
     * @param account the account to create
     * @throws IllegalArgumentException if account is null
     */
    public void create(@NotNull Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }

        try {
            getDao().createIfNotExists(account);
            accountIdentifierCache.add(account.getIdentifier());
            cache.put(account.getIdentifier(), account);
        } catch (SQLException e) {
            logger.error("Exception while creating account {}: {}", account.getIdentifier(), e);
        }
    }

    /**
     * Deletes an account from the database.
     *
     * @param account the account to delete
     * @throws IllegalArgumentException if account is null
     */
    public void delete(@NotNull Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }

        try {
            getDao().delete(account);
            accountIdentifierCache.remove(account.getIdentifier());
            cache.remove(account.getIdentifier());
        } catch (SQLException e) {
            logger.error("Exception while deleting account {}: {}", account.getIdentifier(), e);
        }
    }

    /**
     * Updates the cache with the given account.
     *
     * @param account the account to cache
     * @throws IllegalArgumentException if account is null
     */
    public void updateCache(@NotNull Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }
        cache.put(account.getIdentifier(), account);
    }

    /**
     * Schedules automatic saving of cached accounts at configured intervals.
     */
    private void scheduleAutoSaver() {
        if (shutdown.get()) {
            return;
        }

        final long intervalMinutes = Banco.get().getSettings().get().getDatabase().getCacheClearInterval();

        asyncScheduler.schedule(() -> {
            if (shutdown.get()) {
                return;
            }

            updateAllDatabaseEntries();
            scheduleAutoSaver(); // Schedule next iteration
        }, intervalMinutes, TimeUnit.MINUTES);
    }

    /**
     * Updates all cached accounts in the database.
     *
     * @return the time taken in milliseconds
     */
    public long updateAllDatabaseEntries() {
        if (cache.isEmpty()) {
            return 0;
        }

        final long startTime = System.currentTimeMillis();
        logger.debug("Updating {} cached accounts...", cache.size());

        Map.copyOf(cache).values().forEach(this::updateDatabaseEntry);

        final long totalTime = System.currentTimeMillis() - startTime;
        logger.debug("Done updating accounts (took {}ms)", totalTime);

        return totalTime;
    }

    /**
     * Updates a single account in the database.
     *
     * @param account the account to update
     * @return the time taken in milliseconds (approximate for async operations)
     * @throws IllegalArgumentException if account is null
     */
    public long updateDatabaseEntry(@NotNull Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }

        final Runnable task = () -> {
            try {
                getDao().update(account);
                cache.remove(account.getIdentifier());
            } catch (SQLException e) {
                logger.error("Exception while updating account {}: {}", account.getIdentifier(), e);
            }
        };

        final long startTime = System.currentTimeMillis();

        if (Banco.get().getSettings().get().getDatabase().isAsynchronousWrites()) {
            logger.debug("Saving {} ({}) asynchronously...",
                account.getIdentifier().uuid(),
                account.getIdentifier().name());
            BancoScheduler.get().runAsync(task);
        } else {
            task.run();
        }

        return System.currentTimeMillis() - startTime;
    }

    /**
     * Returns all currently cached accounts.
     *
     * @return an unmodifiable collection of cached accounts
     */
    @NotNull
    public Collection<Account> getCachedAccounts() {
        return Collections.unmodifiableCollection(cache.values());
    }

    /**
     * Returns all account identifiers in the cache.
     *
     * @return an unmodifiable collection of account identifiers
     */
    @NotNull
    public Set<AccountIdentifierKey> getAccountIdentifierCache() {
        return Set.copyOf(this.accountIdentifierCache);
    }

    /**
     * Retrieves all accounts from the database.
     *
     * @return a list of all accounts, or an empty list if an error occurs
     */
    @Nullable
    public List<Account> get() {
        try {
            final List<Account> accounts = getDao().queryForAll();
            return accounts != null ? accounts : Collections.emptyList();
        } catch (SQLException e) {
            logger.error("Exception while retrieving all accounts: {}", e);
            return Collections.emptyList();
        }
    }

    /**
     * Retrieves an account by its UUID.
     *
     * @param uuid the UUID of the account
     * @return the account if found, null otherwise
     * @throws IllegalArgumentException if uuid is null
     */
    @Nullable
    public Account getByUuid(@NotNull UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }

        final Account cachedAccount = findCachedAccountByUuid(uuid);
        if (cachedAccount != null) {
            return cachedAccount;
        }

        try {
            final Account account = getDao().queryForId(uuid);
            if (account != null) {
                cache.put(account.getIdentifier(), account);
            }
            return account;
        } catch (SQLException e) {
            logger.error("Exception while retrieving account by UUID {}: {}", uuid, e);
            return null;
        }
    }

    /**
     * Retrieves an account by its name (case-insensitive).
     *
     * @param name the name of the account
     * @return the account if found, null otherwise
     * @throws IllegalArgumentException if name is null or empty
     */
    @Nullable
    public Account getByName(@NotNull String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }

        final Account cachedAccount = findCachedAccountByName(name);
        if (cachedAccount != null) {
            return cachedAccount;
        }

        try {
            final List<Account> accounts = getDao().queryBuilder()
                    .where()
                    .like("name", name)
                    .query();

            if (accounts != null && !accounts.isEmpty()) {
                final Account account = accounts.getFirst();
                cache.put(account.getIdentifier(), account);
                return account;
            }
        } catch (SQLException e) {
            logger.error("Exception while retrieving account by name '{}': {}", name, e);
        }

        return null;
    }

    /**
     * Retrieves an account by name or UUID as a fallback.
     *
     * @param name the name of the account
     * @param uuid the UUID of the account (used as fallback)
     * @return the account if found, null otherwise
     * @throws IllegalArgumentException if name is null or empty
     */
    @Nullable
    public Account getByNameOrUuid(@NotNull String name, @Nullable UUID uuid) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }

        final Account cachedAccount = findCachedAccountByName(name);
        if (cachedAccount != null) {
            return cachedAccount;
        }

        try {
            final List<Account> accounts = getDao().queryBuilder()
                    .where()
                    .like("name", name)
                    .query();

            if (accounts != null && !accounts.isEmpty()) {
                final Account account = accounts.getFirst();
                cache.put(account.getIdentifier(), account);
                return account;
            }
        } catch (SQLException e) {
            logger.error("Exception while retrieving account by name '{}': {}", name, e);
        }

        if (uuid != null) {
            return getByUuid(uuid);
        }

        return null;
    }

    /**
     * Finds a cached account by UUID.
     *
     * @param uuid the UUID to search for
     * @return the cached account if found, null otherwise
     */
    @Nullable
    private Account findCachedAccountByUuid(@NotNull UUID uuid) {
        return cache.entrySet().stream()
            .filter(entry -> entry.getKey().uuid().equals(uuid))
            .map(Map.Entry::getValue)
            .findFirst()
            .orElse(null);
    }

    /**
     * Finds a cached account by name (case-insensitive).
     *
     * @param name the name to search for
     * @return the cached account if found, null otherwise
     */
    @Nullable
    private Account findCachedAccountByName(@NotNull String name) {
        return cache.entrySet().stream()
            .filter(entry -> entry.getKey().name() != null)
            .filter(entry -> entry.getKey().name().equalsIgnoreCase(name))
            .map(Map.Entry::getValue)
            .findFirst()
            .orElse(null);
    }

    /**
     * Performs database schema upgrades.
     */
    public void upgrade() {
        final int oldVersion = Banco.get().getSettings().get().getDatabase().getDatabaseVersion();

        // Upgrade from version 1 to version 2: Add name column
        if (oldVersion == 1) {
            try {
                logger.info("Upgrading database from version 1 to version 2...");
                getDao().executeRaw("ALTER TABLE `accounts` ADD COLUMN name STRING;");
                logger.info("Database upgrade completed successfully");
            } catch (SQLException e) {
                // Column might already exist, which is fine
                if (e.getMessage() != null &&
                    e.getMessage().contains("Could not run raw execute statement ALTER TABLE `accounts` ADD COLUMN name STRING")) {
                    logger.debug("Name column already exists, skipping upgrade");
                    return;
                }
                logger.error("Exception while upgrading database: {}", e);
            }
        }

        // Update version to 2
        if (!firstBoot) {
            Banco.get().getSettings().updateVersion(2);
        }
    }

    /**
     * Checks if the database has been initialized.
     *
     * @return true if initialized, false otherwise
     */
    public boolean isInitialized() {
        return initialized.get();
    }

    /**
     * Checks if the database has been shut down.
     *
     * @return true if shut down, false otherwise
     */
    public boolean isShutdown() {
        return shutdown.get();
    }
}
