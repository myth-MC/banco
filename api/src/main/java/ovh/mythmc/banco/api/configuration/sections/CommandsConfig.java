package ovh.mythmc.banco.api.configuration.sections;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class CommandsConfig {

    @Comment("Disabling commands will only work in PaperMC and its forks")
    private Command balance = new Command(true);

    @Comment("Disabling commands will only work in PaperMC and its forks")
    private Command pay = new Command(true);

    public record Command(boolean enabled) { }

}
