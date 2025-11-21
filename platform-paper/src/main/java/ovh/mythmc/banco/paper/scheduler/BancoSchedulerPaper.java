package ovh.mythmc.banco.paper.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import lombok.RequiredArgsConstructor;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.scheduler.BancoScheduler;

@RequiredArgsConstructor
public final class BancoSchedulerPaper extends BancoScheduler {

    private final Plugin plugin;

    @Override
    public void run(Runnable runnable) {
        Bukkit.getGlobalRegionScheduler().run(plugin, scheduledTask -> runnable.run());
    }

    @Override
    public void runAsync(Runnable runnable) {
        if (Banco.get().isShuttingDown()) {
            runnable.run();
            return;
        }

        try {
            Bukkit.getAsyncScheduler().runNow(plugin, scheduledTask -> runnable.run());
        } catch (Throwable t) {
            if (Banco.get().getSettings().get().isDebug()) {
                Banco.get().getLogger().debug("Exception while running async task:");
                t.printStackTrace(System.err);
            }
        }
    }
    
}
