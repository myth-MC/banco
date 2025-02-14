package ovh.mythmc.banco.api.events.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.accounts.Account;
import ovh.mythmc.banco.api.events.BancoEvent;

/**
 * Called when an account is unregistered
 */
@Getter
@Setter
@Accessors(fluent = true)
@ToString
@EqualsAndHashCode(callSuper = false)
@Deprecated(since = "1.0", forRemoval = true)
public final class BancoAccountUnregisterEvent extends BancoEvent {
    private @NotNull Account account;

    public BancoAccountUnregisterEvent(@NotNull Account account) {
        super(true);
        this.account = account;
    }
}
