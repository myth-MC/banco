package ovh.mythmc.banco.api.containers;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public interface BancoContainer<T> extends BancoStorage {

    @NotNull List<T> get(UUID uuid);

    @NotNull Integer maxSize();

}

