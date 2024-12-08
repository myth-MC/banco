package ovh.mythmc.banco.api.events.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.api.events.BancoEvent;
import ovh.mythmc.banco.api.items.BancoItem;

/**
 * Called when a BancoItem is registered
 */
@Getter
@Accessors(fluent = true)
@ToString
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
public final class BancoItemRegisterEvent extends BancoEvent {
    private final @NotNull BancoItem bancoItem;
}
