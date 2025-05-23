package ovh.mythmc.banco.api.accounts.database;

import java.sql.SQLException;

import com.j256.ormlite.jdbc.JdbcConnectionSource;

import ovh.mythmc.banco.api.Banco;

public final class MySQLConnectionSource extends JdbcConnectionSource {

    public MySQLConnectionSource() throws SQLException {
        final var host = Banco.get().getSettings().get().getDatabase().getHost();
        final var port = Banco.get().getSettings().get().getDatabase().getPort();
        final var database = Banco.get().getSettings().get().getDatabase().getDatabase();

        // Set connection URL
        this.setUrl("jdbc:mysql://" + host + ":" + port + "/" + database);

        // Set credentials
        this.setUsername(Banco.get().getSettings().get().getDatabase().getUsername());
        this.setPassword(Banco.get().getSettings().get().getDatabase().getPassword());

        initialize();
    }
    
}
