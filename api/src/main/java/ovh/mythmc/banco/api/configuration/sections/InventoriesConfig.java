package ovh.mythmc.banco.api.configuration.sections;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class InventoriesConfig {

    private SimpleInventory balanceTop = new SimpleInventory("ʙᴀɴᴄᴏ", "§e§l%s §a%s §7- §a%s");

    public record SimpleInventory(String title, String format) { }

}
