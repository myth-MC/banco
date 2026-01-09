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

import org.jetbrains.annotations.NotNull;

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

    @DatabaseField(defaultValue = "0.0", columnName = "amount")
    private BigDecimal balance;

    @DatabaseField(defaultValue = "0.0")
    private BigDecimal transactions;

    public @NotNull BigDecimal balance() {
        // Fetch the computed amount from the account manager
        BigDecimal computed = Banco.get().getAccountManager().balance(uuid);

        if (this.name == null || "NULL".equalsIgnoreCase(this.name)) {
            return BigDecimal.valueOf(0).max(computed);
        }

        return computed;
    }

    /**
     * Returns this account's balance
     * @return This account's balance
     * @deprecated As of version 1.3.0, use {@link #balance()} instead.
     */
    public BigDecimal amount() {
        return balance();
    }

    public AccountIdentifierKey getIdentifier() {
        return AccountIdentifierKey.of(uuid, name);
    }
    
    protected void setBalance(BigDecimal amount) {
        this.balance = BigDecimal.valueOf(0).max(amount);
    }

}
