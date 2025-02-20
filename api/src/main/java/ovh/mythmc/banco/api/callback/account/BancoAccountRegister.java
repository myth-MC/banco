package ovh.mythmc.banco.api.callback.account;

import ovh.mythmc.banco.api.accounts.Account;
import ovh.mythmc.callbacks.annotations.v1.Callback;

@Callback
public final record BancoAccountRegister(Account account) {
    
}
