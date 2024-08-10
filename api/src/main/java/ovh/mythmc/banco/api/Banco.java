package ovh.mythmc.banco.api;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.configuration.BancoSettingsProvider;
import ovh.mythmc.banco.api.data.BancoDataProvider;
import ovh.mythmc.banco.api.event.BancoEventManager;
import ovh.mythmc.banco.api.economy.accounts.AccountManager;
import ovh.mythmc.banco.api.economy.EconomyManager;
import ovh.mythmc.banco.api.logger.LoggerWrapper;

public interface Banco {

    @NotNull static Banco get() { return BancoSupplier.get(); }

    void reload();

    String version();

    @NotNull LoggerWrapper getLogger();

    @NotNull BancoSettingsProvider getSettings();

    @NotNull BancoDataProvider getData();

    @NotNull default AccountManager getAccountManager() { return AccountManager.instance; }

    @NotNull default EconomyManager getEconomyManager() { return EconomyManager.instance; }

    @NotNull default BancoEventManager getEventManager() { return BancoEventManager.instance; }

}
