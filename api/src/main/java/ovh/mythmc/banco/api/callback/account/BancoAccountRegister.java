package ovh.mythmc.banco.api.callback.account;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.api.accounts.Account;
import ovh.mythmc.callbacks.annotations.v1.Callback;

/**
 * Callback event fired when an account is being registered.
 * <p>
 * This callback allows plugins to intercept account registration and modify
 * or cancel the operation before it is committed to the database.
 * </p>
 *
 * @param account the account being registered
 * @since 1.0.0
 */
@Callback
public final record BancoAccountRegister(@NotNull Account account) {
}
