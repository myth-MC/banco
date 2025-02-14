package ovh.mythmc.banco.api.events.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.accounts.Transaction;
import ovh.mythmc.banco.api.events.BancoEvent;

/**
 * Called when a transaction is made
 */
@Getter
@Accessors(fluent = true)
@ToString
@EqualsAndHashCode(callSuper = false)
@Deprecated(since = "1.0", forRemoval = true)
public final class BancoTransactionEvent extends BancoEvent {
    private @NotNull Transaction transaction;

    public BancoTransactionEvent(@NotNull Transaction transaction) {
        super(true);
        this.transaction = transaction;
    }
}
