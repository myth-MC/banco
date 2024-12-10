package ovh.mythmc.banco.api.accounts;

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
    private final Map<UUID, Account> cache = new HashMap<>();

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

        scheduleAutoSaver();

        if (Banco.get().getSettings().get().isDebug())
            Banco.get().getLogger().info("Loaded a total amount of " + get().size() + " accounts! (using V3 format)");
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
        cache.put(account.getUuid(), account);
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
        if (Banco.get().getSettings().get().isDebug())
            Banco.get().getLogger().info("Updating " + cache.size() + " cached accounts...");

        Map.copyOf(cache).values().forEach(this::updateDatabaseEntry);

        if (Banco.get().getSettings().get().isDebug())
            Banco.get().getLogger().info("Done!");
    }

    private void updateDatabaseEntry(@NotNull Account account) {
        try {
            accountsDao.update(account);

            // Clear cache value
            cache.remove(account.getUuid());
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
        if (cache.containsKey(uuid))
            return cache.get(uuid);

        try {
            Account account = accountsDao.queryForId(uuid);
            if (account == null)
                return null;

            cache.put(uuid, account);
            return account;
        } catch (SQLException e) {
            logger.error("Exception while getting account {}", e);
        }

        return null;
    }
    
}
