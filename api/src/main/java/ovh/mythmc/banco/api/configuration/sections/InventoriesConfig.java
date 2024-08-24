package ovh.mythmc.banco.api.configuration.sections;

import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class InventoriesConfig {

    private InfoInventory info = new InfoInventory("ʙᴀɴᴄᴏ", "§e§l%s", "§7%s");

    private BalanceTopInventory balanceTop = new BalanceTopInventory("ʙᴀɴᴄᴏ", "§e§l%s §a%s §7- §a%s");

    public record InfoInventory(String title, String keyFormat, String valueFormat) { }

    public record BalanceTopInventory(String title, String format) { }

}
