package ovh.mythmc.banco.common.util;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class PlayerUtil {

    public static UUID getUuid(@NotNull String name) {
        if (!Bukkit.getOnlineMode()) {
            name = "OfflinePlayer:" + name;
            return UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
        }

        return Bukkit.getOfflinePlayer(name).getUniqueId();
    }

}
