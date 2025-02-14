package ovh.mythmc.banco.api.callbacks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ovh.mythmc.banco.api.accounts.Transaction;
import ovh.mythmc.gestalt.callbacks.v1.annotations.Callback;
import ovh.mythmc.gestalt.callbacks.v1.annotations.CallbackFieldGetter;

@AllArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
@Callback
public final class BancoTransaction {
    
    @CallbackFieldGetter("transaction")
    private Transaction transaction;

}
