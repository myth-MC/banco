package ovh.mythmc.banco.api.economy;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import ovh.mythmc.banco.api.Banco;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Getter(AccessLevel.PROTECTED)
public class Account {

    @Getter(AccessLevel.PUBLIC)
    private final UUID uuid;

    private int amount;

    @Setter(AccessLevel.PROTECTED)
    private int transactions;

    public Account(UUID uuid,
                   int amount,
                   int transactions) {
        this.uuid = uuid;
        this.amount = amount;
        this.transactions = transactions;
    }

    public int amount() {
        return Banco.get().getAccountManager().amount(this);
    }

    protected void setAmount(int amount) {
        this.amount = Math.max(amount, 0);
    }

    public final Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("amount", amount);
        map.put("transactions", transactions);
        return map;
    }

}
