package ovh.mythmc.banco.common.inventories;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public final class InventoryManager {

    @Getter
    private static final InventoryManager instance = new InventoryManager();

    private final Map<Inventory, InventoryHandler> inventories = new HashMap<>();

    public void registerInventory(Inventory inventory, InventoryHandler inventoryHandler) {
        this.inventories.put(inventory, inventoryHandler);
    }

    public void unregisterInventory(Inventory inventory) {
        this.inventories.remove(inventory);
    }

    public void openInventory(BasicInventory inventory, Player player) {
        this.registerInventory(inventory.getInventory(), inventory);
        player.openInventory(inventory.getInventory());
    }

    public void handleClick(InventoryClickEvent event) {
        InventoryHandler inventoryHandler = this.inventories.get(event.getInventory());
        if (inventoryHandler != null)
            inventoryHandler.onClick(event);
    }

    public void handleOpen(InventoryOpenEvent event) {
        InventoryHandler inventoryHandler = this.inventories.get(event.getInventory());
        if (inventoryHandler != null)
            inventoryHandler.onOpen(event);
    }

    public void handleClose(InventoryCloseEvent event) {
        InventoryHandler inventoryHandler = this.inventories.get(event.getInventory());
        if (inventoryHandler != null) {
            inventoryHandler.onClose(event);
            inventories.remove(event.getInventory());
        }
    }

}
