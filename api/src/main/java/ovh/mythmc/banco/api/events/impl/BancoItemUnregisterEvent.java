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
 * Called when a BancoItem is unregistered
 */
@Getter
@Accessors(fluent = true)
@ToString
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
@Deprecated(since = "1.0", forRemoval = true)
public final class BancoItemUnregisterEvent extends BancoEvent {
    private final @NotNull BancoItem bancoItem;
}
