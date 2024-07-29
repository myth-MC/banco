package ovh.mythmc.banco.common.boot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.BancoSupplier;
import ovh.mythmc.banco.api.storage.BancoConfig;
import ovh.mythmc.banco.api.storage.BancoStorage;
import ovh.mythmc.banco.common.util.UpdateChecker;

import java.io.File;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public abstract class BancoBootstrap<T> implements Banco {

    private T plugin;
    private BancoConfig config;
    private BancoStorage storage;

    public BancoBootstrap(final @NotNull T plugin,
                          final File dataDirectory) {
        // Set the Banco API
        BancoSupplier.set(this);

        this.plugin = plugin;
        this.config = new BancoConfig(dataDirectory);
        this.storage = new BancoStorage(dataDirectory);
    }

    public final void initialize() {
        getLogger().info("Enabling all tasks and features...");

        reload();

        try {
            enable();

            getLogger().info("Done!");
        } catch (Throwable throwable) {
            getLogger().error("An error has occurred while initializing banco: {}", throwable);
            throwable.printStackTrace(System.err);
            return;
        }

        if (Banco.get().getConfig().getSettings().getUpdateTracker().enabled())
            UpdateChecker.check();
    }

    public abstract void enable();

    public abstract void shutdown();

    public final void reload() {
        getStorage().clear();
        getStorage().load();
        getConfig().load();
    }

    public abstract String version();

    public abstract boolean isOnline(UUID uuid);

    public abstract double getInventoryValue(UUID uuid);

    public abstract void clearInventory(UUID uuid);

    public abstract void setInventory(UUID uuid, int amount);

}
