package ovh.mythmc.banco.api.callback.transaction;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ovh.mythmc.banco.api.accounts.Transaction;
import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.callbacks.annotations.v1.CallbackField;

/**
 * Callback event fired when a transaction is being processed.
 * <p>
 * This callback allows plugins to intercept transaction processing and modify
 * or cancel the transaction before it is executed.
 * </p>
 *
 * @since 1.0.0
 */
@Getter
@Setter
@Accessors(fluent = true)
@Callback
@CallbackField(field = "transaction", getter = "transaction()")
@CallbackField(field = "cancelled", getter = "cancelled()", isExtraParameter = true)
public final class BancoTransactionProcess {

    private final Transaction transaction;
    private boolean cancelled = false;

    /**
     * Creates a new BancoTransactionProcess callback.
     *
     * @param transaction the transaction being processed
     * @throws IllegalArgumentException if transaction is null
     */
    public BancoTransactionProcess(@NotNull Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        this.transaction = transaction;
    }
}
