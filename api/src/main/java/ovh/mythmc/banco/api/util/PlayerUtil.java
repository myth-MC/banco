package ovh.mythmc.banco.api.util;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

import lombok.experimental.UtilityClass;
import ovh.mythmc.banco.api.Banco;

import java.util.UUID;

/**
 * Utility class for player-related operations.
 * <p>
 * This class provides internal utility methods for checking player states
 * and world restrictions.
 * </p>
 *
 * @since 1.0.0
 */
@UtilityClass
@Internal
public class PlayerUtil {

    /**
     * Checks if a player is currently in a blacklisted world.
     * <p>
     * Blacklisted worlds are configured in the plugin settings and typically
     * prevent currency operations from occurring.
     * </p>
     *
     * @param uuid the UUID of the player to check
     * @return true if the player is online and in a blacklisted world, false otherwise
     * @throws IllegalArgumentException if uuid is null
     */
    public boolean isInBlacklistedWorld(@NotNull UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }

        final var optionalReference = Banco.get().getAccountManager().getUuidResolver().resolveOfflinePlayer(uuid);
        if (optionalReference.isEmpty()) {
            return false;
        }

        final var offlinePlayer = optionalReference.get().toOfflinePlayer();
        if (!offlinePlayer.isOnline()) {
            return false;
        }

        final var player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return false;
        }

        final String worldName = player.getWorld().getName();
        return Banco.get().getSettings().get().getCurrency().getBlacklistedWorlds().contains(worldName);
    }
}
