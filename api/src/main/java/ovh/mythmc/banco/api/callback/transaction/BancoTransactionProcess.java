package ovh.mythmc.banco.api.callback.transaction;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ovh.mythmc.banco.api.accounts.Transaction;
import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.callbacks.annotations.v1.CallbackField;

@Getter
@Setter
@Accessors(fluent = true)
@Callback
@CallbackField(field = "transaction", getter = "transaction()")
@CallbackField(field = "cancelled", getter = "cancelled()", isExtraParameter = true)
public final class BancoTransactionProcess {
    
    private final Transaction transaction;

    private boolean cancelled = false;

    public BancoTransactionProcess(Transaction transaction) {
        this.transaction = transaction;
    }

}
