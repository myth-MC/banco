package ovh.mythmc.banco.api;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.configuration.BancoSettingsProvider;
import ovh.mythmc.banco.api.storage.BancoStorageRegistry;
import ovh.mythmc.banco.api.accounts.AccountManager;
import ovh.mythmc.banco.api.items.BancoItemRegistry;
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

    @NotNull AccountManager getAccountManager();

    @NotNull LoggerWrapper getLogger();

    @NotNull BancoSettingsProvider getSettings();

    @NotNull default BancoItemRegistry getItemRegistry() { return BancoItemRegistry.instance; }

    @NotNull default BancoStorageRegistry getStorageRegistry() { return BancoStorageRegistry.instance; }

}
