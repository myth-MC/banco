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
 * Called when an account is registered
 */
@Getter
@Setter
@Accessors(fluent = true)
@ToString
@EqualsAndHashCode(callSuper = false)
@Deprecated(since = "1.0", forRemoval = true)
public final class BancoAccountRegisterEvent extends BancoEvent {
    private @NotNull Account account;

    public BancoAccountRegisterEvent(@NotNull Account account) {
        super(true);
        this.account = account;
    }
}
