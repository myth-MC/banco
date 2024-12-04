package ovh.mythmc.banco.common.boot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.BancoSupplier;
import ovh.mythmc.banco.api.configuration.BancoSettingsProvider;
import ovh.mythmc.banco.common.features.InventoryFeatures;
import ovh.mythmc.banco.common.features.LocalizationFeature;
import ovh.mythmc.banco.common.features.MetricsFeature;
import ovh.mythmc.banco.common.features.PlaceholderAPIFeature;
import ovh.mythmc.banco.common.features.UpdateCheckerFeature;
import ovh.mythmc.banco.common.features.VaultFeature;
import ovh.mythmc.banco.common.listeners.GestaltListener;
import ovh.mythmc.gestalt.Gestalt;
import ovh.mythmc.gestalt.features.FeatureConstructorParams;
import ovh.mythmc.gestalt.features.GestaltFeature;

import java.io.File;
import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public abstract class BancoBootstrap implements Banco {

    private JavaPlugin plugin;

    private final BancoSettingsProvider settings;
    private final File dataDirectory;

    public BancoBootstrap(final @NotNull JavaPlugin plugin,
                          final File dataDirectory) {
        // Set the Banco API
        BancoSupplier.set(this);

        this.plugin = plugin;

        this.dataDirectory = dataDirectory;
        this.settings = new BancoSettingsProvider(dataDirectory);
    }

    public final void initialize() {
        loadGestalt();
        
        // Register Gestalt features
        Gestalt.get().register(
            InventoryFeatures.class,
            PlaceholderAPIFeature.class,
            UpdateCheckerFeature.class
        );

        registerFeatureWithPluginParam(
            LocalizationFeature.class, 
            MetricsFeature.class, 
            VaultFeature.class
        );

        // Load settings and messages
        reload();

        // Register Gestalt feature listener
        Gestalt.get().getListenerRegistry().register(new GestaltListener(), true);

        try {
            Banco.get().getAccountManager().getDatabase().initialize(dataDirectory.getAbsolutePath() + File.separator + "accounts.db");
            enable();
        } catch (Throwable throwable) {
            getLogger().error("An error has occurred while initializing banco: {}", throwable);
            throwable.printStackTrace(System.err);
            return;
        }
    }

    public abstract void loadGestalt();

    public abstract void enable();

    public abstract void shutdown();

    public final void reload() {
        Gestalt.get().disableAllFeatures("banco");

        getSettings().load();

        Gestalt.get().enableAllFeatures("banco");
    }

    public abstract String version();

    private void registerFeatureWithPluginParam(Class<?>... classes) {
        Arrays.stream(classes).forEach(clazz -> {
            Gestalt.get().register(GestaltFeature.builder()
                .featureClass(clazz)
                .constructorParams(FeatureConstructorParams.builder()
                    .params(plugin)
                    .types(JavaPlugin.class)
                    .build())
                .build());
        });
    }

}
