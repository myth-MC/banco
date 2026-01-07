package ovh.mythmc.banco.api.accounts;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Per-account lock helper.
 *
 * Call {@code lockFor(uuid)} and synchronize on the returned object to make sure
 * only one thread updates that account at a time.
 */
public final class AccountLocks {

    private static final ConcurrentHashMap<UUID, Object> locks = new ConcurrentHashMap<>();

    public static Object lockFor(UUID uuid) {
        return locks.computeIfAbsent(uuid, u -> new Object());
    }

    private AccountLocks() {}

}
