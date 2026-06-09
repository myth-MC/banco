package ovh.mythmc.banco.api.configuration.sections;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;
import ovh.mythmc.banco.api.items.BancoItem;
import ovh.mythmc.banco.api.items.impl.LegacyBancoItem;
import ovh.mythmc.banco.api.items.impl.VanillaBancoItem;
import ovh.mythmc.banco.api.items.impl.VanillaBancoItem.BancoItemOptions;
import ovh.mythmc.banco.api.items.impl.VanillaBancoItem.BancoItemOptions.RestrictedInteraction;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Configuration
@Getter
public class CurrencyConfig {

    @Comment("Currency name in singular")
    private @NotNull String nameSingular = "Emerald";

    @Comment("Currency name in plural")
    private @NotNull String namePlural = "Emeralds";

    @Deprecated(forRemoval = true, since = "1.4.0")
    private @NotNull String symbol;

    @Comment("Symbol to display before the amount. You can use MiniMessage formatting here. If you want to display something after the amount, use the 'suffix' option instead")
    private @NotNull String prefix = "";

    @Comment("Symbol to display after the amount. You can use MiniMessage formatting here. If you want to display something before the amount, use the 'prefix' option instead")
    private @NotNull String suffix = "$";

    @Comment("Decimal format to use when displaying money values. For example, '#,###.##' will display 1234567.89 as '1,234,567.89'")
    private @NotNull String format = "#,###.##";

    @Comment("Number of decimal places to round to")
    private int scale = 2;

    @Comment({
        "Rounding mode to use when normalizing money values",
        "https://docs.oracle.com/javase/8/docs/api/java/math/RoundingMode.html"
    })
    private RoundingMode roundingMode = RoundingMode.HALF_UP;

    @Comment({
        "Whether to prevent items configured below from dropping when killing mobs", 
        "This prevents players from building money farms, so it's recommended to keep it enabled"
    })
    private boolean removeDrops = true;

    @Comment("Defines the order in which items will be added/removed from storages. You can add or remove entries to customize your setup")
    private List<String> inventoryOrder = List.of(
        "BUNDLE",
        "PLAYER_INVENTORY",
        "ENDER_CHEST",
        "SHULKER_BOX",
        "OTHER"
    );

    @Comment("List of worlds where the plugin will not take effect. You can add or remove entries to customize your setup")
    private @NotNull List<String> blacklistedWorlds = List.of("exampleWorldName");

    @Comment("Whether to allow players to have a negative balance. Some plugins may require tweaking this option to work properly, so you can disable it if you notice any issues")
    private boolean negativeBalance = true;

    @Comment({
        "List of items that can be used as currency. You can add or remove entries to customize your setup. The default configuration includes Emeralds, Emerald Blocks and a custom Player Head representing a bag of Emerald Blocks.",
        "The 'value' field represents how much each item is worth in terms of the currency. For example, with the default configuration, 1 Emerald is worth 1 unit of currency, while 1 Emerald Block is worth 9 units (since it's made of 9 Emeralds). The custom Player Head is worth 576 units because it represents a bag that can hold 64 Emerald Blocks (64 x 9 = 576).",
        "The 'options' field allows you to customize the appearance and behavior of each item. For example, you can set a custom name and lore for the item, specify a custom texture for player heads, and even add attribute modifiers that affect player stats when the item is held or worn."
    })
    private List<BancoItem> itemRegistry = List.of(
        new VanillaBancoItem(Material.EMERALD, BigDecimal.valueOf(1), null),
        new VanillaBancoItem(Material.EMERALD_BLOCK, BigDecimal.valueOf(9), null),
        new VanillaBancoItem(Material.PLAYER_HEAD, BigDecimal.valueOf(576), new BancoItemOptions(
            null,
            "<white>Bag of Emerald Blocks</white>", 
            List.of("<gray>Holds <white>64x Emerald Blocks</white></gray>"), 
            1009, 
            false, 
            4,
            "http://textures.minecraft.net/texture/31d827a5decb0ae730abb69617776e1894f2bdb46968540433115d3688fbac38",
            null,
            List.of(
                RestrictedInteraction.ALL
            ),
            null
            //List.of(
            //    new BancoItemOptions.AttributeField("minecraft:movement_speed", -0.0025, AttributeModifier.Operation.ADD_NUMBER, "ANY")
            //)
        ))
    );

    // Legacy items
    private List<LegacyBancoItem> items = null;

}
