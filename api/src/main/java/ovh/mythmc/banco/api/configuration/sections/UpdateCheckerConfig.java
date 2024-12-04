package ovh.mythmc.banco.api.configuration.sections;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class UpdateCheckerConfig {

    @Comment("Whether the update checker should be enabled")
    private boolean enabled = true;

    @Comment("Interval of hours between each check")
    private int intervalInHours = 6;

}
