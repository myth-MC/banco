package ovh.mythmc.banco.api.accounts;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.NotNull;

/**
 * Per-account lock helper for thread-safe account operations.
 * <p>
 * This utility class provides per-account synchronization locks to ensure
 * that only one thread can update a specific account at a time. This prevents
 * race conditions when multiple threads attempt to modify the same account
 * simultaneously.
 * </p>
 * <p>
 * Usage:
 * <pre>{@code
 * UUID accountUuid = ...;
 * Object lock = AccountLocks.lockFor(accountUuid);
 * synchronized (lock) {
 *     // Perform account operations safely
 * }
 * }</pre>
 * </p>
 *
 * @since 1.3.0
 */
public final class AccountLocks {

    private static final ConcurrentHashMap<UUID, Object> locks = new ConcurrentHashMap<>();

    /**
     * Gets a lock object for the specified account UUID.
     * <p>
     * The same UUID will always return the same lock object, allowing multiple
     * threads to synchronize on the same account.
     * </p>
     *
     * @param uuid the UUID of the account
     * @return a lock object for synchronizing on this account
     * @throws IllegalArgumentException if uuid is null
     */
    @NotNull
    public static Object lockFor(@NotNull UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }
        return locks.computeIfAbsent(uuid, u -> new Object());
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private AccountLocks() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
