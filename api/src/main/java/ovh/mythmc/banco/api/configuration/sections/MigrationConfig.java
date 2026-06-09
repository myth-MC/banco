package ovh.mythmc.banco.api.configuration.sections;

import java.util.List;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class MigrationConfig {

    @Comment({
        "EXPERIMENTAL FEATURE - USE WITH CAUTION!!",
        "Set to 'true' to enable the migration from another Vault provider (only TNE is supported for now):", 
        "  1. Back up the current economy database", 
        "  2. Configure the item registry", 
        "  3. Shut down the server and set 'migration.enabled' to true", 
        "  4. Start the server and wait for the process to complete", 
        "  5. Shut down the server", 
        "  6. Uninstall the old economy provider and set 'migration.enabled' back to false", 
        "  7. Start the server and make sure everything works as expected",
        " ",
        "You can report any issues in our GitHub repo (https://github.com/myth-MC/banco) or in our Discord",
        "server (https://discord.gg/bpkwdzREcR)"
    })
    private boolean enabled = false;

    @Comment({
        "Some economy providers such as TNE support per-world currencies. Since this is not a supported",
        "feature in banco, you can configure the world accounts that will be merged into a single account",
        "below"
    })
    private List<String> worlds = List.of("world");
    
}
