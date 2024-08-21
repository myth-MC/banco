package ovh.mythmc.banco.common.commands;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.common.inventories.InventoryManager;
import ovh.mythmc.banco.common.inventories.impl.BalanceTopInventory;

import java.util.*;

public abstract class BalanceTopCommand {

    public void run(@NotNull Audience sender, @NotNull String[] args) {
        Optional<UUID> uuid = sender.get(Identity.UUID);
        if (uuid.isEmpty())
            return;

        InventoryManager.getInstance().openInventory(new BalanceTopInventory(), Objects.requireNonNull(Bukkit.getPlayer(uuid.get())));
    }

    public @NotNull Collection<String> getSuggestions(@NotNull String[] args) {
        return List.of();
    }

}
