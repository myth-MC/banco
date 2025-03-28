package ovh.mythmc.banco.common.menus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
@Getter
public abstract class MenuButton {

    private final ItemStack icon;

    public abstract void onClick(InventoryClickEvent event);

}
