package ovh.mythmc.banco.api.event.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.event.BancoEvent;
import ovh.mythmc.banco.api.inventories.BancoInventory;

@Getter
@Accessors(fluent = true)
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public final class BancoInventoryUnregisterEvent implements BancoEvent {
    private final @NotNull BancoInventory<?> bancoInventory;
}
