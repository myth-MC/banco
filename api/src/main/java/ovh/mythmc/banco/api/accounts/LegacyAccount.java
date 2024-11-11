package ovh.mythmc.banco.api.accounts;

import de.exlll.configlib.SerializeWith;
import lombok.AccessLevel;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@SerializeWith(serializer = LegacyAccountSerializer.class)
@Getter(AccessLevel.PUBLIC)
public class LegacyAccount {

    private final UUID uuid;

    private BigDecimal amount;

    private BigDecimal transactions;

    public LegacyAccount(UUID uuid,
                   BigDecimal amount,
                   BigDecimal transactions) {
        this.uuid = uuid;
        this.amount = amount;
        this.transactions = transactions;
    }

}