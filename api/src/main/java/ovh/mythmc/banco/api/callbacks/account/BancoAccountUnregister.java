package ovh.mythmc.banco.api.callbacks.account;

import ovh.mythmc.banco.api.accounts.Account;
import ovh.mythmc.callbacks.annotations.v1.Callback;

@Callback
public final record BancoAccountUnregister(Account account) {
    
}
