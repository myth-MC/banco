package ovh.mythmc.banco.api.event.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.storage.BancoStorage;
import ovh.mythmc.banco.api.event.BancoEvent;

/**
 * Called when a BancoStorage is unregistered
 */
@Getter
@Accessors(fluent = true)
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public final class BancoStorageUnregisterEvent implements BancoEvent {
    private final @NotNull BancoStorage bancoStorage;
}
