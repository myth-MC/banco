package ovh.mythmc.banco.api.configuration;

import de.exlll.configlib.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.configuration.sections.*;

@Configuration
@Getter
public class BancoSettings {

    @Comment("Enabling this will send more messages to console")
    private boolean debug = false;

    @Comment("Language that is sent when a player's locale is not available")
    private @NotNull String defaultLanguageTag = "en-US";

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
