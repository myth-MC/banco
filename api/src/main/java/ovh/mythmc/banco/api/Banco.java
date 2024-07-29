package ovh.mythmc.banco.api;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.storage.BancoConfig;
import ovh.mythmc.banco.api.economy.AccountManager;
import ovh.mythmc.banco.api.economy.EconomyManager;
import ovh.mythmc.banco.api.logger.LoggerWrapper;
import ovh.mythmc.banco.api.storage.BancoStorage;

import java.util.UUID;

public interface Banco {

    @NotNull static Banco get() { return BancoSupplier.get(); }

    void reload();

    String version();

    boolean isOnline(UUID uuid);

    double getInventoryValue(UUID uuid);

    void clearInventory(UUID uuid);

    void setInventory(UUID uuid, int amount);

    @NotNull LoggerWrapper getLogger();

    @NotNull BancoConfig getConfig();

    @NotNull BancoStorage getStorage();

    @NotNull default AccountManager getAccountManager() { return AccountManager.instance; }

    @NotNull default EconomyManager getEconomyManager() { return EconomyManager.instance; }

}
