package ovh.mythmc.banco.api.callbacks.item;

import ovh.mythmc.banco.api.items.BancoItem;
import ovh.mythmc.callbacks.annotations.v1.Callback;

@Callback
public final record BancoItemUnregister(BancoItem bancoItem) {
    
}
