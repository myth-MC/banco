package ovh.mythmc.banco.api.configuration.sections;

import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class MenusConfig {

    private InfoMenu info = new InfoMenu("ʙᴀɴᴄᴏ", "§e§l%s", "§7%s");

    private BalanceTopMenu balanceTop = new BalanceTopMenu("ʙᴀɴᴄᴏ", "§e§l%s §a%s §7- §a%s");

    public record InfoMenu(String title, String keyFormat, String valueFormat) { }

    public record BalanceTopMenu(String title, String format) { }

}
