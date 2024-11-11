package ovh.mythmc.banco.api.accounts;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ovh.mythmc.banco.api.Banco;

import java.math.BigDecimal;
import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@Data
@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor
@DatabaseTable(tableName = "accounts")
public class Account {

    @Getter(AccessLevel.PUBLIC)
    @DatabaseField(id = true)
    private UUID uuid;

    @DatabaseField(defaultValue = "0.0")
    private BigDecimal amount;

    @Setter(AccessLevel.PROTECTED)
    @DatabaseField(defaultValue = "0.0")
    private BigDecimal transactions;

    public Account(UUID uuid,
                   BigDecimal amount,
                   BigDecimal transactions) {
        this.uuid = uuid;
        this.amount = amount;
        this.transactions = transactions;
    }

    /**
     * Returns this account's balance
     * @return This account's balance
     */
    public BigDecimal amount() {
        return Banco.get().getAccountManager().amount(uuid);
    }
    
    protected void setAmount(BigDecimal amount) {
        this.amount = BigDecimal.valueOf(0).max(amount);
    }

}
