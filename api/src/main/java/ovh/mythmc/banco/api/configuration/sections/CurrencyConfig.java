package ovh.mythmc.banco.api.configuration.sections;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.items.BancoItem;

import java.math.BigDecimal;
import java.util.List;

@Configuration
@Getter
public class CurrencyConfig {

    @Comment("Currency name in singular")
    private @NotNull String nameSingular = "Dollar";

    @Comment("Currency name in plural")
    private @NotNull String namePlural = "Dollars";

    @Comment("Currency symbol")
    private @NotNull String symbol = "$";

    @Comment("How money should be displayed (default = 1,451.34)")
    private @NotNull String format = "#,###.##";

    @Comment("Whether to prevent valuable items from dropping when killing mobs or not")
    private boolean removeDrops = true;

    @Comment("Whether to count items stored in a player's ender chest or not")
    private boolean countEnderChest = true;

    @Comment("Enable this if you want to give players the chance of changing lower value items for higher value ones by using /balance change")
    private boolean changeMoney = false;

    @Comment("Options: PLAYER_INVENTORY or ENDER_CHEST")
    private InventoryPriority inventoryPriority = InventoryPriority.PLAYER_INVENTORY;

    @Comment("Worlds where banco's economy should be disabled")
    private @NotNull List<String> blacklistedWorlds = List.of("exampleWorldName");

    @Comment({"Configure items and their respective value", "Please, put less valuable items first. You can use '§' to format text"})
    private List<BancoItem> items = List.of(
            new BancoItem("COPPER_INGOT", "§eCent", List.of("This is a simple setup example!", "You can use §bcolours §rto format text", " ", "§dCustom model data §ris also supported!"), 1009, true, BigDecimal.valueOf(0.1)),
            new BancoItem("GOLD_INGOT", null, null, null, null, BigDecimal.valueOf(1)),
            new BancoItem("GOLD_BLOCK", null, null, null, null, BigDecimal.valueOf(9))
    );

    public enum InventoryPriority {
        PLAYER_INVENTORY,
        ENDER_CHEST
    }

}
