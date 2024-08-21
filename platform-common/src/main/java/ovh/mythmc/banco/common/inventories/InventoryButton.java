package ovh.mythmc.banco.common.inventories;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
@Getter
public abstract class InventoryButton {

    private final ItemStack icon;

    public abstract void onClick(InventoryClickEvent event);

}
