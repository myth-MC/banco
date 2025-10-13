package ovh.mythmc.banco.api.configuration.sections;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class DatabaseConfig {

    @Comment("Database type (SQLITE or MYSQL)")
    private DatabaseType type = DatabaseType.SQLITE;

    @Comment("MySQL connection")
    private String host = "localhost";

    private int port = 3306;

    private String database = "banco";

    private String username = "";

    private String password = "";

    @Comment("Time between each cache clean in minutes")
    private int cacheClearInterval = 5;

    @Comment("Don't change this, you might lose all your data")
    private int databaseVersion = 0;

    @Comment("Don't change this, you might lose all your data")
    private boolean initialized = false;

    public void setVersion(int version) {
        databaseVersion = version;
    }

    public void setDatabaseInitialized() {
        this.initialized = true;
    }

    public enum DatabaseType {
        SQLITE,
        MYSQL
    }
    
}
