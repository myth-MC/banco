package ovh.mythmc.banco.api.storage;

import java.math.BigDecimal;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a storage system for holding currency items.
 * <p>
 * Storage systems are responsible for managing the physical storage of currency
 * items for players. Different storage implementations may support different
 * features, such as offline player support.
 * </p>
 * <p>
 * Storage systems are registered with the {@link BancoStorageRegistry} and
 * are processed in order when calculating account balances or performing
 * transactions.
 * </p>
 *
 * @since 1.0.0
 */
public interface BancoStorage {

    /**
     * Gets a friendly name for this storage system.
     * <p>
     * This name is used in configuration and logging to identify the storage system.
     * </p>
     *
     * @return the friendly name of this storage system
     */
    @NotNull
    default String friendlyName() {
        return "OTHER";
    }

    /**
     * Checks whether this storage system supports offline players.
     * <p>
     * Storage systems that support offline players can be used to store or
     * retrieve currency even when the player is not online.
     * </p>
     *
     * @return true if offline players are supported, false otherwise
     */
    default boolean supportsOfflinePlayers() {
        return false;
    }

    /**
     * Gets the total value stored in this storage system for a player.
     *
     * @param uuid the UUID of the player
     * @return the total value stored, or zero if none
     * @throws IllegalArgumentException if uuid is null
     */
    @NotNull
    BigDecimal value(@NotNull UUID uuid);

    /**
     * Adds currency to this storage system for a player.
     * <p>
     * This method attempts to add as much of the specified amount as possible.
     * The returned value represents the amount that was successfully added.
     * </p>
     *
     * @param uuid the UUID of the player
     * @param amount the amount to add
     * @return the amount that was successfully added
     * @throws IllegalArgumentException if uuid is null or amount is null
     */
    @NotNull
    BigDecimal add(@NotNull UUID uuid, @NotNull BigDecimal amount);

    /**
     * Removes currency from this storage system for a player.
     * <p>
     * This method attempts to remove as much of the specified amount as possible.
     * The returned value represents the amount that could not be removed
     * (i.e., the remaining amount that still needs to be removed).
     * </p>
     *
     * @param uuid the UUID of the player
     * @param amount the amount to remove
     * @return the amount that could not be removed (zero if all was removed)
     * @throws IllegalArgumentException if uuid is null or amount is null
     */
    @NotNull
    BigDecimal remove(@NotNull UUID uuid, @NotNull BigDecimal amount);
}
