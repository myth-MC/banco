package ovh.mythmc.banco.api.items;

import java.math.BigDecimal;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import de.exlll.configlib.Polymorphic;
import de.exlll.configlib.PolymorphicTypes;
import net.kyori.adventure.text.Component;
import ovh.mythmc.banco.api.items.impl.Base64BancoItem;
import ovh.mythmc.banco.api.items.impl.ItemsAdderBancoItem;
import ovh.mythmc.banco.api.items.impl.MythicMobsBancoItem;
import ovh.mythmc.banco.api.items.impl.NexoBancoItem;
import ovh.mythmc.banco.api.items.impl.NovaBancoItem;
import ovh.mythmc.banco.api.items.impl.VanillaBancoItem;
import ovh.mythmc.banco.api.items.impl.OraxenBancoItem;
import ovh.mythmc.banco.api.items.impl.SlimefunBancoItem;

/**
 * Represents a currency item in the Banco system.
 * <p>
 * Currency items are used to represent money in the form of physical items.
 * Different implementations support different item types:
 * <ul>
 *   <li>Vanilla Minecraft items</li>
 *   <li>Items from custom item plugins (ItemsAdder, Oraxen, etc.)</li>
 *   <li>Base64-encoded custom items</li>
 * </ul>
 * </p>
 * <p>
 * This interface is polymorphic and supports multiple implementations through
 * the configuration system.
 * </p>
 *
 * @since 1.0.0
 */
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

    /**
     * Gets the display name of this currency item.
     *
     * @return this item's display name
     */
    @NotNull
    Component displayName();

    /**
     * Gets the value of a single unit of this currency item.
     *
     * @return the value of one item
     */
    @NotNull
    BigDecimal value();

    /**
     * Creates an ItemStack representing the specified amount of this currency item.
     *
     * @param amount the number of items to create
     * @return an ItemStack containing the specified amount
     * @throws IllegalArgumentException if amount is negative
     */
    @NotNull
    ItemStack asItemStack(int amount);

    /**
     * Gets the total value for a specified amount of this currency item.
     *
     * @param amount the number of items
     * @return the total value (value per item multiplied by amount)
     * @throws IllegalArgumentException if amount is negative
     */
    @NotNull
    default BigDecimal value(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        return value().multiply(BigDecimal.valueOf(amount));
    }

    /**
     * Checks if an ItemStack matches this currency item.
     * <p>
     * This method uses Bukkit's ItemStack similarity check to determine if
     * the provided ItemStack represents this currency item.
     * </p>
     *
     * @param itemStack the ItemStack to check
     * @return true if the ItemStack matches this currency item, false otherwise
     */
    default boolean match(@NotNull ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        return itemStack.isSimilar(asItemStack());
    }

    /**
     * Creates an ItemStack representing a single unit of this currency item.
     *
     * @return an ItemStack containing one item
     */
    @NotNull
    default ItemStack asItemStack() {
        return asItemStack(1);
    }
}
