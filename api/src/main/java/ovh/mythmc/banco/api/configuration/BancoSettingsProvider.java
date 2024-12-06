package ovh.mythmc.banco.api.configuration;

import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public final class BancoSettingsProvider {

    private BancoSettings settings;

    private final Path settingsFilePath;

    public BancoSettingsProvider(final @NotNull File pluginFolder) {
        this.settings = new BancoSettings();
        this.settingsFilePath = new File(pluginFolder, "settings.yml").toPath();
    }

    public void load() {
        this.settings = YamlConfigurations.update(
                settingsFilePath,
                BancoSettings.class,
                YamlConfigurationProperties.newBuilder().charset(StandardCharsets.UTF_8).build()
        );

        Banco.get().getItemManager().clear();
        // Legacy items (pre 1.0)
        if (get().getCurrency().getItems() != null) {
            Banco.get().getLogger().warn("This server's settings.yml contains items configured using the legacy format. It is recommended to migrate to the newer format which includes many new features and options. Please, take a look at https://docs.mythmc.ovh/banco or join our Discord (https://discord.gg/bpkwdzREcR) if you need further assistance");
            get().getCurrency().getItems().forEach(bancoItem -> Banco.get().getItemManager().registerItems(bancoItem));
        }
        
        // Modern items (post 1.0)
        if (get().getCurrency().getItems() == null || get().getCurrency().getItems().size() == 0)
            get().getCurrency().getItemMappings().forEach(bancoItem -> Banco.get().getItemManager().registerItems(bancoItem));
    }

    public BancoSettings get() { return settings; }

}
