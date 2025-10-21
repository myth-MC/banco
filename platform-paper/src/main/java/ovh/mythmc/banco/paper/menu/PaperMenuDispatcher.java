package ovh.mythmc.banco.paper.menu;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.common.menu.MenuDispatcher;
import ovh.mythmc.banco.paper.dialog.BalanceTopDialog;
import ovh.mythmc.banco.paper.dialog.InfoDialog;

public final class PaperMenuDispatcher implements MenuDispatcher {

    private final BalanceTopDialog balanceTopDialog = new BalanceTopDialog();

    private final InfoDialog infoDialog = new InfoDialog();

    @Override
    public void showBalanceTop(@NotNull Player player) {
        balanceTopDialog.open(player);
    }

    @Override
    public void showInfo(@NotNull Player player) {
        infoDialog.open(player);
    } 
    
}
