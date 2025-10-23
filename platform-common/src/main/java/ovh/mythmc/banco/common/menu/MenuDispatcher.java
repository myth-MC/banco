package ovh.mythmc.banco.common.menu;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.api.accounts.Account;

public interface MenuDispatcher {

    void showBalanceTop(@NotNull Player player);

    void showInfo(@NotNull Player player);

    void showTransactionHistory(@NotNull Player player, @NotNull Account account);
    
}
