package ovh.mythmc.banco.api.configuration.sections;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class AutoSaveConfig {

    @Comment("Whether the auto-saver should be enabled or not")
    private boolean enabled = true;

    @Comment("Save frequency in seconds (900 seconds = every 15 minutes)")
    private int frequency = 900;

}
