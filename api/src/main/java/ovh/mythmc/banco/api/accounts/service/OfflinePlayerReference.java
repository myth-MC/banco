package ovh.mythmc.banco.api.accounts.service;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a reference to an offline player.
 * <p>
 * This class provides a lightweight representation of an offline player that
 * can be cached and used for UUID resolution without requiring the full
 * OfflinePlayer object to be loaded.
 * </p>
 *
 * @since 1.0.0
 */
public final class OfflinePlayerReference {

    private final UUID uuid;
    private final String name;

    /**
     * Creates an OfflinePlayerReference from an OfflinePlayer.
     *
     * @param offlinePlayer the offline player
     * @return a new OfflinePlayerReference
     * @throws IllegalArgumentException if offlinePlayer is null
     */
    @NotNull
    public static OfflinePlayerReference from(final @NotNull OfflinePlayer offlinePlayer) {
        if (offlinePlayer == null) {
            throw new IllegalArgumentException("Offline player cannot be null");
        }
        return new OfflinePlayerReference(offlinePlayer.getUniqueId(), offlinePlayer.getName());
    }

    /**
     * Creates a new OfflinePlayerReference.
     *
     * @param uuid the UUID of the player
     * @param name the name of the player (may be null)
     * @throws IllegalArgumentException if uuid is null
     */
    OfflinePlayerReference(final @NotNull UUID uuid, final @Nullable String name) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }
        this.uuid = uuid;
        this.name = name;
    }

    /**
     * Converts this reference to a Bukkit OfflinePlayer.
     *
     * @return the OfflinePlayer instance
     */
    @NotNull
    public OfflinePlayer toOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    /**
     * Gets the UUID of the player.
     *
     * @return the UUID
     */
    @NotNull
    public UUID uuid() {
        return this.uuid;
    }

    /**
     * Gets the name of the player.
     *
     * @return the name, or null if unknown
     */
    @Nullable
    public String name() {
        return this.name;
    }
}
