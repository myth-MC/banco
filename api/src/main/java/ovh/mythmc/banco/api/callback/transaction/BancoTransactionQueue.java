package ovh.mythmc.banco.api.callback.transaction;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ovh.mythmc.banco.api.accounts.Transaction;
import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.callbacks.annotations.v1.CallbackField;

/**
 * Callback event fired when a transaction is being queued.
 * <p>
 * This callback allows plugins to intercept transaction queuing and modify
 * or cancel the transaction before it is added to the queue.
 * </p>
 *
 * @since 1.0.0
 */
@Getter
@Setter
@Accessors(fluent = true)
@Callback
@CallbackField(field = "transaction", getter = "transaction()")
@CallbackField(field = "position", getter = "position()")
@CallbackField(field = "cancelled", getter = "cancelled()", isExtraParameter = true)
public final class BancoTransactionQueue {

    private final Transaction.ImmutableView transaction;
    private final int position;
    private boolean cancelled = false;

    /**
     * Creates a new BancoTransactionQueue callback.
     *
     * @param transaction the transaction being queued
     * @param position the position in the queue (1-indexed)
     * @throws IllegalArgumentException if transaction is null or position is negative
     */
    public BancoTransactionQueue(@NotNull Transaction.ImmutableView transaction, int position) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        if (position < 0) {
            throw new IllegalArgumentException("Position cannot be negative");
        }
        this.transaction = transaction;
        this.position = position;
    }
}
