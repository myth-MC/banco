package ovh.mythmc.banco.api.callbacks.storage;

import ovh.mythmc.banco.api.storage.BancoStorage;
import ovh.mythmc.callbacks.annotations.v1.Callback;

@Callback
public final record BancoStorageUnregister(BancoStorage bancoStorage) {
    
}
