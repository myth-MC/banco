package ovh.mythmc.banco.api.callbacks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ovh.mythmc.banco.api.accounts.Transaction;
import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.callbacks.annotations.v1.CallbackFieldGetter;

@AllArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
@Callback
@CallbackFieldGetter(field = "transaction", getter = "transaction()")
public final class BancoTransaction {
    
    private Transaction transaction;

}
