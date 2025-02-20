package ovh.mythmc.banco.api.callback.storage;

import ovh.mythmc.banco.api.storage.BancoStorage;
import ovh.mythmc.callbacks.annotations.v1.Callback;

@Callback
public final record BancoStorageRegister(BancoStorage bancoStorage) {
    
}
