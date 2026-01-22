package ovh.mythmc.banco.api.accounts;

import java.util.Comparator;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Represents a unique identifier for an account.
 * <p>
 * An account identifier combines a UUID and an optional name to uniquely identify
 * an account. This allows for lookup by either UUID or name.
 * </p>
 * <p>
 * This class is immutable and implements {@link Comparable} for use in sorted collections.
 * </p>
 *
 * @since 1.0.0
 */
@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
public final class AccountIdentifierKey implements Comparable<AccountIdentifierKey> {

    /**
     * Comparator for AccountIdentifierKey that compares by UUID first, then by name.
     */
    static final Comparator<AccountIdentifierKey> COMPARATOR = Comparator
        .comparing(AccountIdentifierKey::uuid)
        .thenComparing(AccountIdentifierKey::name, Comparator.nullsLast(String::compareTo));

    private final UUID uuid;
    private final String name;

    /**
     * Creates a new account identifier key.
     *
     * @param uuid the UUID of the account (must not be null)
     * @param name the name of the account (may be null)
     * @return a new AccountIdentifierKey
     * @throws IllegalArgumentException if uuid is null
     */
    @NotNull
    public static AccountIdentifierKey of(@NotNull UUID uuid, @Nullable String name) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }
        return new AccountIdentifierKey(uuid, name);
    }

    @Override
    public int compareTo(@NotNull AccountIdentifierKey that) {
        if (that == null) {
            throw new IllegalArgumentException("Cannot compare to null");
        }
        return COMPARATOR.compare(this, that);
    }
}
