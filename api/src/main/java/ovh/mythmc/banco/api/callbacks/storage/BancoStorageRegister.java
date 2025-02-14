package ovh.mythmc.banco.api.callbacks.storage;

import ovh.mythmc.banco.api.storage.BancoStorage;
import ovh.mythmc.gestalt.callbacks.v1.annotations.Callback;

@Callback
public final record BancoStorageRegister(BancoStorage bancoStorage) {
    
}
