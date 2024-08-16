package ovh.mythmc.banco.api.accounts;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.economy.BancoHelper;
import ovh.mythmc.banco.api.event.impl.BancoTransactionEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public void clear() { accountsList.clear(); }

    public List<Account> get() { return accountsList; }

    public Account get(UUID uuid) {
        for (Account account : accountsList) {
            if (account.getUuid().equals(uuid))
                return account;
        }

        return null;
    }

    public void deposit(final @NotNull Account account, BigDecimal amount) {
        set(account, account.amount().add(amount));
    }

    public void withdraw(final @NotNull Account account, BigDecimal amount) {
        set(account, account.amount().subtract(amount));
    }

    public void set(final @NotNull Account account, BigDecimal amount) {
        if (account.amount().compareTo(amount) == 0)
            return;

        if (account.amount().compareTo(amount) < 0) {
            if (BancoHelper.get().isOnline(account.getUuid())) {
                account.setTransactions(BigDecimal.valueOf(0));

                Banco.get().getEventManager().publish(new BancoTransactionEvent(account, amount.subtract(account.amount())));

                BigDecimal remainder = BancoHelper.get().add(account.getUuid(), amount.subtract(account.amount()));
                account.setTransactions(account.getTransactions().add(remainder.setScale(2, RoundingMode.HALF_UP)));
                return;
            }

            account.setTransactions(account.getTransactions().add(amount.subtract(account.amount())));
        } else {
            if (BancoHelper.get().isOnline(account.getUuid())) {
                account.setTransactions(BigDecimal.valueOf(0));
                BigDecimal toRemove = account.amount().subtract(amount);
                BigDecimal remainder = BancoHelper.get().remove(account.getUuid(), toRemove);

                Banco.get().getEventManager().publish(new BancoTransactionEvent(account, toRemove.negate()));

                account.setTransactions(account.getTransactions().subtract(remainder.setScale(2, RoundingMode.HALF_UP)));
                return;
            }

            account.setTransactions(account.getTransactions().subtract(account.amount().subtract(amount)));
        }
    }

    public boolean has(final @NotNull Account account, BigDecimal amount) {
        return account.amount().compareTo(amount) >= 0;
    }

    public BigDecimal amount(final @NotNull Account account) {
        if (BancoHelper.get().isOnline(account.getUuid()))
            account.setAmount(BancoHelper.get().getInventoryValue(account.getUuid()));

        return account.getAmount().add(account.getTransactions());
    }

    public void updateTransactions(final @NotNull Account account) {
        BigDecimal amount = account.amount();
        account.setTransactions(BigDecimal.valueOf(0));

        set(account, amount);
    }

}
