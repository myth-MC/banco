package ovh.mythmc.banco.common.commands;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import ovh.mythmc.banco.common.menus.MenuManager;
import ovh.mythmc.banco.common.menus.impl.BalanceTopMenu;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class BalanceTopCommand {

    public void run(@NotNull Audience sender, @NotNull String[] args) {
        Optional<UUID> uuid = sender.get(Identity.UUID);
        if (uuid.isEmpty())
            return;

        MenuManager.getInstance().openInventory(new BalanceTopMenu(), Objects.requireNonNull(Bukkit.getPlayer(uuid.get())));
    }

    public @NotNull Collection<String> getSuggestions(@NotNull String[] args) {
        return List.of();
    }

}
