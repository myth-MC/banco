package ovh.mythmc.banco.api.configuration;

import de.exlll.configlib.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.configuration.sections.*;
import ovh.mythmc.banco.api.scheduler.ExecutionOrder;

@Configuration
@Getter
public class BancoSettings {

    @Comment("Enabling this will send more messages to console")
    private boolean debug = false;

    @Comment("Language that is sent when a player's locale is not available")
    private @NotNull String defaultLanguageTag = "en-US";

    @Comment("Delay between queued task execution in ticks (20 ticks = 1 second)")
    private int taskQueueDelay = 2;

    @Comment({"Task execution type (use SYNC in small servers or switch to ASYNC in larger ones)", "Do note that ASYNC may introduce unexpected behavior"})
    private ExecutionOrder taskExecutionOrder = ExecutionOrder.SYNC;

    @Comment({"", "Configuration for currency"})
    private CurrencyConfig currency = new CurrencyConfig();

    @Comment({"", "Configuration for the database"})
    private DatabaseConfig database = new DatabaseConfig();

    @Comment({"", "Configuration for the update checker"})
    private UpdateCheckerConfig updateChecker = new UpdateCheckerConfig();

    @Comment({"", "Configuration for commands"})
    private CommandsConfig commands = new CommandsConfig();

    @Comment({"", "Configuration for menus/GUIs"})
    private MenusConfig menus = new MenusConfig();

}
