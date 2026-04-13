package ovh.mythmc.banco.api.callback.storage;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.api.storage.BancoStorage;
import ovh.mythmc.callbacks.annotations.v1.Callback;

/**
 * Callback event fired when a storage system is being unregistered.
 * <p>
 * This callback allows plugins to intercept storage unregistration and perform
 * cleanup operations or cancel the operation before it is removed from the registry.
 * </p>
 *
 * @param bancoStorage the storage system being unregistered
 * @since 1.0.0
 */
@Callback
public final record BancoStorageUnregister(@NotNull BancoStorage bancoStorage) {
}
