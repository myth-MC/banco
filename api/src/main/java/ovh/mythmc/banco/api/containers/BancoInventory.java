package ovh.mythmc.banco.api.containers;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface BancoInventory<T> extends BancoStorage {

    @NotNull T get(UUID uuid);

}

