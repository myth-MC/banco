package ovh.mythmc.banco.api.util;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;

import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.items.BancoItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public final class ItemUtil {

    /**
     * Gets an ItemStack matching BancoItem's parameters
     * @param bancoItem BancoItem to get parameters from
     * @param amount amount of items
     * @return An ItemStack matching BancoItem's parameters
     */
    @Deprecated
    @ScheduledForRemoval
    public static ItemStack getItemStack(final @NotNull BancoItem bancoItem, final int amount) {
        return bancoItem.asItemStack(amount);
    }

    /**
     * Gets a BancoItem matching an ItemStack's parameters
     * @param item an ItemStack to get parameters from
     * @return a BancoItem matching item's parameters
     */
    @Deprecated
    @ScheduledForRemoval
    public static BancoItem getBancoItem(final @NotNull ItemStack item) {
        return Banco.get().getItemManager().get(item);
        /*
        String materialName = item.getType().name();
        String displayName = null;
        Integer customModelData = null;
        boolean glowEffect = false;

        if (item.hasItemMeta()) {
            if (item.getItemMeta().hasDisplayName())
                displayName = item.getItemMeta().getDisplayName();
            if (item.getItemMeta().hasCustomModelData())
                customModelData = item.getItemMeta().getCustomModelData();
            if (item.getItemMeta().hasEnchant(Enchantment.LOYALTY))
                glowEffect = true;
        }

        return Banco.get().getItemManager().get(materialName, displayName, glowEffect, customModelData); */
    }

    /**
     * Checks whether an ItemStack is a valid BancoItem or not
     * @param item an ItemStack to get parameters from
     * @return true if a BancoItem matching item's parameters exists
     */
    @Deprecated
    @ScheduledForRemoval
    public static boolean isBancoItem(ItemStack item) {
        return getBancoItem(item) != null;
    }

    /**
     * Gets a list of ItemStack valued at a specified amount
     * @param amount amount of money
     * @return A list of ItemStack valued at a specified amount
     */
    public static List<ItemStack> convertAmountToItems(BigDecimal amount) {
        List<ItemStack> items = new ArrayList<>();

        for (BancoItem bancoItem : Banco.get().getItemManager().get().reversed()) {
            do {
                int itemAmount = Math.min((amount.divide(bancoItem.value(), RoundingMode.FLOOR)).intValue(), 64);

                if (itemAmount > 0) {
                    items.add(bancoItem.asItemStack(itemAmount));

                    amount = amount.subtract(Banco.get().getItemManager().value(bancoItem, itemAmount));
                }
            } while (bancoItem.value().compareTo(amount) < 0);
        }

        return items;
    }

}
