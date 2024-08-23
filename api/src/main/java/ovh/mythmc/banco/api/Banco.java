package ovh.mythmc.banco.api;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.configuration.BancoSettingsProvider;
import ovh.mythmc.banco.api.containers.BancoStorageManager;
import ovh.mythmc.banco.api.data.BancoDataProvider;
import ovh.mythmc.banco.api.event.BancoEventManager;
import ovh.mythmc.banco.api.accounts.AccountManager;
import ovh.mythmc.banco.api.items.BancoItemManager;
import ovh.mythmc.banco.api.logger.LoggerWrapper;

public interface Banco {

    @NotNull static Banco get() { return BancoSupplier.get(); }

    void reload();

    String version();

    @NotNull LoggerWrapper getLogger();

    @NotNull BancoSettingsProvider getSettings();

    @NotNull BancoDataProvider getData();

    @NotNull default AccountManager getAccountManager() { return AccountManager.instance; }

    @NotNull default BancoItemManager getItemManager() { return BancoItemManager.instance; }

    @NotNull default BancoEventManager getEventManager() { return BancoEventManager.instance; }

    @NotNull default BancoStorageManager getStorageManager() { return BancoStorageManager.instance; }

}
