package ovh.mythmc.banco.common.util;

import lombok.experimental.UtilityClass;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.logger.LoggerWrapper;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@UtilityClass
public class UpdateChecker {
    private final ExecutorService asyncExecutor = Executors.newSingleThreadExecutor();

    private final LoggerWrapper logger = new LoggerWrapper() {
        @Override
        public void info(final String message, final Object... args) {
            Banco.get().getLogger().info("[update-checker] " + message, args);
        }

        @Override
        public void warn(final String message, final Object... args) {
            Banco.get().getLogger().warn("[update-checker] " + message, args);
        }

        @Override
        public void error(final String message, final Object... args) {
            Banco.get().getLogger().error("[update-checker] " + message, args);
        }
    };

    public void check() {
        asyncExecutor.execute(() -> {
            URLConnection connection = null;
            try {
                String url = "https://raw.githubusercontent.com/myth-MC/banco/main/VERSION";
                connection = new URL(url).openConnection();
            } catch (IOException e) {
                if (Banco.get().getConfig().getSettings().isDebug())
                    logger.warn(e.getMessage());
            }

            try (Scanner scanner = new Scanner(Objects.requireNonNull(connection).getInputStream())) {
                if (Banco.get().getConfig().getSettings().isDebug())
                    logger.info("Checking for updates...");

                String latest = scanner.next();

                if (!Banco.get().version().equals(latest)) {
                    logger.info("A new update has been found: " + latest);
                    logger.info("You are currently running banco v" + Banco.get().version());
                    return;
                }

                if (Banco.get().getConfig().getSettings().isDebug())
                    logger.info("No updates have been found.");
            } catch (IOException e) {
                if (Banco.get().getConfig().getSettings().isDebug())
                    logger.warn(e.getMessage());
            }
        });
    }

}
