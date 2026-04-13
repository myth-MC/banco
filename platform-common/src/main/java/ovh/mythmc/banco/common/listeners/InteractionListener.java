package ovh.mythmc.banco.common.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.items.BancoItem;
import ovh.mythmc.banco.api.util.ItemUtil;

public class InteractionListener implements Listener {

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        ItemStack firstItem = event.getInventory().getItem(0);
        BancoItem bancoItem = Banco.get().getItemRegistry().getByItemStack(firstItem);
        if (bancoItem == null)
            return;

        if (!ItemUtil.isInteractive(bancoItem))
            event.setResult(null);
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        if (event.getInventory().isEmpty())
            return;

        for (ItemStack itemStack : event.getInventory()) {
            if (itemStack == null || itemStack.getType() == Material.AIR)
                return;

            BancoItem bancoItem = Banco.get().getItemRegistry().getByItemStack(itemStack);
            if (bancoItem == null)
                return;

            if (!ItemUtil.isInteractive(bancoItem)) {
                event.getInventory().setResult(null);
                break;
            }
        }
    }

    @EventHandler
    public void onPrepareItemEnchant(PrepareItemEnchantEvent event) {
        BancoItem bancoItem = Banco.get().getItemRegistry().getByItemStack(event.getItem());
        if (bancoItem == null) 
            return;

        if (!ItemUtil.isInteractive(bancoItem)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        BancoItem bancoItem = Banco.get().getItemRegistry().getByItemStack(event.getItem());
        if (bancoItem == null)
            return;

        if (!ItemUtil.isInteractive(bancoItem)) {
            event.setCancelled(true);
        }
    }

    @EventHandler 
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        ItemStack itemStack = event.getPlayer().getItemInUse();
        BancoItem bancoItem = Banco.get().getItemRegistry().getByItemStack(itemStack);
        if (bancoItem == null)
            return;

        if (!ItemUtil.isInteractive(bancoItem)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        BancoItem bancoItem = Banco.get().getItemRegistry().getByItemStack(event.getItem());
        if (bancoItem == null)
            return;

        if (!ItemUtil.isInteractive(bancoItem)) {
            event.setCancelled(true);
        }
    }   
    
}
