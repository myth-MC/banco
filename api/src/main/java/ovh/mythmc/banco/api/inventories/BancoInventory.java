package ovh.mythmc.banco.api.inventories;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public interface BancoInventory<T> {

    @NotNull T get(UUID uuid);

    BigDecimal add(UUID uuid, BigDecimal amount);

    BigDecimal remove(UUID uuid, BigDecimal amount);

}

