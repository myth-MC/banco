package ovh.mythmc.banco.api.callback.item;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.api.items.BancoItem;
import ovh.mythmc.callbacks.annotations.v1.Callback;

/**
 * Callback event fired when a currency item is being unregistered.
 * <p>
 * This callback allows plugins to intercept item unregistration and perform
 * cleanup operations or cancel the operation before it is removed from the registry.
 * </p>
 *
 * @param bancoItem the currency item being unregistered
 * @since 1.0.0
 */
@Callback
public final record BancoItemUnregister(@NotNull BancoItem bancoItem) {
}
