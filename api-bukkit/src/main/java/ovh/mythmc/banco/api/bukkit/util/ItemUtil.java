package ovh.mythmc.banco.api.bukkit.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
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
    public static ItemStack getItemStack(final @NotNull BancoItem bancoItem, final int amount) {
        ItemStack itemStack = new ItemStack(Material.getMaterial(bancoItem.name()), amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (bancoItem.displayName() != null)
            itemMeta.setDisplayName(bancoItem.displayName());
        if (bancoItem.lore() != null)
            itemMeta.setLore(bancoItem.lore().stream().map(string -> ChatColor.RESET + string).toList());
        if (bancoItem.customModelData() != null)
            itemMeta.setCustomModelData(bancoItem.customModelData());
        if (bancoItem.glowEffect() != null && bancoItem.glowEffect())
            itemMeta.addEnchant(Enchantment.LOYALTY, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    /**
     * Gets a BancoItem matching an ItemStack's parameters
     * @param item an ItemStack to get parameters from
     * @return a BancoItem matching item's parameters
     */
    public static BancoItem getBancoItem(final @NotNull ItemStack item) {
        String materialName = item.getType().name();
        String displayName = null;
        Integer customModelData = null;
        Boolean glowEffect = null;

        if (item.hasItemMeta()) {
            if (item.getItemMeta().hasDisplayName())
                displayName = item.getItemMeta().getDisplayName();
            if (item.getItemMeta().hasCustomModelData())
                customModelData = item.getItemMeta().getCustomModelData();
            if (item.getItemMeta().hasEnchant(Enchantment.LOYALTY))
                glowEffect = true;
        }

        return Banco.get().getItemManager().get(materialName, displayName, glowEffect, customModelData);
    }

    /**
     * Checks whether an ItemStack is a valid BancoItem or not
     * @param item an ItemStack to get parameters from
     * @return true if a BancoItem matching item's parameters exists
     */
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
            if(bancoItem.value().compareTo(amount) > 0)
                continue;

            int itemAmount = (amount.divide(bancoItem.value(), RoundingMode.FLOOR)).intValue();

            if (itemAmount > 0) {
                items.add(ItemUtil.getItemStack(bancoItem, itemAmount));

                amount = amount.subtract(Banco.get().getItemManager().value(bancoItem, itemAmount));
            }
        }

        return items;
    }

}
