package ovh.mythmc.banco.common.boot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.BancoSupplier;
import ovh.mythmc.banco.api.configuration.BancoSettingsProvider;
import ovh.mythmc.banco.common.listeners.GestaltListener;
import ovh.mythmc.banco.common.util.MigrationUtil;
import ovh.mythmc.banco.common.util.UpdateChecker;
import ovh.mythmc.gestalt.Gestalt;

import java.io.File;
import java.sql.SQLException;

@Getter
@RequiredArgsConstructor
public abstract class BancoBootstrap<T> implements Banco {

    private T plugin;

    private final BancoSettingsProvider settings;
    private final MigrationUtil migrationUtil;
    private final File dataDirectory;

    public BancoBootstrap(final @NotNull T plugin,
                          final File dataDirectory) {
        // Set the Banco API
        BancoSupplier.set(this);

        this.plugin = plugin;

        this.dataDirectory = dataDirectory;
        this.migrationUtil = new MigrationUtil(dataDirectory);
        this.settings = new BancoSettingsProvider(dataDirectory);
  
    }

    public final void initialize() {
        getSettings().load();

        migrationUtil.data();

        try {
            Banco.get().getAccountManager().getDatabase().initialize(dataDirectory.getAbsolutePath() + File.separator + "accounts.db");
        } catch (SQLException e) {
            Banco.get().getLogger().error("An exception has been produced while loading database: ", e);
        }

        try {
            enable();
        } catch (Throwable throwable) {
            getLogger().error("An error has occurred while initializing banco: {}", throwable);
            throwable.printStackTrace(System.err);
            return;
        }

        // Register Gestalt feature listener
        Gestalt.get().getListenerRegistry().register(new GestaltListener(), true);

        if (Banco.get().getSettings().get().getUpdateTracker().isEnabled())
            UpdateChecker.check();
    }

    public abstract void enable();

    public abstract void shutdown();

    public final void reload() {
        getSettings().load();
    }

    public abstract String version();

}
