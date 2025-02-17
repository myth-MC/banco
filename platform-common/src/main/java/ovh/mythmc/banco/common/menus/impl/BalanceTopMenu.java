package ovh.mythmc.banco.common.menus.impl;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.common.menus.BasicMenu;
import ovh.mythmc.banco.common.menus.MenuButton;
import ovh.mythmc.banco.common.util.MessageUtil;
import ovh.mythmc.banco.common.util.PlayerUtil;

import java.math.BigDecimal;
import java.util.*;

public final class BalanceTopMenu extends BasicMenu {

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 9, Banco.get().getSettings().get().getMenus().getBalanceTop().title());
    }

    @Override
    public void decorate() {
        Banco.get().getAccountManager().getTopAsync(1024000).thenAccept(map -> {
            int slot = 0;
            
            for (Map.Entry<UUID, BigDecimal> entry : map.entrySet()) {
                if (slot >= 8)
                    break;
    
                OfflinePlayer player = PlayerUtil.getOfflinePlayerByUuid(entry.getKey());
                if (player == null || !player.hasPlayedBefore())
                    continue;
                    
                String balance = MessageUtil.format(entry.getValue()) + Banco.get().getSettings().get().getCurrency().getSymbol();
    
                String itemName = String.format(Banco.get().getSettings().get().getMenus().getBalanceTop().format(),
                        slot+1,
                        player.getName(),
                        balance
                );
    
                ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
                skullMeta.setOwningPlayer(player);
                skullMeta.setDisplayName(itemName);
                itemStack.setItemMeta(skullMeta);
    
                MenuButton button = new MenuButton(itemStack) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        // ignored
                    }
                };
    
                this.addButton(slot, button);
    
                slot = slot + 1;
            }
        
            super.decorate();
            update();
        });
    }
}
