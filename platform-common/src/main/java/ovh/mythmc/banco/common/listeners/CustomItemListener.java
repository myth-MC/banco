package ovh.mythmc.banco.common.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;

import ovh.mythmc.banco.api.Banco;

public class CustomItemListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getItem() == null || !event.getItem().hasItemMeta())
            return;

        PersistentDataContainer persistentDataContainer = event.getItem().getItemMeta().getPersistentDataContainer();
        if (persistentDataContainer.has(Banco.get().getItemRegistry().CUSTOM_ITEM_IDENTIFIER_KEY))
            event.setCancelled(true);
    }
    
}
