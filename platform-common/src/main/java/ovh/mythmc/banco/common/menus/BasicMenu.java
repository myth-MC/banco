package ovh.mythmc.banco.common.menus;

import lombok.Getter;
import ovh.mythmc.banco.api.Banco;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public abstract class BasicMenu implements MenuHandler {

    @Getter
    private final Inventory inventory;
    private final Map<Integer, MenuButton> inventoryButtons = new HashMap<>();

    public BasicMenu() {
        this.inventory = this.createInventory();
    }

    public void addButton(int slot, MenuButton button) {
        this.inventoryButtons.put(slot, button);
    }

    public void decorate() {
        this.inventoryButtons.forEach((slot, button) -> this.inventory.setItem(slot, button.getIcon()));
    }

    public void update() {
        inventory.getViewers().forEach(viewer -> {
            Banco.get().getLogger().info(viewer + "");
            viewer.closeInventory();
            viewer.openInventory(getInventory());
        });
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        MenuButton button = this.inventoryButtons.get(event.getSlot());
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
