package ovh.mythmc.banco.api.storage;

import java.math.BigDecimal;
import java.util.UUID;

public interface BancoStorage {

    /**
     *
     * @param uuid UUID of the account where items will be added
     * @param amount amount of money to add to this BancoStorage
     * @return Total amount of money that has been added
     */
    BigDecimal add(UUID uuid, BigDecimal amount);

    /**
     *
     * @param uuid UUID of the account where items will be removed
     * @param amount amount of money to remove from this BancoStorage
     * @return Amount of money that has not been removed
     */
    BigDecimal remove(UUID uuid, BigDecimal amount);

}
