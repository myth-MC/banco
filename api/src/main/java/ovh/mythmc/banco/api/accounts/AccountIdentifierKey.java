package ovh.mythmc.banco.api.accounts;

import java.util.Comparator;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
public final class AccountIdentifierKey implements Comparable<AccountIdentifierKey> {

    static final Comparator<AccountIdentifierKey> COMPARATOR = Comparator.comparing(AccountIdentifierKey::uuid).thenComparing(AccountIdentifierKey::name);

    private final UUID uuid;

    private final String name;

    public static AccountIdentifierKey of(UUID uuid, String name) {
        return new AccountIdentifierKey(uuid, name);
    }

    @Override
    public int compareTo(AccountIdentifierKey that) {
        return COMPARATOR.compare(this, that);
    }
    
}
