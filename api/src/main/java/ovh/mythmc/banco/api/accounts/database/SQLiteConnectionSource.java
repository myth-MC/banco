package ovh.mythmc.banco.api.accounts.database;

import java.sql.SQLException;

import org.jetbrains.annotations.NotNull;

import com.j256.ormlite.jdbc.JdbcConnectionSource;

import ovh.mythmc.banco.api.logger.LoggerWrapper;

/**
 * SQLite connection source implementation.
 * <p>
 * This class provides a connection to a SQLite database file.
 * </p>
 *
 * @since 1.0.0
 */
public final class SQLiteConnectionSource extends JdbcConnectionSource {

    private static final String CONNECTION_URL_PREFIX = "jdbc:sqlite:";

    /**
     * Creates a new SQLite connection source for the specified file path.
     *
     * @param path the path to the SQLite database file
     * @throws SQLException if the connection cannot be established
     * @throws IllegalArgumentException if path is null or empty
     */
    public SQLiteConnectionSource(@NotNull LoggerWrapper loggerWrapper, @NotNull String path) throws SQLException {
        super(CONNECTION_URL_PREFIX + path);
        loggerWrapper.debug("Established SQLite connection with path {}", path);
    }
}
