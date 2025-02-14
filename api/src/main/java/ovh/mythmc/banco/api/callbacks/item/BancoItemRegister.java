package ovh.mythmc.banco.api.callbacks.item;

import ovh.mythmc.banco.api.items.BancoItem;
import ovh.mythmc.gestalt.callbacks.v1.annotations.Callback;

@Callback
public final record BancoItemRegister(BancoItem bancoItem) {
    
}
