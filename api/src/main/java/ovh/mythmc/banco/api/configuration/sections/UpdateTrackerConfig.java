package ovh.mythmc.banco.api.configuration.sections;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class UpdateTrackerConfig {

    @Comment("Whether the update tracker should be enabled or not")
    private boolean enabled = true;

}
