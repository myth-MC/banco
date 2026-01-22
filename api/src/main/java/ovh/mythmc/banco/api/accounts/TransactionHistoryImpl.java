package ovh.mythmc.banco.api.accounts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

/**
 * Default implementation of {@link TransactionHistory}.
 * <p>
 * This implementation stores transaction history in memory using a HashMap.
 * Transactions are stored per account identifier key.
 * </p>
 *
 * @since 1.2.0
 */
final class TransactionHistoryImpl implements TransactionHistory {

    private final Map<AccountIdentifierKey, List<Transaction.ImmutableView>> transactionHistory = new HashMap<>();

    @Override
    @NotNull
    public List<Transaction.ImmutableView> get(@NotNull AccountIdentifierKey accountIdentifierKey) {
        if (accountIdentifierKey == null) {
            throw new IllegalArgumentException("Account identifier key cannot be null");
        }

        synchronized (transactionHistory) {
            return Collections.unmodifiableList(
                transactionHistory.getOrDefault(accountIdentifierKey, Collections.emptyList())
            );
        }
    }

    @Override
    public void register(@NotNull AccountIdentifierKey accountIdentifierKey, @NotNull Transaction.ImmutableView transaction) {
        if (accountIdentifierKey == null) {
            throw new IllegalArgumentException("Account identifier key cannot be null");
        }
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }

        synchronized (transactionHistory) {
            final List<Transaction.ImmutableView> accountTransactionHistory = transactionHistory
                .computeIfAbsent(accountIdentifierKey, k -> new ArrayList<>());
            accountTransactionHistory.add(transaction);
        }
    }
}
