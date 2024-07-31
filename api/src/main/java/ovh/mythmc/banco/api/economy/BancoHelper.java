package ovh.mythmc.banco.api.economy;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public interface BancoHelper {

    @NotNull static BancoHelper get() { return BancoHelperSupplier.get(); }

    void add(UUID uuid, BigDecimal amount);

    void remove(UUID uuid, BigDecimal amount);

    boolean isOnline(UUID uuid);

    BigDecimal getInventoryValue(UUID uuid);

}
