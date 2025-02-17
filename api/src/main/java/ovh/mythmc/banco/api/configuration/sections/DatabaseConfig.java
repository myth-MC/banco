package ovh.mythmc.banco.api.configuration.sections;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class DatabaseConfig {

    @Comment("Database type (SQLITE, MYSQL or MARIADB)")
    private DatabaseType type = DatabaseType.SQLITE;

    @Comment("Time between each cache clean in minutes")
    private int cacheClearInterval = 5;

    @Comment("Don't change this, you might lose all your data")
    private int databaseVersion = 0;

    public void setVersion(int version) {
        databaseVersion = version;
    }

    public enum DatabaseType {
        SQLITE,
        MYSQL,
        MARIADB
    }
    
}
