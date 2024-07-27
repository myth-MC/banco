package ovh.mythmc.banco.common.boot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.BancoSupplier;
import ovh.mythmc.banco.api.storage.BancoStorage;

import java.io.File;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public abstract class BancoBootstrap<T> implements Banco {

    private T plugin;
    private BancoStorage storage;

    public BancoBootstrap(final @NotNull T plugin,
                          final File dataDirectory) {
        // Set the Banco API
        BancoSupplier.set(this);

        this.plugin = plugin;
        this.storage = new BancoStorage(dataDirectory);
    }

    public final void initialize() {
        getLogger().info("Enabling all tasks and features...");

        reload();

        try {
            enable();

            getLogger().info("Done!");
        } catch (Throwable throwable) {
            getLogger().error("An error has occurred while initializing babnco: {}", throwable);
            throwable.printStackTrace(System.err);
            return;
        }

        // update checker
    }

    public abstract void enable();

    public abstract void shutdown();

    public final void reload() {
        getStorage().load();
    }

    public abstract String version();

    public abstract boolean isOnline(UUID uuid);

    public abstract int getInventoryValue(UUID uuid);

    public abstract void clearInventory(UUID uuid);

    public abstract void setInventory(UUID uuid, int amount);

}
