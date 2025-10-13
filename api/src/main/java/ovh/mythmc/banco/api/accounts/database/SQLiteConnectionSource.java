package ovh.mythmc.banco.api.accounts.database;

import java.sql.SQLException;

import com.j256.ormlite.jdbc.JdbcConnectionSource;

import io.th0rgal.oraxen.shaded.jetbrains.annotations.NotNull;

public final class SQLiteConnectionSource extends JdbcConnectionSource {

    public SQLiteConnectionSource(@NotNull String path) throws SQLException {
        super("jdbc:sqlite:" + path);
    }
    
}
