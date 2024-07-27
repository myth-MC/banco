package ovh.mythmc.banco.common.util;

import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.logger.LoggerWrapper;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.Scanner;

public final class UpdaterUtil {

    static final LoggerWrapper logger = new LoggerWrapper() {
        @Override
        public void info(String message, Object... args) {
            Banco.get().getLogger().info("[updater] " + message, args);
        }

        @Override
        public void warn(String message, Object... args) {
            Banco.get().getLogger().warn("[updater] " + message, args);
        }

        @Override
        public void error(String message, Object... args) {
            Banco.get().getLogger().error("[updater] " + message, args);
        }
    };

    public static void check() {
        URLConnection connection = null;
        try {
            String url = "https://raw.githubusercontent.com/myth-MC/banco/main/VERSION";
            connection = new URL(url).openConnection();
        } catch (IOException e) {
            if (Banco.get().getStorage().getConfig().isDebug())
                logger.warn(e.getMessage());
        }

        try (Scanner scanner = new Scanner(Objects.requireNonNull(connection).getInputStream())) {
            if (Banco.get().getStorage().getConfig().isDebug())
                logger.info("Checking for updates...");

            String latest = scanner.next();

            if (!Banco.get().version().equals(latest)) {
                logger.info("A new update has been found: " + latest);
                logger.info("You are currently running banco v" + Banco.get().version());
                return;
            }

            if (Banco.get().getStorage().getConfig().isDebug())
                logger.info("No updates have been found.");
        } catch (IOException e) {
            if (Banco.get().getStorage().getConfig().isDebug())
                logger.warn(e.getMessage());
        }
    }

}
