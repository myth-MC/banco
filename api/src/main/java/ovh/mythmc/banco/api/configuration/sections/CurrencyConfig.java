package ovh.mythmc.banco.api.configuration.sections;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;
import ovh.mythmc.banco.api.items.BancoItem;
import ovh.mythmc.banco.api.items.impl.LegacyBancoItem;
import ovh.mythmc.banco.api.items.impl.VanillaBancoItem;
import ovh.mythmc.banco.api.items.impl.VanillaBancoItem.BancoItemOptions;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

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

    @Comment({"Whether to prevent items configured below from dropping when killing mobs", "This prevents players from building money farms, so it's recommended to keep it enabled"})
    private boolean removeDrops = true;

    @Comment("Enable this if you want to give players the chance of changing lower value items for higher value ones by using /balance change")
    private boolean changeMoney = false;

    @Comment("Order in which items will be added/removed from storages. You can add or remove entries to customize your setup")
    private List<InventoryPriority> inventoryOrder = List.of(
        InventoryPriority.BUNDLE,
        InventoryPriority.PLAYER_INVENTORY,
        InventoryPriority.ENDER_CHEST
    );

    @Comment("Worlds where banco's economy should be disabled")
    private @NotNull List<String> blacklistedWorlds = List.of("exampleWorldName");

    @Comment({"Configure items and their respective value", "Please, put less valuable items first. You can use MiniMessage to format text"})
    private List<BancoItem> itemRegistry = List.of(
        new VanillaBancoItem(Material.GOLD_INGOT, BigDecimal.valueOf(1), null),
        new VanillaBancoItem(Material.GOLD_BLOCK, BigDecimal.valueOf(9), null),
        new VanillaBancoItem(Material.PLAYER_HEAD, BigDecimal.valueOf(576), new BancoItemOptions("<gold>Bag of Gold Blocks</gold>", List.of("<gray>This bag contains <gold>64x Gold Blocks</gold></gray>"), 1009, true, "http://textures.minecraft.net/texture/95fd67d56ffc53fb360a17879d9b5338d7332d8f129491a5e17e8d6e8aea6c3a"))
    );

    // Legacy items
    private List<LegacyBancoItem> items = null;

    public enum InventoryPriority {
        BUNDLE,
        PLAYER_INVENTORY,
        ENDER_CHEST
    }

}
