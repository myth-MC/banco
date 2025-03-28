package ovh.mythmc.banco.paper.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import lombok.RequiredArgsConstructor;
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
        Bukkit.getAsyncScheduler().runNow(plugin, scheduledTask -> runnable.run());
    }
    
}
