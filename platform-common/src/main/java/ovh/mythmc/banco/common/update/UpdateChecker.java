package ovh.mythmc.banco.common.update;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.logger.LoggerWrapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLConnection;
import java.util.Objects;
import java.util.Scanner;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@UtilityClass
public class UpdateChecker {

    private final String url = "https://raw.githubusercontent.com/myth-MC/banco/main/VERSION";

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private String latest = Banco.get().version();

    private final ScheduledExecutorService asyncScheduler = Executors.newScheduledThreadPool(1);

    @Getter
    private boolean running = false;

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

    private void scheduleTask() {
        if (!Banco.get().getSettings().get().getUpdateChecker().isEnabled())
            return;

        asyncScheduler.schedule(new TimerTask() {
            @Override
            public void run() {
                performTask();
            }
        }, Banco.get().getSettings().get().getUpdateChecker().getIntervalInHours(), TimeUnit.HOURS);
    }

    private void performTask() {
        URLConnection connection = null;
        try {
            connection = URI.create(url).toURL().openConnection();
        } catch (IOException e) {
            if (Banco.get().getSettings().get().isDebug()) {
                logger.warn("Exception while checking for updates:");
                e.printStackTrace();
            }

            return;
        }

        try (Scanner scanner = new Scanner(Objects.requireNonNull(connection).getInputStream())) {
            logger.debug("Checking for updates...");

            String latest = scanner.next();
            setLatest(latest);

            if (isDevelopmentBuild(latest)) {
                logger.warn("This server is running a development build!");
                return;
            }

            if (!Banco.get().version().equals(latest)) {
                logger.info("A new update has been found: " + latest + " (currently running " + Banco.get().version() + ")");
                logger.info("https://github.com/myth-MC/banco/releases/" + latest);
                return;
            }

            logger.debug("No updates have been found.");
        } catch (IOException e) {
            if (Banco.get().getSettings().get().isDebug())
                e.printStackTrace();
        }

        scheduleTask();
    }

    private boolean isDevelopmentBuild(String latest) {
        latest = latest
            .replace(".", "")
            .replace("v", "");

        String currentVersion = Banco.get().version()
            .replace(".", "")
            .replace("v", "");

        if (Integer.parseInt(currentVersion) > Integer.parseInt(latest))
            return true;

        return false;
    }

    public void startTask() {
        if (running)
            return;

        running = true;
        asyncScheduler.schedule(new TimerTask() {
            @Override
            public void run() {
                performTask();
            }
        }, 1, TimeUnit.MINUTES);
    }
}