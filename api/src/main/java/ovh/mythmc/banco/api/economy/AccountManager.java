package ovh.mythmc.banco.api.economy;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AccountManager {

    public static final AccountManager instance = new AccountManager();
    private static final List<Account> accountsList = new ArrayList<>();

    public void add(final @NotNull Account account) { accountsList.add(account); }

    public void remove(final @NotNull Account account) { accountsList.remove(account); }

    public List<Account> get() { return accountsList; }

    public Account get(UUID uuid) {
        for (Account account : accountsList) {
            if (account.getUuid().equals(uuid))
                return account;
        }

        return null;
    }

    public void deposit(final @NotNull Account account, double amount) {
        set(account, account.amount() + amount);
    }

    public void withdraw(final @NotNull Account account, double amount) {
        set(account, account.amount() - amount);
    }

    public void set(final @NotNull Account account, double amount) {
        if (Banco.get().isOnline(account.getUuid())) {
            Banco.get().clearInventory(account.getUuid());
            Banco.get().setInventory(account.getUuid(), (int) Math.round(amount));

            account.setAmount(amount);
            return;
        }

        account.setTransactions(amount - account.amount());
    }

    public boolean has(final @NotNull Account account, int amount) {
        return account.amount() >= amount;
    }

    public double amount(final @NotNull Account account) {
        //final int decimals = Banco.get().getConfig().getSettings().getCurrency().decimals() * 10;

        if (Banco.get().isOnline(account.getUuid()))
            account.setAmount(Banco.get().getInventoryValue(account.getUuid()));

        return account.getAmount() + account.getTransactions();
        //return (double) Math.round((account.getAmount() + account.getTransactions()) * decimals) / decimals;
    }

    public void updateTransactions(final @NotNull Account account) {
        set(account, account.amount() + account.getTransactions());
    }

}
