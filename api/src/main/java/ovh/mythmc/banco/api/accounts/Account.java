package ovh.mythmc.banco.api.accounts;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@DatabaseTable(tableName = "accounts")
public class Account {

    @Getter(AccessLevel.PUBLIC)
    @DatabaseField(id = true)
    private UUID uuid;

    @Getter(AccessLevel.PUBLIC)
    @DatabaseField
    private String name;

    @DatabaseField(defaultValue = "0.0")
    private BigDecimal amount;

    @DatabaseField(defaultValue = "0.0")
    private BigDecimal transactions;

    /**
     * Returns this account's balance
     * @return This account's balance
     */
    public BigDecimal amount() {
        // Fetch the computed amount from the account manager
        BigDecimal computed = Banco.get().getAccountManager().amount(uuid);

        if (this.name == null || "NULL".equalsIgnoreCase(this.name)) {
            return BigDecimal.valueOf(0).max(computed);
        }

        return computed;
    }

    public AccountIdentifierKey getIdentifier() {
        return AccountIdentifierKey.of(uuid, name);
    }
    
    protected void setAmount(BigDecimal amount) {
        this.amount = BigDecimal.valueOf(0).max(amount);
    }

}
