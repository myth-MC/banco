package ovh.mythmc.banco.api.data;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;

import com.google.common.io.Files;

import org.jetbrains.annotations.NotNull;

import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;
import lombok.Getter;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.LegacyAccountSerializer;
import ovh.mythmc.banco.api.accounts.LegacyAccount;
import ovh.mythmc.banco.api.logger.LoggerWrapper;

@Internal
@Deprecated
@ScheduledForRemoval
public final class BancoDataProvider {

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

    private BancoData data;

    @Getter
    private final Path dataFilePath;

    public BancoDataProvider(final @NotNull File pluginFolder) {
        this.data = new BancoData();
        this.dataFilePath = new File(pluginFolder, "accounts.yml").toPath();
    }

    public void load() {
        logger.info("Loading legacy accounts from accounts.yml...");

        YamlConfigurationProperties properties = YamlConfigurationProperties.newBuilder()
                .header(
                        "This file has already been migrated. This is a backup in case you need to go back to the previous version"
                )
                .addSerializer(LegacyAccount.class, new LegacyAccountSerializer())
                .charset(StandardCharsets.UTF_8)
                .build();

        this.data = YamlConfigurations.update(dataFilePath, BancoData.class, properties);

        logger.info("Done! (" + data.getAccounts().size() + " legacy accounts loaded)");
    }

    public void move() {
        try {
            File oldFile = dataFilePath.toFile();
            File newFile = Path.of(dataFilePath.getParent() + File.separator + "accounts-v2-backup.yml").toFile();
            Files.move(oldFile, newFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BancoData get() { return data; }

}