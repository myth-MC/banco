package ovh.mythmc.banco.bukkit.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.RequiredArgsConstructor;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.scheduler.BancoScheduler;

@RequiredArgsConstructor
public final class BancoSchedulerBukkit extends BancoScheduler {

    private final JavaPlugin plugin;

    @Override
    public void run(Runnable runnable) {
        Bukkit.getScheduler().runTask(plugin, runnable);
    }

    @Override
    public void runAsync(Runnable runnable) {
        if (Banco.get().isShuttingDown()) {
            runnable.run();
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }
    
}
