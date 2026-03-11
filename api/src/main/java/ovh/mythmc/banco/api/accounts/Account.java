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
import org.jetbrains.annotations.Nullable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Represents a player account in the Banco system.
 * <p>
 * An account stores information about a player's financial status, including:
 * <ul>
 *   <li>UUID - The unique identifier for the player</li>
 *   <li>Name - The player's name (may be null for older accounts)</li>
 *   <li>Amount - The base balance stored in the database</li>
 *   <li>Transactions - Pending transactions that haven't been processed yet</li>
 * </ul>
 * </p>
 * <p>
 * The actual balance of an account is calculated dynamically and includes:
 * <ul>
 *   <li>The base amount</li>
 *   <li>Pending transactions</li>
 *   <li>Value stored in all registered storage systems</li>
 * </ul>
 * </p>
 *
 * @since 1.0.0
 */
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
     * Gets this account's current balance.
     * <p>
     * The balance is calculated dynamically and includes the base amount,
     * pending transactions, and value stored in all registered storage systems.
     * </p>
     *
     * @return the account's current balance
     */
    @NotNull
    public BigDecimal balance() {
        return Banco.get().getAccountManager().balance(uuid);
    }

    /**
     * Gets this account's current balance.
     * <p>
     * The balance is calculated dynamically and includes the base amount,
     * pending transactions, and value stored in all registered storage systems.
     * </p>
     *
     * @deprecated As of banco-api v1.3.0, use {@link #balance()} instead
     * @return the account's current balance
     */
    @NotNull
    public BigDecimal amount() {
        return balance();
    }

    /**
     * Gets the identifier key for this account.
     * <p>
     * The identifier key combines the UUID and name to uniquely identify the account.
     * </p>
     *
     * @return the account identifier key
     */
    @NotNull
    public AccountIdentifierKey getIdentifier() {
        return AccountIdentifierKey.of(uuid, name);
    }

    /**
     * Sets the base amount for this account.
     * <p>
     * The amount is automatically clamped to a minimum of zero (no negative balances).
     * </p>
     *
     * @param amount the new base amount (will be clamped to minimum of zero)
     */
    protected void setAmount(@Nullable BigDecimal amount) {
        if (amount == null) {
            this.amount = BigDecimal.ZERO;
        } else {
            this.amount = BigDecimal.ZERO.max(amount);
        }
    }
}
