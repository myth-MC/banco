package ovh.mythmc.banco.common.util;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import lombok.experimental.UtilityClass;
import ovh.mythmc.banco.api.Banco;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

@UtilityClass
public class PlayerUtil {

    public UUID getUuid(@NotNull String name) {
        UUID uuid = null;

        if (Bukkit.getOnlineMode() && getOfflinePlayerByName(name) != null)
            uuid = Objects.requireNonNull(getOfflinePlayerByName(name)).getUniqueId();

        if (uuid == null) {
            name = "OfflinePlayer:" + name;
            uuid = UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
        }

        return uuid;
    }

    private OfflinePlayer getOfflinePlayerByName(String username) {
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers())
            if (Objects.equals(offlinePlayer.getName(), username))
                return offlinePlayer;

        return null;
    }

    public boolean isInBlacklistedWorld(UUID uuid) {
        if (!Bukkit.getOfflinePlayer(uuid).isOnline())
            return false;

        String worldName = Bukkit.getPlayer(uuid).getWorld().getName();
        return Banco.get().getSettings().get().getCurrency().getBlacklistedWorlds().contains(worldName);
    }

}
