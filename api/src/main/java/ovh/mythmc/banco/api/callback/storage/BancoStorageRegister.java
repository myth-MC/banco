package ovh.mythmc.banco.api.callback.storage;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.api.storage.BancoStorage;
import ovh.mythmc.callbacks.annotations.v1.Callback;

/**
 * Callback event fired when a storage system is being registered.
 * <p>
 * This callback allows plugins to intercept storage registration and modify
 * or cancel the operation before it is added to the storage registry.
 * </p>
 *
 * @param bancoStorage the storage system being registered
 * @since 1.0.0
 */
@Callback
public final record BancoStorageRegister(@NotNull BancoStorage bancoStorage) {
}
