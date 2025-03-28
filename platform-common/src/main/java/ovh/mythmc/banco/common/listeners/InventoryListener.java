package ovh.mythmc.banco.common.listeners;

import lombok.RequiredArgsConstructor;
import ovh.mythmc.banco.common.menus.MenuManager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

@RequiredArgsConstructor
public final class InventoryListener implements Listener {

    private final MenuManager inventoryManager = MenuManager.getInstance();

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
