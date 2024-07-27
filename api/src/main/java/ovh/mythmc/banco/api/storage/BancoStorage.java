package ovh.mythmc.banco.api.storage;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.ConfigurationSection;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.economy.Account;
import ovh.mythmc.banco.api.logger.LoggerWrapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.UUID;

@Getter
public final class BancoStorage {

    static final LoggerWrapper logger = new LoggerWrapper() {
        @Override
        public void info(String message, Object... args) {
            Banco.get().getLogger().info("[data] " + message, args);
        }

        @Override
        public void warn(String message, Object... args) {
            Banco.get().getLogger().warn("[data] " + message, args);
        }

        @Override
        public void error(String message, Object... args) {
            Banco.get().getLogger().error("[data] " + message, args);
        }
    };

    private final SimpleYamlConfig yamlConfig, dataYamlConfig;
    private final Config config = new Config();
    private final Data data = new Data();

    public BancoStorage(final @NotNull File pluginFolder) {
        try {
            Files.createDirectories(pluginFolder.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.yamlConfig = new SimpleYamlConfig(new File(pluginFolder, "config.yml"));
        this.dataYamlConfig = new SimpleYamlConfig(new File(pluginFolder, "data.yml"));
    }

    public void saveData() throws IOException {
        if (Banco.get().getStorage().getConfig().isDebug())
            logger.info("Saving " + Banco.get().getAccountManager().get().size() + " account(s)...");

        File dataFile = dataYamlConfig.getFile();

        if (!dataFile.exists())
            dataFile.createNewFile();

        ConfigurationSection accountsSection = dataYamlConfig.getYaml().createSection("accounts");
        Banco.get().getAccountManager().get().forEach(account -> {
            ConfigurationSection accountSection = accountsSection.createSection(account.getUuid().toString());
            accountSection.set("amount", account.amount());
            accountSection.set("transactions", account.transactions());
        });

        dataYamlConfig.getYaml().save(dataFile);

        if (Banco.get().getStorage().getConfig().isDebug())
            logger.info("Done!");
    }

    public void load() {
        try {
            yamlConfig.load(Objects.requireNonNull(Banco.class.getResource("/config.yml")));
            dataYamlConfig.load(Objects.requireNonNull(Banco.class.getResource("/data.yml")));
        } catch (Exception e) {
            throw new IllegalStateException("Error while loading configuration", e);
        }

        loadValues();
    }

    public void loadValues() {
        config.debug = yamlConfig.getBoolean("debug");
        config.currency = yamlConfig.getConfigurationSection("currency");
        config.autoSave = yamlConfig.getConfigurationSection("auto-save");
        config.updateTracker = yamlConfig.getConfigurationSection("update-tracker");
        config.commands = yamlConfig.getConfigurationSection("commands");

        data.accounts = dataYamlConfig.getConfigurationSection("accounts");
        data.accounts.getKeys(false).forEach(key -> {
            ConfigurationSection account = data.accounts.getConfigurationSection(key);
            UUID uuid = UUID.fromString(key);
            int amount = account.getInt("amount");
            int transactions = account.getInt("transactions");

            Banco.get().getAccountManager().add(new Account(uuid, amount, transactions));
        });

        Banco.get().getEconomyManager().registerAll(config.currency.getConfigurationSection("value"));
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Config {
        private boolean debug;
        private ConfigurationSection currency;
        private ConfigurationSection autoSave;
        private ConfigurationSection updateTracker;
        private ConfigurationSection commands;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Data {
        private ConfigurationSection accounts;
    }

}
