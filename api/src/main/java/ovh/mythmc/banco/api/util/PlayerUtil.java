package ovh.mythmc.banco.api.util;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus.Internal;

import lombok.experimental.UtilityClass;
import ovh.mythmc.banco.api.Banco;

import java.util.UUID;

@UtilityClass
@Internal
public class PlayerUtil {

    public boolean isInBlacklistedWorld(UUID uuid) {
        final var optionalReference = Banco.get().getAccountManager().getUuidResolver().resolveOfflinePlayer(uuid);
        if (optionalReference.isEmpty() || !optionalReference.get().toOfflinePlayer().isOnline())
            return false;

        final String worldName = Bukkit.getPlayer(uuid).getWorld().getName();
        return Banco.get().getSettings().get().getCurrency().getBlacklistedWorlds().contains(worldName);
    }

}
