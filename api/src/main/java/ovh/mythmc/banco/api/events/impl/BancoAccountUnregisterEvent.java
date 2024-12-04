package ovh.mythmc.banco.api.events.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.accounts.Account;
import ovh.mythmc.banco.api.events.BancoEvent;

/**
 * Called when an account is unregistered
 */
@Getter
@Accessors(fluent = true)
@ToString
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
public final class BancoAccountUnregisterEvent extends BancoEvent {
    private final @NotNull Account account;
}
