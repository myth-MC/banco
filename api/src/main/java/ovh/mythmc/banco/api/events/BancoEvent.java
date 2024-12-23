package ovh.mythmc.banco.api.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BancoEvent extends Event {

    private static final @NotNull HandlerList handlers = new HandlerList();

    public BancoEvent() {
        super(false);
    }

    public BancoEvent(boolean isAsync) {
        super(isAsync);
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public void call() {
        if (!Bukkit.getPluginManager().isPluginEnabled("banco"))
            return;

        BancoEvent event = this;
        Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("banco"), new Runnable() {
            @Override
            public void run() {
                Bukkit.getPluginManager().callEvent(event);
            }
        });
    }

    public void callAsync() {
        if (!Bukkit.getPluginManager().isPluginEnabled("banco"))
            return;
            
        BancoEvent event = this;
        Bukkit.getScheduler().runTaskAsynchronously(Bukkit.getPluginManager().getPlugin("banco"), new Runnable() {
            @Override
            public void run() {
                Bukkit.getPluginManager().callEvent(event);
            }
        });
    }
    
}
