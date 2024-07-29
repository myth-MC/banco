package ovh.mythmc.banco.api.economy;

import lombok.Getter;
import ovh.mythmc.banco.api.Banco;

import java.util.UUID;

public class Account {
    @Getter
    private final UUID uuid;
    private int amount;
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

    protected int getAmount() {
        return amount;
    }

    public int transactions() {
        return transactions;
    }

    protected void setAmount(int amount) {
        this.amount = Math.max(amount, 0);
    }

    protected void setTransactions(int transactions) {
        this.transactions = transactions;
    }

}
