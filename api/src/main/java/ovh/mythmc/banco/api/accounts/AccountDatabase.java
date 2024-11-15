package ovh.mythmc.banco.api.accounts;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

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

    private Dao<Account, UUID> accountsDao;

    private LoggerWrapper logger;

    public void initialize(@NotNull String path) throws SQLException {
        ConnectionSource connectionSource = new JdbcConnectionSource("jdbc:sqlite:" + path);
        TableUtils.createTableIfNotExists(connectionSource, Account.class);
        accountsDao = DaoManager.createDao(connectionSource, Account.class);
        if (Banco.get().getSettings().get().isDebug())
            Banco.get().getLogger().info("Loaded a total amount of " + get().size() + " accounts! (using V3 format)");
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
        try {
            accountsDao.update(account);
            if (Banco.get().getSettings().get().isDebug())
                Banco.get().getLogger().info("Updating account: " + account.toString());
        } catch (SQLException e) {
            logger.error("Exception while updating account {}", e);
        }
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
        try {
            return accountsDao.queryForId(uuid);
        } catch (SQLException e) {
            logger.error("Exception while getting account {}", e);
        }

        return null;
    }
    
}
