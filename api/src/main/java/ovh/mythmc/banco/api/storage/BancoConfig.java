package ovh.mythmc.banco.api.storage;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.ConfigurationSection;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.logger.LoggerWrapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

@Getter
public final class BancoConfig {

    static final LoggerWrapper logger = new LoggerWrapper() {
        @Override
        public void info(String message, Object... args) {
            Banco.get().getLogger().info("[config] " + message, args);
        }

        @Override
        public void warn(String message, Object... args) {
            Banco.get().getLogger().warn("[config] " + message, args);
        }

        @Override
        public void error(String message, Object... args) {
            Banco.get().getLogger().error("[config] " + message, args);
        }
    };

    private final SimpleYamlConfig yamlConfig;
    private final Settings settings = new Settings();

    public BancoConfig(final @NotNull File pluginFolder) {
        try {
            Files.createDirectories(pluginFolder.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.yamlConfig = new SimpleYamlConfig(new File(pluginFolder, "config.yml"));
    }

    public void load() {
        try {
            yamlConfig.load(Objects.requireNonNull(Banco.class.getResource("/config.yml")));
        } catch (Exception e) {
            throw new IllegalStateException("Error while loading configuration", e);
        }

        loadValues();
    }

    public void loadValues() {
        settings.debug = yamlConfig.getBoolean("debug");
        settings.currency = yamlConfig.getConfigurationSection("currency");
        settings.autoSave = yamlConfig.getConfigurationSection("auto-save");
        settings.updateTracker = yamlConfig.getConfigurationSection("update-tracker");
        settings.commands = yamlConfig.getConfigurationSection("commands");

        Banco.get().getEconomyManager().registerAll(settings.currency.getConfigurationSection("value"));
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Settings {
        private boolean debug;
        private ConfigurationSection currency;
        private ConfigurationSection autoSave;
        private ConfigurationSection updateTracker;
        private ConfigurationSection commands;
    }

}
