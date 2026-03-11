package ovh.mythmc.banco.api.callback.account;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.api.accounts.Account;
import ovh.mythmc.callbacks.annotations.v1.Callback;

/**
 * Callback event fired when an account is being unregistered.
 * <p>
 * This callback allows plugins to intercept account unregistration and perform
 * cleanup operations or cancel the operation before it is committed to the database.
 * </p>
 *
 * @param account the account being unregistered
 * @since 1.0.0
 */
@Callback
public final record BancoAccountUnregister(@NotNull Account account) {
}
