package ovh.mythmc.banco.api.accounts.service;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public final class OfflinePlayerReference {

    public static OfflinePlayerReference from(final @NotNull OfflinePlayer offlinePlayer) {
        return new OfflinePlayerReference(offlinePlayer.getUniqueId(), offlinePlayer.getName());
    }

    private final UUID uuid;

    private final String name;

    OfflinePlayerReference(final @NotNull UUID uuid, final @NotNull String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public OfflinePlayer toOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    public UUID uuid() {
        return this.uuid;
    }

    public String name() {
        return this.name;
    }
    
}
