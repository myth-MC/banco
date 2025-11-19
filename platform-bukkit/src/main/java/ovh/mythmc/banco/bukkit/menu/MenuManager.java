package ovh.mythmc.banco.bukkit.menu;

import lombok.Getter;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public final class MenuManager {

    @Getter
    private static final MenuManager instance = new MenuManager();

    private final Map<Inventory, MenuHandler> inventories = new HashMap<>();

    public void registerInventory(Inventory inventory, MenuHandler inventoryHandler) {
        this.inventories.put(inventory, inventoryHandler);
    }

    public void unregisterInventory(Inventory inventory) {
        this.inventories.remove(inventory);
    }
    
    public void openInventory(BasicMenu inventory, Player player) {
        this.registerInventory(inventory.getInventory(), inventory);
        player.openInventory(inventory.getInventory());
    }

    public void handleClick(InventoryClickEvent event) {
        MenuHandler inventoryHandler = this.inventories.get(event.getInventory());
        if (inventoryHandler != null)
            inventoryHandler.onClick(event);
    }

    public void handleOpen(InventoryOpenEvent event) {
        MenuHandler inventoryHandler = this.inventories.get(event.getInventory());
        if (inventoryHandler != null)
            inventoryHandler.onOpen(event);
    }

    public void handleClose(InventoryCloseEvent event) {
        MenuHandler inventoryHandler = this.inventories.get(event.getInventory());
        if (inventoryHandler != null) {
            inventoryHandler.onClose(event);
            inventories.remove(event.getInventory());
        }
    }

}
