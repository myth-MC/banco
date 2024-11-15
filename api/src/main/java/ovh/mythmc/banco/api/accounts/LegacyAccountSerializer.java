package ovh.mythmc.banco.api.accounts;

import org.jetbrains.annotations.ApiStatus;

import de.exlll.configlib.Serializer;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@ApiStatus.Internal
public final class LegacyAccountSerializer implements Serializer<LegacyAccount, Map<String, Object>> {

    @Override
    public Map<String, Object> serialize(LegacyAccount account) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("uuid", account.getUuid().toString());
        map.put("amount", account.getAmount().doubleValue());
        map.put("transactions", account.getTransactions().doubleValue());
        return map;
    }

    @Override
    public LegacyAccount deserialize(Map<String, Object> map) {
        UUID uuid = UUID.fromString((String) map.get("uuid"));
        BigDecimal amount = BigDecimal.valueOf((Double) map.get("amount"));
        BigDecimal transactions = BigDecimal.valueOf((Double) map.get("transactions"));
        return new LegacyAccount(uuid, amount, transactions);
    }

}