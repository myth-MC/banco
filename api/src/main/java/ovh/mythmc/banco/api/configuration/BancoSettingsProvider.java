package ovh.mythmc.banco.api.configuration;

import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;
import net.kyori.adventure.text.Component;
import ovh.mythmc.banco.api.configuration.serializer.ComponentSerializer;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public final class BancoSettingsProvider {

    private final YamlConfigurationProperties properties = YamlConfigurationProperties.newBuilder()
        .charset(StandardCharsets.UTF_8)
        .addSerializer(Component.class, new ComponentSerializer())
        .build();

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
                this.properties
        );
    }

    public void updateVersion(int newVersion) {
        this.settings.getDatabase().setVersion(newVersion);
        YamlConfigurations.save(settingsFilePath, BancoSettings.class, this.settings, this.properties);
    }

    public void setDatabaseInitialized() {
        this.settings.getDatabase().setDatabaseInitialized();
        YamlConfigurations.save(settingsFilePath, BancoSettings.class, this.settings, this.properties);
    }

    public BancoSettings get() { return settings; }

}
