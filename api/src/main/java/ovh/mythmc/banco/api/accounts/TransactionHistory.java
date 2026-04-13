package ovh.mythmc.banco.api.accounts;

import java.util.List;

import org.jetbrains.annotations.NotNull;

/**
 * Manages the transaction history for accounts.
 * <p>
 * This interface provides methods for storing and retrieving transaction history
 * for accounts. Transaction history is used to track all financial operations
 * performed on accounts.
 * </p>
 *
 * @since 1.2.0
 */
public interface TransactionHistory {

    /**
     * Gets the transaction history for an account identified by its identifier key.
     *
     * @param accountIdentifierKey the account identifier key
     * @return an unmodifiable list of transactions, ordered from oldest to newest
     * @throws IllegalArgumentException if accountIdentifierKey is null
     */
    @NotNull
    List<Transaction.ImmutableView> get(@NotNull AccountIdentifierKey accountIdentifierKey);

    /**
     * Gets the transaction history for an account.
     *
     * @param account the account
     * @return an unmodifiable list of transactions, ordered from oldest to newest
     * @throws IllegalArgumentException if account is null
     */
    @NotNull
    default List<Transaction.ImmutableView> get(@NotNull Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }
        return get(account.getIdentifier());
    }

    /**
     * Registers a transaction in the history for an account.
     *
     * @param accountIdentifierKey the account identifier key
     * @param transaction the transaction to register
     * @throws IllegalArgumentException if accountIdentifierKey or transaction is null
     */
    void register(@NotNull AccountIdentifierKey accountIdentifierKey, @NotNull Transaction.ImmutableView transaction);

    /**
     * Registers a transaction in the history for an account.
     * <p>
     * The transaction will be converted to an immutable view before being stored.
     * </p>
     *
     * @param accountIdentifierKey the account identifier key
     * @param transaction the transaction to register
     * @throws IllegalArgumentException if accountIdentifierKey or transaction is null
     */
    default void register(@NotNull AccountIdentifierKey accountIdentifierKey, @NotNull Transaction transaction) {
        if (accountIdentifierKey == null) {
            throw new IllegalArgumentException("Account identifier key cannot be null");
        }
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        register(accountIdentifierKey, transaction.asImmutable());
    }
}
