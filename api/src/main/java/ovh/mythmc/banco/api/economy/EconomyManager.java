package ovh.mythmc.banco.api.economy;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.logger.LoggerWrapper;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EconomyManager {

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

    public static final EconomyManager instance = new EconomyManager();
    private static final Map<String, BigDecimal> valuesMap = new HashMap<>();

    public void registerAll(List<BancoItem> items) {
        values().clear();

        items.forEach(item -> {
            register(item.name(), item.value());

            if (Banco.get().getSettings().get().isDebug())
                logger.info(item.name() + ": " + item.value());
        });
    }

    public void register(String materialName, BigDecimal value) { valuesMap.put(materialName, value); }

    public void unregister(String materialName) { valuesMap.remove(materialName); }

    public void clear() { valuesMap.clear(); }

    public Map<String, BigDecimal> values() { return valuesMap; }

    public BigDecimal value(String materialName) { return value(materialName, 1); }

    public BigDecimal value(String materialName, int amount) {
        for (Map.Entry<String, BigDecimal> entry : valuesMap.entrySet()) {
            if (!materialName.equals(entry.getKey())) continue;
            return entry.getValue().multiply(BigDecimal.valueOf(amount));
        }

        return BigDecimal.valueOf(0);
    }

}
