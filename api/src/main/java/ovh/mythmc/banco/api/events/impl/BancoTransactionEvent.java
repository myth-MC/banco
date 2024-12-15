package ovh.mythmc.banco.api.events.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.accounts.Account;
import ovh.mythmc.banco.api.events.BancoEvent;

import java.math.BigDecimal;

/**
 * Called when a transaction is made
 */
@Getter
@Accessors(fluent = true)
@ToString
@EqualsAndHashCode(callSuper = false)
public final class BancoTransactionEvent extends BancoEvent {
    private @NotNull Account account;
    private @NotNull BigDecimal amount;

    public BancoTransactionEvent(@NotNull Account account, @NotNull BigDecimal amount) {
        super(true);
        this.account = account;
        this.amount = amount;
    }
}
