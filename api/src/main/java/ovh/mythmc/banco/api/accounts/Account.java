package ovh.mythmc.banco.api.accounts;

import de.exlll.configlib.SerializeWith;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import ovh.mythmc.banco.api.Banco;

import java.math.BigDecimal;
import java.util.UUID;

@SerializeWith(serializer = AccountSerializer.class)
@Getter(AccessLevel.PROTECTED)
public class Account {

    @Getter(AccessLevel.PUBLIC)
    private final UUID uuid;

    private BigDecimal amount;

    @Setter(AccessLevel.PROTECTED)
    private BigDecimal transactions;

    public Account(UUID uuid,
                   BigDecimal amount,
                   BigDecimal transactions) {
        this.uuid = uuid;
        this.amount = amount;
        this.transactions = transactions;
    }

    public BigDecimal amount() {
        return Banco.get().getAccountManager().amount(this);
    }
    
    protected void setAmount(BigDecimal amount) {
        this.amount = BigDecimal.valueOf(0).max(amount);
    }

}
