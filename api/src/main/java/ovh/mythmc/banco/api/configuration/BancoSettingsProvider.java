package ovh.mythmc.banco.api.configuration;

import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;

import org.jetbrains.annotations.NotNull;

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
    }

    public void updateVersion(int newVersion) {
        this.settings.getDatabase().setVersion(newVersion);
        YamlConfigurations.save(settingsFilePath, BancoSettings.class, this.settings);
    }

    public BancoSettings get() { return settings; }

}
