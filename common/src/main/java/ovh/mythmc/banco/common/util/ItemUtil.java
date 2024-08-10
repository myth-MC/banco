package ovh.mythmc.banco.common.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.economy.BancoItem;

public final class ItemUtil {

    public static ItemStack getItemStack(final @NotNull BancoItem bancoItem, final int amount) {
        ItemStack itemStack = new ItemStack(Material.getMaterial(bancoItem.name()), amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (bancoItem.displayName() != null)
            itemMeta.setDisplayName(bancoItem.displayName());
        if (bancoItem.lore() != null)
            itemMeta.setLore(bancoItem.lore().stream().map(string -> ChatColor.RESET + string).toList());
        if (bancoItem.customModelData() != null)
            itemMeta.setCustomModelData(bancoItem.customModelData());
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static BancoItem getBancoItem(final @NotNull ItemStack item) {
        String materialName = item.getType().name();
        String displayName = null;
        Integer customModelData = null;

        if (item.hasItemMeta()) {
            displayName = item.getItemMeta().getDisplayName();
            if (item.getItemMeta().hasCustomModelData())
                customModelData = item.getItemMeta().getCustomModelData();
        }

        return Banco.get().getEconomyManager().get(materialName, displayName, customModelData);
    }

    public static boolean isBancoItem(ItemStack item) {
        return getBancoItem(item) != null;
    }

}
