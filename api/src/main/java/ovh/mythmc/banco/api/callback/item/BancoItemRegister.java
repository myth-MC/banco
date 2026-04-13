package ovh.mythmc.banco.api.callback.item;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.api.items.BancoItem;
import ovh.mythmc.callbacks.annotations.v1.Callback;

/**
 * Callback event fired when a currency item is being registered.
 * <p>
 * This callback allows plugins to intercept item registration and modify
 * or cancel the operation before it is added to the item registry.
 * </p>
 *
 * @param bancoItem the currency item being registered
 * @since 1.0.0
 */
@Callback
public final record BancoItemRegister(@NotNull BancoItem bancoItem) {
}
