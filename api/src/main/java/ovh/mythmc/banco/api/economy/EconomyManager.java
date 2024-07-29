package ovh.mythmc.banco.api.economy;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.simpleyaml.configuration.ConfigurationSection;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.logger.LoggerWrapper;

import java.util.HashMap;
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
    private static final Map<String, Double> valuesMap = new HashMap<>();

    public void registerAll(ConfigurationSection configurationSection) {
        values().clear();

        for (String materialName : configurationSection.getKeys(false)) {
            double value = configurationSection.getDouble(materialName);

            if (Banco.get().getConfig().getSettings().isDebug())
                logger.info(materialName + ": " + value);

            register(materialName, value);
        }
    }

    public void register(String materialName, double value) { valuesMap.put(materialName, value); }

    public void unregister(String materialName) { valuesMap.remove(materialName); }

    public void clear() { valuesMap.clear(); }

    public Map<String, Double> values() { return valuesMap; }

    public double value(String materialName) { return value(materialName, 1); }

    public double value(String materialName, int amount) {
        for (Map.Entry<String, Double> entry : valuesMap.entrySet()) {
            if (!materialName.equals(entry.getKey())) continue;
            return entry.getValue() * amount;
        }

        return 0;
    }

}
