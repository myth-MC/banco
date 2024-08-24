package ovh.mythmc.banco.api.event.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.accounts.Account;
import ovh.mythmc.banco.api.event.BancoEvent;

import java.math.BigDecimal;

@Getter
@Accessors(fluent = true)
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
/**
 * Called when a transaction is made
 */
public final class BancoTransactionEvent implements BancoEvent {
    private final @NotNull Account account;
    private final @NotNull BigDecimal amount;
}
