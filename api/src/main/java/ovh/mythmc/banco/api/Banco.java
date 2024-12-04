package ovh.mythmc.banco.api;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.configuration.BancoSettingsProvider;
import ovh.mythmc.banco.api.storage.BancoStorageManager;
import ovh.mythmc.banco.api.accounts.AccountManager;
import ovh.mythmc.banco.api.items.BancoItemManager;
import ovh.mythmc.banco.api.logger.LoggerWrapper;

public interface Banco {

    @NotNull static Banco get() { return BancoSupplier.get(); }

    @ApiStatus.Internal
    void reload();

    /**
     *
     * @return Plugin version
     */
    String version();

    @NotNull LoggerWrapper getLogger();

    @NotNull BancoSettingsProvider getSettings();

    @NotNull default AccountManager getAccountManager() { return AccountManager.instance; }

    @NotNull default BancoItemManager getItemManager() { return BancoItemManager.instance; }

    @NotNull default BancoStorageManager getStorageManager() { return BancoStorageManager.instance; }

}
