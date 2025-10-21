package ovh.mythmc.banco.common.menu;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface MenuDispatcher {

    void showBalanceTop(@NotNull Player player);

    void showInfo(@NotNull Player player);
    
}
