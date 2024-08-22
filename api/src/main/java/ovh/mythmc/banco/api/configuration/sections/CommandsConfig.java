package ovh.mythmc.banco.api.configuration.sections;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class CommandsConfig {

    @Comment("Disabling commands will only work in PaperMC and its forks")
    private SimpleCommand balance = new SimpleCommand(true);

    private SimpleCommand pay = new SimpleCommand(true);

    private SimpleCommand balanceTop = new SimpleCommand(true);

    public record SimpleCommand(boolean enabled) { }

}
