package ovh.mythmc.banco.common.util;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

public class PlayerUtil {


    public static UUID getUuid(@NotNull String name) {
        UUID uuid = null;
        Player player = Bukkit.getPlayer(name);
        if(player != null)
        {
            uuid = player.getUniqueId();
        } else if (Bukkit.getOnlineMode() && getOfflinePlayerByName(name) != null) {
            uuid = Objects.requireNonNull(getOfflinePlayerByName(name)).getUniqueId();

        }

        if (uuid == null) {
            name = "OfflinePlayer:" + name;
            uuid = UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
        }

        return uuid;
    }

    private static OfflinePlayer getOfflinePlayerByName(String username) {
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers())
            if (Objects.equals(offlinePlayer.getName(), username))
                return offlinePlayer;

        return null;
    }

}
