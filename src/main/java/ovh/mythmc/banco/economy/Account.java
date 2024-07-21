package ovh.mythmc.banco.economy;

import java.util.UUID;

public class Account {
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

    protected UUID getUuid() {
        return uuid;
    }

    protected int getAmount() {
        return amount;
    }

    protected int getTransactions() {
        return transactions;
    }

    protected void setAmount(int amount) {
        this.amount = Math.max(amount, 0);
    }

    protected void setTransactions(int transactions) {
        this.transactions = transactions;
    }
}
