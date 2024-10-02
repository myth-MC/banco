package ovh.mythmc.banco.api.economy;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.storage.BancoStorage;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.UUID;

@ApiStatus.Internal
public interface BancoHelper {

    @NotNull static BancoHelper get() { return BancoHelperSupplier.get(); }

    boolean isOnline(UUID uuid);

    boolean isInBlacklistedWorld(UUID uuid);

    BigDecimal getValue(UUID uuid, Collection<BancoStorage> bancoStorages);

    BigDecimal getValue(UUID uuid);

}
