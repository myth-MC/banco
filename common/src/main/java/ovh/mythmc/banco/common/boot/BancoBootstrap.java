package ovh.mythmc.banco.common.boot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.BancoSupplier;
import ovh.mythmc.banco.api.configuration.BancoSettingsProvider;
import ovh.mythmc.banco.api.data.BancoDataProvider;
import ovh.mythmc.banco.common.util.MigrationUtil;
import ovh.mythmc.banco.common.util.UpdateChecker;

import java.io.File;

@Getter
@RequiredArgsConstructor
public abstract class BancoBootstrap<T> implements Banco {

    private T plugin;
    private BancoSettingsProvider settings;
    private BancoDataProvider data;

    private MigrationUtil migrationUtil;

    public BancoBootstrap(final @NotNull T plugin,
                          final File dataDirectory) {
        // Set the Banco API
        BancoSupplier.set(this);

        this.migrationUtil = new MigrationUtil(dataDirectory);

        this.plugin = plugin;
        this.settings = new BancoSettingsProvider(dataDirectory);
        this.data = new BancoDataProvider(dataDirectory);
    }

    public final void initialize() {
        getSettings().load();
        getData().load();

        migrationUtil.data();

        try {
            enable();
        } catch (Throwable throwable) {
            getLogger().error("An error has occurred while initializing banco: {}", throwable);
            throwable.printStackTrace(System.err);
            return;
        }

        if (Banco.get().getSettings().get().getUpdateTracker().isEnabled())
            UpdateChecker.check();
    }

    public abstract void enable();

    public abstract void shutdown();

    public final void reload() {
        getSettings().load();
        getData().save();
        getData().load();
    }

    public abstract String version();

}
