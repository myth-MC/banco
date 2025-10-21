package ovh.mythmc.banco.api.configuration.sections;

import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class MenusConfig {

    private InfoMenu info = new InfoMenu("ʙᴀɴᴄᴏ", "<yellow><bold>%s</bold></yellow>", "<gray>%s</gray>");

    private BalanceTopMenu balanceTop = new BalanceTopMenu("ʙᴀɴᴄᴏ", "<yellow><bold>%s</bold></yellow> <green>%s</green> <gray>-</gray> <green>%s</green>");

    public record InfoMenu(String title, String keyFormat, String valueFormat) { }

    public record BalanceTopMenu(String title, String format) { }

}
