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

    private double amount;

    @Setter(AccessLevel.PROTECTED)
    private double transactions;

    public Account(UUID uuid,
                   double amount,
                   double transactions) {
        this.uuid = uuid;
        this.amount = amount;
        this.transactions = transactions;
    }

    public double amount() {
        return Banco.get().getAccountManager().amount(this);
    }
    
    protected void setAmount(double amount) {
        this.amount = Math.max(amount, 0);
    }

    public final Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("amount", amount);
        map.put("transactions", transactions);
        return map;
    }

}
