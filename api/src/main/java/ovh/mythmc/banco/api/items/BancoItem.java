package ovh.mythmc.banco.api.items;

import java.math.BigDecimal;

import org.bukkit.inventory.ItemStack;

import de.exlll.configlib.Polymorphic;
import de.exlll.configlib.PolymorphicTypes;
import ovh.mythmc.banco.api.items.impl.Base64BancoItem;
import ovh.mythmc.banco.api.items.impl.ItemsAdderBancoItem;
import ovh.mythmc.banco.api.items.impl.MythicMobsBancoItem;
import ovh.mythmc.banco.api.items.impl.NexoBancoItem;
import ovh.mythmc.banco.api.items.impl.NovaBancoItem;
import ovh.mythmc.banco.api.items.impl.VanillaBancoItem;
import ovh.mythmc.banco.api.items.impl.OraxenBancoItem;
import ovh.mythmc.banco.api.items.impl.SlimefunBancoItem;

@Polymorphic
@PolymorphicTypes({
    @PolymorphicTypes.Type(type = Base64BancoItem.class, alias = "base64"),
    @PolymorphicTypes.Type(type = VanillaBancoItem.class, alias = "vanilla"),
    @PolymorphicTypes.Type(type = ItemsAdderBancoItem.class, alias = "itemsadder"),
    @PolymorphicTypes.Type(type = OraxenBancoItem.class, alias = "oraxen"),
    @PolymorphicTypes.Type(type = MythicMobsBancoItem.class, alias = "mythicmobs"),
    @PolymorphicTypes.Type(type = NovaBancoItem.class, alias = "nova"),
    @PolymorphicTypes.Type(type = NexoBancoItem.class, alias = "nexo"),
    @PolymorphicTypes.Type(type = SlimefunBancoItem.class, alias = "slimefun")
})
public interface BancoItem {

    BigDecimal value();

    ItemStack asItemStack(int amount);

    default BigDecimal value(int amount) {
        return value().multiply(BigDecimal.valueOf(amount));
    }

    default boolean match(ItemStack itemStack) {
        return itemStack.isSimilar(asItemStack());
    }

    default ItemStack asItemStack() {
        return asItemStack(1);
    }
    
}
