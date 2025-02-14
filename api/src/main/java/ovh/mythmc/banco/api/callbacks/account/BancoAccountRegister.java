package ovh.mythmc.banco.api.callbacks.account;

import ovh.mythmc.banco.api.accounts.Account;
import ovh.mythmc.gestalt.callbacks.v1.annotations.Callback;

@Callback
public final record BancoAccountRegister(Account account) {
    
}
