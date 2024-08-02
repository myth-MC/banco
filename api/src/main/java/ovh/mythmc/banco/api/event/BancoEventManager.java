package ovh.mythmc.banco.api.event;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;

import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BancoEventManager {

    public static final BancoEventManager instance = new BancoEventManager();
    private static final Collection<BancoEventListener> eventListeners = new Vector<>(0);
    private static final ExecutorService eventService = Executors.newSingleThreadExecutor();

    @ApiStatus.Internal
    public void publish(final @NotNull BancoEvent event) {
        if (eventListeners.isEmpty())
            return;

        eventService.execute(() -> {
            for (final BancoEventListener eventListener : eventListeners) {
                try {
                    eventListener.handle(event);
                } catch (Throwable throwable) {
                    Banco.get().getLogger().error("Could not pass {} to listener: {}",
                            event.getClass().getSimpleName(), throwable);
                }
            }
        });
    }

    public synchronized void registerListener(final @NotNull BancoEventListener... listeners) {
        eventListeners.addAll(Arrays.asList(listeners));
    }

    public synchronized void unregisterListener(final @NotNull BancoEventListener... listeners) {
        eventListeners.removeAll(Arrays.asList(listeners));
    }

}
