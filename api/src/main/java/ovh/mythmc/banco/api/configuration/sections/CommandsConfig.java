package ovh.mythmc.banco.api.configuration.sections;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class CommandsConfig {

    private String infoPrefix = "<#6ab2c5>\uD83D\uDD14</#6ab2c5>";

    private String warnPrefix = "<#ffa319>\u26A0</#ffa319>";

    private String successPrefix = "<#6ebc51>\u2714</#6ebc51>";

    private String errorPrefix = "<#810909>\u274C</#810909>";

    @Comment("Disabling commands will only work in Paper and its forks")
    private SimpleCommand balance = new SimpleCommand(true);

    private SimpleCommand pay = new SimpleCommand(true);

    private SimpleCommand balanceTop = new SimpleCommand(true);

    public record SimpleCommand(boolean enabled) { }

}
