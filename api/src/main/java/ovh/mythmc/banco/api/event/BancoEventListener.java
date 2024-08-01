package ovh.mythmc.banco.api.event;

@FunctionalInterface
public interface BancoEventListener {
    void handle(final BancoEvent event);
}
