package ovh.mythmc.banco.api.items;

import java.math.BigDecimal;

import org.bukkit.inventory.ItemStack;

import de.exlll.configlib.Polymorphic;
import de.exlll.configlib.PolymorphicTypes;
import ovh.mythmc.banco.api.items.impl.ItemsAdderBancoItem;
import ovh.mythmc.banco.api.items.impl.MythicMobsBancoItem;
import ovh.mythmc.banco.api.items.impl.NovaBancoItem;
import ovh.mythmc.banco.api.items.impl.OraxenBancoItem;
import ovh.mythmc.banco.api.items.impl.VanillaBancoItem;

@Polymorphic
@PolymorphicTypes({
    @PolymorphicTypes.Type(type = VanillaBancoItem.class, alias = "vanilla"),
    @PolymorphicTypes.Type(type = ItemsAdderBancoItem.class, alias = "itemsadder"),
    @PolymorphicTypes.Type(type = OraxenBancoItem.class, alias = "oraxen"),
    @PolymorphicTypes.Type(type = MythicMobsBancoItem.class, alias = "mythicmobs"),
    @PolymorphicTypes.Type(type = NovaBancoItem.class, alias = "nova")
})
public interface BancoItem {

    BigDecimal value();

    ItemStack asItemStack(int amount);

    default boolean match(ItemStack itemStack) {
        return itemStack.isSimilar(asItemStack());
    }

    default ItemStack asItemStack() {
        return asItemStack(1);
    }
    
}
