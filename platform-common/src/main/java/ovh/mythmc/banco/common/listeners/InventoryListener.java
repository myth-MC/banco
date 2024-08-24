package ovh.mythmc.banco.common.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import ovh.mythmc.banco.common.inventories.InventoryManager;

@RequiredArgsConstructor
public final class InventoryListener implements Listener {

    private final InventoryManager inventoryManager = InventoryManager.getInstance();

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        inventoryManager.handleClick(event);
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        inventoryManager.handleOpen(event);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        inventoryManager.handleClose(event);
    }

}
