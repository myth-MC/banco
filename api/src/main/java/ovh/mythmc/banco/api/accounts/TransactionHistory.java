package ovh.mythmc.banco.api.accounts;

import java.util.List;

import org.jetbrains.annotations.NotNull;

public interface TransactionHistory {

    @NotNull List<Transaction.ImmutableView> get(@NotNull AccountIdentifierKey accountIdentifierKey);

    default @NotNull List<Transaction.ImmutableView> get(@NotNull Account account) {
        return get(account.getIdentifier());
    }

    void register(@NotNull AccountIdentifierKey accountIdentifierKey, @NotNull Transaction.ImmutableView transaction);

    default void register(@NotNull AccountIdentifierKey accountIdentifierKey, @NotNull Transaction transaction) {
        register(accountIdentifierKey, transaction.asImmutable());
    }
    
}
