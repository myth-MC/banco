package ovh.mythmc.banco.api.accounts.database;

import java.sql.SQLException;

import com.j256.ormlite.jdbc.JdbcConnectionSource;

import ovh.mythmc.banco.api.Banco;

/**
 * MySQL connection source implementation.
 * <p>
 * This class provides a connection to a MySQL database using the configuration
 * from the plugin settings.
 * </p>
 *
 * @since 1.1.0
 */
public final class MySQLConnectionSource extends JdbcConnectionSource {

    private static final String CONNECTION_URL_TEMPLATE = "jdbc:mysql://%s:%d/%s?sessionVariables=wait_timeout=99999999,interactive_timeout=99999999";

    /**
     * Creates a new MySQL connection source using settings from the plugin configuration.
     *
     * @throws SQLException if the connection cannot be established
     */
    public MySQLConnectionSource() throws SQLException {
        final var databaseConfig = Banco.get().getSettings().get().getDatabase();
        final String host = databaseConfig.getHost();
        final int port = databaseConfig.getPort();
        final String database = databaseConfig.getDatabase();

        // Set connection URL
        final String url = String.format(CONNECTION_URL_TEMPLATE, host, port, database);
        this.setUrl(url);

        // Set credentials
        this.setUsername(databaseConfig.getUsername());
        this.setPassword(databaseConfig.getPassword());

        initialize();
    }

    @Override
    public void initialize() throws SQLException {
        if (connection != null && connection.isClosed()) {
            closeConnection();
        }

        super.initialize();
    }

    /**
     * Closes the database connection.
     * <p>
     * Any exceptions that occur during closing are silently ignored.
     * </p>
     */
    public void closeConnection() {
        try {
            this.close();
        } catch (Exception e) {
            // Connection closing errors are typically not critical during shutdown
        }
    }
}
