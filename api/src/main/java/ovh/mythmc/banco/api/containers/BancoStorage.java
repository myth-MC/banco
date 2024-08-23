package ovh.mythmc.banco.api.containers;

import java.math.BigDecimal;
import java.util.UUID;

public interface BancoStorage {

    BigDecimal add(UUID uuid, BigDecimal amount);

    BigDecimal remove(UUID uuid, BigDecimal amount);

}
