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

    @Comment({"/balance - Allows players to view their current balance", "Permission: banco.use.balance (assigned by default)"})
    private SimpleCommand balance = new SimpleCommand(true);

    @Comment({"/balancechange - Allows players to compact their balance", "Permission: banco.use.balancechange (assigned by default)"})
    private SimpleCommand balanceChange = new SimpleCommand(false);

    @Comment({"/balancetop - Allows players to view the balance top", "Permission: banco.use.balancetop (assigned by default)"})
    private SimpleCommand balanceTop = new SimpleCommand(true);

    @Comment({"/pay - Allows players to send money to other accounts", "Permission: banco.use.pay (assigned by default)"})
    private SimpleCommand pay = new SimpleCommand(true);

    public record SimpleCommand(boolean enabled) { }

}
