package ovh.mythmc.banco.common.util;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.Account;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.UUID;

public final class MigrationUtil {

    private static File pluginFolder;

    public MigrationUtil(final @NotNull File pluginFolder) {
        MigrationUtil.pluginFolder = pluginFolder;
    }

    public void data() {
        File dataConfigFile = new File(pluginFolder, "data.yml");
        if (!dataConfigFile.exists())
            return;

        Banco.get().getLogger().warn("Migrating data.yml to new format...");

        FileConfiguration dataConfig = new YamlConfiguration();
        try {
            Files.copy(dataConfigFile.toPath(), new File(pluginFolder, "data.yml.backup").toPath());
            dataConfig.load(dataConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            Banco.get().getLogger().error("Error while migrating data: {}", e);
        }

        ConfigurationSection accounts = dataConfig.getConfigurationSection("accounts");
        accounts.getKeys(false).forEach(key -> {
            ConfigurationSection account = accounts.getConfigurationSection(key);
            UUID uuid = UUID.fromString(key);
            BigDecimal amount = BigDecimal.valueOf(account.getDouble("amount"));
            BigDecimal transactions = BigDecimal.valueOf(account.getDouble("transactions"));

            Banco.get().getAccountManager().registerAccount(new Account(uuid, amount, transactions));
        });

        Banco.get().getLogger().warn("{} accounts have been migrated!", Banco.get().getAccountManager().get().size());

        Banco.get().getData().save();

        try {
            Files.delete(dataConfigFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
