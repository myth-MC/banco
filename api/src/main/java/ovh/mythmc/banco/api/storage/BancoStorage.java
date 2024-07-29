package ovh.mythmc.banco.api.storage;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.file.YamlFile;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.economy.Account;
import ovh.mythmc.banco.api.logger.LoggerWrapper;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Getter
public final class BancoStorage {

    final YamlFile yamlFile;

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

    public BancoStorage(final @NotNull File pluginFolder) {
        this.yamlFile = new YamlFile(new File(pluginFolder, "data.yml"));
    }

    public void clear() {
        for (int i = 0; i < Banco.get().getAccountManager().get().size(); i++) {
            Account account = Banco.get().getAccountManager().get().get(i);
            Banco.get().getAccountManager().remove(account);
        }
    }

    public void load() {
        try {
            if (yamlFile.exists()) {
                yamlFile.load();

                if (Banco.get().getConfig().getSettings().isDebug())
                    logger.info("Loading accounts from data.yml...");

                ConfigurationSection accounts = yamlFile.getConfigurationSection("accounts");
                accounts.getKeys(false).forEach(key -> {
                    ConfigurationSection account = accounts.getConfigurationSection(key);
                    UUID uuid = UUID.fromString(key);
                    int amount = account.getInt("amount");
                    int transactions = account.getInt("transactions");

                    Banco.get().getAccountManager().add(new Account(uuid, amount, transactions));
                });

            } else {
                if (Banco.get().getConfig().getSettings().isDebug())
                    logger.info("Creating data.yml...");

                yamlFile.createNewFile(true);
                yamlFile.createSection("accounts");
                yamlFile.save();
            }

            if (Banco.get().getConfig().getSettings().isDebug())
                logger.info("Done! (" + Banco.get().getAccountManager().get().size() + " accounts loaded)");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void save() throws IOException {
        if (Banco.get().getConfig().getSettings().isDebug())
            logger.info("Saving " + Banco.get().getAccountManager().get().size() + " account(s)...");

        ConfigurationSection accountsSection = yamlFile.createSection("accounts");
        Banco.get().getAccountManager().get().forEach(account -> {
            ConfigurationSection accountSection = accountsSection.createSection(account.getUuid().toString());
            accountSection.set("amount", account.amount());
            accountSection.set("transactions", account.transactions());
        });

        yamlFile.save();

        if (Banco.get().getConfig().getSettings().isDebug())
            logger.info("Done!");
    }

}
