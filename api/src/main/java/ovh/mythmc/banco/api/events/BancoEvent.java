package ovh.mythmc.banco.api.events;

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
    
}
