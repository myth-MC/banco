package ovh.mythmc.banco.api.accounts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import lombok.NoArgsConstructor;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.logger.LoggerWrapper;

@NoArgsConstructor
public final class AccountDatabase {

    private final ScheduledExecutorService asyncScheduler = Executors.newScheduledThreadPool(1);

    private Dao<Account, UUID> accountsDao;

    private final Map<AccountIdentifierKey, Account> cache = new HashMap<>();

    private boolean firstBoot = false;

    private String path;

    private final LoggerWrapper logger = new LoggerWrapper() {
        @Override
        public void info(final String message, final Object... args) {
            Banco.get().getLogger().info("[database] " + message, args);
        }

        @Override
        public void warn(final String message, final Object... args) {
            Banco.get().getLogger().warn("[database] " + message, args);
        }

        @Override
        public void error(final String message, final Object... args) {
            Banco.get().getLogger().error("[database] " + message, args);
        }
    };

    public void initialize(@NotNull String path) throws SQLException {
        ConnectionSource connectionSource = new JdbcConnectionSource("jdbc:sqlite:" + path);
        TableUtils.createTableIfNotExists(connectionSource, Account.class);
        accountsDao = DaoManager.createDao(connectionSource, Account.class);

        this.path = path;

        firstBoot = !Banco.get().getSettings().get().getDatabase().isInitialized() &&
            Banco.get().getSettings().get().getDatabase().getDatabaseVersion() == 0;

        backup("backup");
        upgrade();

        scheduleAutoSaver();

        if (Banco.get().getSettings().get().isDebug())
            Banco.get().getLogger().info("Loaded a total amount of " + get().size() + " accounts! (using V3 format)");

        Banco.get().getSettings().get().getDatabase().setDatabaseInitialized();
    }

    public void backup(String differentiator) {
        var file = new File(path);
        var oldFile = new File(path + "." + differentiator);
        
        try {
            Files.deleteIfExists(oldFile.toPath());
            Files.copy(file.toPath(), new FileOutputStream(oldFile));
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    public void shutdown() {
        updateAllDatabaseEntries();
    }

    public void create(@NotNull Account account) {
        try {
            accountsDao.createIfNotExists(account);
        } catch (SQLException e) {
            logger.error("Exception while creating account {}", e);
        }
    }

    public void delete(@NotNull Account account) {
        try {
            accountsDao.delete(account);
        } catch (SQLException e) {
            logger.error("Exception while deleting account {}", e);
        }
    }

    public void update(@NotNull Account account) {
        cache.put(account.getIdentifier(), account);
    }

    private void scheduleAutoSaver() {
        asyncScheduler.schedule(new TimerTask() {
            @Override
            public void run() {
                // Update all entries
                updateAllDatabaseEntries();

                // Schedule another task
                scheduleAutoSaver();

            }
        }, Banco.get().getSettings().get().getDatabase().getCacheClearInterval(), TimeUnit.MINUTES);
    }

    private void updateAllDatabaseEntries() {
        var startTime = System.currentTimeMillis();

        if (Banco.get().getSettings().get().isDebug())
            Banco.get().getLogger().info("Updating " + cache.size() + " cached accounts...");

        Map.copyOf(cache).values().forEach(this::updateDatabaseEntry);

        if (Banco.get().getSettings().get().isDebug())
            Banco.get().getLogger().info("Done! (took " + (System.currentTimeMillis() - startTime) + "ms)");
    }

    private void updateDatabaseEntry(@NotNull Account account) {
        try {
            accountsDao.update(account);

            // Clear cache value
            cache.remove(account.getIdentifier());
        } catch (SQLException e) {
            logger.error("Exception while updating account {}", e);
        }
    }

    public Collection<Account> getCachedAccounts() {
        return cache.values();
    }

    public List<Account> get() {
        try {
            return accountsDao.queryForAll();
        } catch (SQLException e) {
            logger.error("Exception while getting every account {}", e);
        }

        return null;
    }

    public Account getByUuid(@NotNull UUID uuid) {
        Account cachedAccount = findCachedAccountByUuid(uuid);
        if (cachedAccount != null)
            return cachedAccount;

        try {
            Account account = accountsDao.queryForId(uuid);
            if (account == null)
                return null;

            cache.put(account.getIdentifier(), account);
            return account;
        } catch (SQLException e) {
            logger.error("Exception while getting account {}", e);
        }

        return null;
    }

    public Account getByName(@NotNull String name) {
        Account cachedAccount = findCachedAccountByName(name);
        if (cachedAccount != null)
            return cachedAccount;

        try {
            List<Account> accounts = accountsDao.queryBuilder()
                    .where()
                    .eq("name", name)
                    .query();

            if (accounts != null && !accounts.isEmpty()) {
                Account account = accounts.getFirst();
                
                cache.put(account.getIdentifier(), account);
                return account;
            }
        } catch (SQLException e) {
            logger.error("Exception while getting account {}", e);
        }

        return null;
    }

    public Account getByNameOrUuid(@NotNull String name, UUID uuid) {
        Account cachedAccount = findCachedAccountByName(name);
        if (cachedAccount != null)
            return cachedAccount;

        try {
            List<Account> accounts = accountsDao.queryBuilder()
                    .where()
                    .eq("name", name)
                    .query();

            if (accounts != null && !accounts.isEmpty()) {
                Account account = accounts.getFirst();
                
                cache.put(account.getIdentifier(), account);
                return account;
            }
        } catch (SQLException e) {
            if (uuid != null)
                return getByUuid(uuid);
        }

        return null;
    }

    private Account findCachedAccountByUuid(@NotNull UUID uuid) {
        return cache.entrySet().stream()
            .filter(entry -> entry.getKey().uuid().equals(uuid))
            .map(entry -> entry.getValue())
            .findFirst().orElse(null);
    }

    private Account findCachedAccountByName(@NotNull String name) {
        return cache.entrySet().stream()
            .filter(entry -> entry.getKey().name() != null)
            .filter(entry -> entry.getKey().name().equals(name))
            .map(entry -> entry.getValue())
            .findFirst().orElse(null);
    }

    public void upgrade() {
        if (!firstBoot) {
            int oldVersion = Banco.get().getSettings().get().getDatabase().getDatabaseVersion();
            if(oldVersion < 1) {
                try {
                    logger.info("Upgrading database...");
                    accountsDao.executeRaw("ALTER TABLE `accounts` ADD COLUMN name STRING;");
                    logger.info("Done!");
                } catch (SQLException e) {
                    logger.error("Exception while upgrading database: {}", e);
                }
            } 
        }

        Banco.get().getSettings().updateVersion(1);  
    }
    
}
