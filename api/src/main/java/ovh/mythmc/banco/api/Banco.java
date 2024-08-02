package ovh.mythmc.banco.api;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.event.BancoEventManager;
import ovh.mythmc.banco.api.storage.BancoConfig;
import ovh.mythmc.banco.api.economy.accounts.AccountManager;
import ovh.mythmc.banco.api.economy.EconomyManager;
import ovh.mythmc.banco.api.logger.LoggerWrapper;
import ovh.mythmc.banco.api.storage.BancoStorage;

public interface Banco {

    @NotNull static Banco get() { return BancoSupplier.get(); }

    void reload();

    String version();

    @NotNull LoggerWrapper getLogger();

    @NotNull BancoConfig getConfig();

    @NotNull BancoStorage getStorage();

    @NotNull default AccountManager getAccountManager() { return AccountManager.instance; }

    @NotNull default EconomyManager getEconomyManager() { return EconomyManager.instance; }

    @NotNull default BancoEventManager getEventManager() { return BancoEventManager.instance; }

}
