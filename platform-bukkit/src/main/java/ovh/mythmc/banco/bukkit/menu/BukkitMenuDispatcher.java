package ovh.mythmc.banco.bukkit.menu;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.bukkit.menu.impl.BalanceTopMenu;
import ovh.mythmc.banco.bukkit.menu.impl.InfoMenu;
import ovh.mythmc.banco.common.menu.MenuDispatcher;

public final class BukkitMenuDispatcher implements MenuDispatcher {

    @Override
    public void showBalanceTop(@NotNull Player player) {
        MenuManager.getInstance().openInventory(new BalanceTopMenu(), player);
    }

    @Override
    public void showInfo(@NotNull Player player) {
        MenuManager.getInstance().openInventory(new InfoMenu(), player);
    }
    
}
