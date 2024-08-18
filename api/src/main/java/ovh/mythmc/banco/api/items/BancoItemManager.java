package ovh.mythmc.banco.api.items;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.event.impl.BancoItemRegisterEvent;
import ovh.mythmc.banco.api.event.impl.BancoItemUnregisterEvent;
import ovh.mythmc.banco.api.logger.LoggerWrapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BancoItemManager {

    static final LoggerWrapper logger = new LoggerWrapper() {
        @Override
        public void info(String message, Object... args) {
            Banco.get().getLogger().info("[eco-manager] " + message, args);
        }

        @Override
        public void warn(String message, Object... args) {
            Banco.get().getLogger().warn("[eco-manager] " + message, args);
        }

        @Override
        public void error(String message, Object... args) {
            Banco.get().getLogger().error("[eco-manager] " + message, args);
        }
    };

    public static final BancoItemManager instance = new BancoItemManager();
    private static final List<BancoItem> itemsList = new ArrayList<>();

    public void registerAll(final @NotNull List<BancoItem> items) {
        itemsList.clear();

        items.forEach(this::register);
    }

    public void register(final @NotNull BancoItem... items) {
        Arrays.asList(items).forEach(bancoItem -> {
            itemsList.add(bancoItem);

            if (Banco.get().getSettings().get().isDebug())
                logger.info("Registered material {} with displayName {} and customModelData {}: {}",
                        bancoItem.name(),
                        bancoItem.displayName(),
                        bancoItem.customModelData(),
                        bancoItem.value()
                );

            // Call BancoItemRegisterEvent
            Banco.get().getEventManager().publish(new BancoItemRegisterEvent(bancoItem));
        });
    }

    public void unregister(final @NotNull BancoItem... items) {
        Arrays.asList(items).forEach(bancoItem -> {
            itemsList.remove(bancoItem);

            // Call BancoItemUnregisterEvent
            Banco.get().getEventManager().publish(new BancoItemUnregisterEvent(bancoItem));
        });
    }

    public void clear() { itemsList.clear(); }

    public List<BancoItem> get() { return List.copyOf(itemsList); }

    public BancoItem get(final @NotNull String materialName,
                         final @NotNull String displayName,
                         final Integer customModelData) {
        for (BancoItem item : get()) {
            if (Objects.equals(materialName, item.name())
                    && Objects.equals(displayName, item.displayName())
                    && Objects.equals(customModelData, item.customModelData()))

                return item;
        }

        return null;
    }

    public BigDecimal value(final @NotNull BancoItem item, int amount) {
        return item.value().multiply(BigDecimal.valueOf(amount));
    }

}
