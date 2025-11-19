package ovh.mythmc.banco.api.accounts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class TransactionHistoryImpl implements TransactionHistory {

    private final Map<AccountIdentifierKey, List<Transaction.ImmutableView>> transactionHistory = new HashMap<>();

    @Override
    public List<Transaction.ImmutableView> get(AccountIdentifierKey accountIdentifierKey) {
        return transactionHistory.getOrDefault(accountIdentifierKey, List.of());
    }

    @Override
    public void register(AccountIdentifierKey accountIdentifierKey, Transaction.ImmutableView transaction) {
        final List<Transaction.ImmutableView> accountTransactionHistory = transactionHistory.getOrDefault(accountIdentifierKey, new ArrayList<>());
        accountTransactionHistory.add(transaction);

        transactionHistory.put(accountIdentifierKey, accountTransactionHistory);
    }
    
}
