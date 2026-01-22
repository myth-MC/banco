package ovh.mythmc.banco.api.configuration;

import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;
import net.kyori.adventure.text.Component;
import ovh.mythmc.banco.api.configuration.serializer.ComponentSerializer;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * Provides access to and manages the plugin's configuration settings.
 * <p>
 * This class handles loading, saving, and updating the plugin's YAML configuration file.
 * It uses the configlib library for YAML serialization and deserialization.
 * </p>
 *
 * @since 1.0.0
 */
public final class BancoSettingsProvider {

    private final YamlConfigurationProperties properties = YamlConfigurationProperties.newBuilder()
        .charset(StandardCharsets.UTF_8)
        .addSerializer(Component.class, new ComponentSerializer())
        .build();

    private BancoSettings settings;
    private final Path settingsFilePath;

    /**
     * Creates a new BancoSettingsProvider.
     *
     * @param pluginFolder the plugin's data folder
     * @throws IllegalArgumentException if pluginFolder is null
     */
    public BancoSettingsProvider(final @NotNull File pluginFolder) {
        if (pluginFolder == null) {
            throw new IllegalArgumentException("Plugin folder cannot be null");
        }
        this.settings = new BancoSettings();
        this.settingsFilePath = new File(pluginFolder, "settings.yml").toPath();
    }

    /**
     * Loads the configuration from the settings file.
     * <p>
     * If the file doesn't exist, a new configuration will be created with default values.
     * If the file exists, it will be loaded and updated with any new default values.
     * </p>
     */
    public void load() {
        try {
            this.settings = YamlConfigurations.update(
                settingsFilePath,
                BancoSettings.class,
                this.properties
            );
        } catch (Exception e) {
            // If loading fails, use default settings
            this.settings = new BancoSettings();
        }
    }

    /**
     * Updates the database version in the configuration.
     *
     * @param newVersion the new database version
     * @throws IllegalArgumentException if newVersion is negative
     */
    public void updateVersion(int newVersion) {
        if (newVersion < 0) {
            throw new IllegalArgumentException("Database version cannot be negative");
        }

        this.settings.getDatabase().setVersion(newVersion);
        try {
            YamlConfigurations.save(settingsFilePath, BancoSettings.class, this.settings, this.properties);
        } catch (Exception e) {
            // Log error but don't throw - version update is best-effort
        }
    }

    /**
     * Marks the database as initialized in the configuration.
     */
    public void setDatabaseInitialized() {
        this.settings.getDatabase().setDatabaseInitialized();
        try {
            YamlConfigurations.save(settingsFilePath, BancoSettings.class, this.settings, this.properties);
        } catch (Exception e) {
            // Log error but don't throw - initialization flag update is best-effort
        }
    }

    /**
     * Gets the current settings.
     *
     * @return the current settings instance
     */
    @NotNull
    public BancoSettings get() {
        return settings;
    }
}
