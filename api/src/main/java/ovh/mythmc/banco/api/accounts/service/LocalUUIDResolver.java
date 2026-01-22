package ovh.mythmc.banco.api.accounts.service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

/**
 * Interface for resolving player UUIDs and offline player references.
 * <p>
 * This interface provides methods for:
 * <ul>
 *   <li>Resolving usernames to UUIDs</li>
 *   <li>Resolving UUIDs to offline player references</li>
 *   <li>Getting all cached offline player references</li>
 * </ul>
 * </p>
 *
 * @since 1.0.0
 */
public interface LocalUUIDResolver {

    /**
     * Gets all cached offline player references.
     *
     * @return an unmodifiable set of all cached offline player references
     */
    @NotNull
    Set<OfflinePlayerReference> references();

    /**
     * Resolves a username to a UUID.
     *
     * @param username the username to resolve
     * @return an Optional containing the UUID if found, empty otherwise
     * @throws IllegalArgumentException if username is null or empty
     */
    @NotNull
    Optional<UUID> resolve(@NotNull String username);

    /**
     * Resolves a UUID to an offline player reference.
     *
     * @param uuid the UUID to resolve
     * @return an Optional containing the offline player reference if found, empty otherwise
     * @throws IllegalArgumentException if uuid is null
     */
    @NotNull
    Optional<OfflinePlayerReference> resolveOfflinePlayer(@NotNull UUID uuid);
}
