package ovh.mythmc.banco.api.economy;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

@ApiStatus.Internal
public interface BancoHelper {

    @NotNull static BancoHelper get() { return BancoHelperSupplier.get(); }

    boolean isOnline(UUID uuid);

    BigDecimal getValue(UUID uuid);

}
