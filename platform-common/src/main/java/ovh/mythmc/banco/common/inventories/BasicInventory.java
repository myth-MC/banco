package ovh.mythmc.banco.common.inventories;

import lombok.Getter;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public abstract class BasicInventory implements InventoryHandler {

    @Getter
    private final Inventory inventory;
    private final Map<Integer, InventoryButton> inventoryButtons = new HashMap<>();

    public BasicInventory() {
        this.inventory = this.createInventory();
    }

    public void addButton(int slot, InventoryButton button) {
        this.inventoryButtons.put(slot, button);
    }

    public void decorate() {
        this.inventoryButtons.forEach((slot, button) -> this.inventory.setItem(slot, button.getIcon()));
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        InventoryButton button = this.inventoryButtons.get(event.getSlot());
        if (button != null)
            button.onClick(event);
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        this.decorate();
    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }

    protected abstract Inventory createInventory();

}
