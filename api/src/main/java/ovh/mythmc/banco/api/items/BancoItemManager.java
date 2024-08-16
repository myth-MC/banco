package ovh.mythmc.banco.api.items;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.logger.LoggerWrapper;

import java.math.BigDecimal;
import java.util.ArrayList;
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

    public void registerAll(List<BancoItem> items) {
        itemsList.clear();

        items.forEach(this::register);
    }

    public void register(BancoItem item) {
        itemsList.add(item);

        if (Banco.get().getSettings().get().isDebug())
            logger.info("Registered material {} with displayName {} and customModelData {}: {}",
                    item.name(),
                    item.displayName(),
                    item.customModelData(),
                    item.value()
            );
    }

    public void unregister(BancoItem item) { itemsList.remove(item); }

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

    public BigDecimal value(BancoItem item, int amount) {
        return item.value().multiply(BigDecimal.valueOf(amount));
    }

}
