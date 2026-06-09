package ovh.mythmc.banco.api.configuration.sections;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class UpdateCheckerConfig {

    @Comment("Set to 'true' to enable the update checker. The plugin will check for updates every 'intervalInHours' hours and notify the console if a new version is available")
    private boolean enabled = true;

    @Comment("Interval of hours between each check for updates")
    private int intervalInHours = 6;

}
