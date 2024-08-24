package ovh.mythmc.banco.api.event.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.storage.BancoStorage;
import ovh.mythmc.banco.api.event.BancoEvent;

@Getter
@Accessors(fluent = true)
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
/**
 * Called when a BancoStorage is registered
 */
public final class BancoStorageRegisterEvent implements BancoEvent {
    private final @NotNull BancoStorage bancoStorage;
}
