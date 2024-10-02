package ovh.mythmc.banco.common.inventories.impl;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.common.inventories.BasicInventory;
import ovh.mythmc.banco.common.inventories.InventoryButton;
import ovh.mythmc.banco.common.util.MessageUtil;

import java.math.BigDecimal;
import java.util.*;

public final class BalanceTopInventory extends BasicInventory {

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 9, Banco.get().getSettings().get().getInventories().getBalanceTop().title());
    }

    @Override
    public void decorate() {
        int slot = 0;

        for (Map.Entry<UUID, BigDecimal> entry : Banco.get().getAccountManager().getTop(9).entrySet()) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());
            if (!player.hasPlayedBefore())
                continue;
            String balance = MessageUtil.format(entry.getValue()) + Banco.get().getSettings().get().getCurrency().getSymbol();

            String itemName = String.format(Banco.get().getSettings().get().getInventories().getBalanceTop().format(),
                    slot+1,
                    player.getName(),
                    balance
            );

            ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
            skullMeta.setOwningPlayer(player);
            skullMeta.setDisplayName(itemName);
            itemStack.setItemMeta(skullMeta);

            InventoryButton button = new InventoryButton(itemStack) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    // ignored
                }
            };

            this.addButton(slot, button);

            slot = slot + 1;
        }


        super.decorate();
    }
}
