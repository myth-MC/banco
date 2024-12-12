package ovh.mythmc.banco.api.configuration.sections;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;
import ovh.mythmc.banco.api.items.BancoItem;
import ovh.mythmc.banco.api.items.impl.LegacyBancoItem;
import ovh.mythmc.banco.api.items.impl.VanillaBancoItem;
import ovh.mythmc.banco.api.items.impl.VanillaBancoItem.BancoItemOptions;

import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
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
    private List<String> inventoryOrder = List.of(
        "BUNDLE",
        "PLAYER_INVENTORY",
        "ENDER_CHEST",
        "OTHER"
    );

    @Comment("Worlds where banco's economy should be disabled")
    private @NotNull List<String> blacklistedWorlds = List.of("exampleWorldName");

    @Comment({"Configure items and their respective value", "Please, put less valuable items first. You can use MiniMessage to format text"})
    private List<BancoItem> itemRegistry = List.of(
        new VanillaBancoItem(Material.EMERALD, BigDecimal.valueOf(1), null),
        new VanillaBancoItem(Material.EMERALD_BLOCK, BigDecimal.valueOf(9), null),
        new VanillaBancoItem(Material.PLAYER_HEAD, BigDecimal.valueOf(576), new BancoItemOptions(
            "<white>Bag of Emerald Blocks</white>", 
            List.of("<gray>Holds <white>64x Emerald Blocks</whtie></gray>"), 
            1009, 
            false, 
            "http://textures.minecraft.net/texture/31d827a5decb0ae730abb69617776e1894f2bdb46968540433115d3688fbac38",
            List.of(
                new BancoItemOptions.AttributeField("minecraft:movement_speed", 0.05, AttributeModifier.Operation.ADD_NUMBER, "ANY")
            )
        ))
    );

    // Legacy items
    private List<LegacyBancoItem> items = null;

}
