package ovh.mythmc.banco.api.accounts;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.callback.transaction.BancoTransactionProcessCallback;
import ovh.mythmc.banco.api.callback.transaction.BancoTransactionProcess;
import ovh.mythmc.banco.api.scheduler.BancoScheduler;
import ovh.mythmc.banco.api.storage.BancoStorage;

@Data
@Builder
@Accessors(fluent = true)
@AllArgsConstructor
public class Transaction {

    private @NotNull Account account;

    private @NotNull Operation operation;

    private @NotNull BigDecimal amount;

    public void transact() {
        // Best-effort logging: mark the start of this transaction. Don't let logging stop the transaction.
        try {
            Banco.get().getLogger().info("Transaction START - acct={} op={} amount={} thread={} time={}",
                    account.getUuid(), operation, amount, Thread.currentThread().getName(), Instant.now());
        } catch (Exception ignored) {
            // logging failed - continue anyway
        }

        // Run transaction callbacks
        final var callback = new BancoTransactionProcess(this);
        BancoTransactionProcessCallback.INSTANCE.invoke(callback);

        if (callback.cancelled()) {
            // A callback cancelled this transaction, log and stop
            try {
                Banco.get().getLogger().info("Transaction CANCELLED - acct={} op={} amount={} time={}", account.getUuid(), operation, amount, Instant.now());
            } catch (Exception ignored) {}
            return;
        }
        // Prevent concurrent updates to the same account: synchronize on a per-account lock
        UUID acct = account.getUuid();
        Object lock = AccountLocks.lockFor(acct);
        synchronized (lock) {
            switch (operation) {
                case DEPOSIT -> set(account.amount().add(amount));
                case WITHDRAW -> set(account.amount().subtract(amount));
                case SET -> set(amount);
            }

            try {
                Banco.get().getLogger().info("Transaction END   - acct={} op={} amount={} resulting={} time={}",
                        acct, operation, amount, account.amount(), Instant.now());
            } catch (Exception ignored) {}
        }
    }

    public void queue() {
        BancoScheduler.get().queueTransaction(this);
    }

    public ImmutableView asImmutable() {
        return new ImmutableView(this);
    }

    private void set(@NotNull BigDecimal newAmount) {
        if (account.amount().compareTo(newAmount) == 0)
            return;

        if (account.amount().compareTo(newAmount) < 0) { // Add amount to account
            account.setTransactions(BigDecimal.valueOf(0));
            BigDecimal toAdd = newAmount.subtract(account.amount());

            // Online accounts
            if (Bukkit.getOfflinePlayer(account.getUuid()).isOnline()) {
                // Add to all BancoStorage instances
                for (BancoStorage storage : Banco.get().getStorageRegistry().getByOrder())
                    if (toAdd.compareTo(BigDecimal.valueOf(0)) > 0)
                        toAdd = toAdd.subtract(storage.add(account.getUuid(), toAdd));

                // Set transactions to remaining amount
                account.setTransactions(account.getTransactions().add(toAdd.setScale(2, RoundingMode.HALF_UP)));
                Banco.get().getAccountManager().getDatabase().update(account);
                return;
            }

            // Add to all BancoStorage instances that do not require an online Player
            for (BancoStorage storage : Banco.get().getStorageRegistry().getByOrder()) {
                if (!storage.supportsOfflinePlayers())
                    continue;
                
                if (toAdd.compareTo(BigDecimal.valueOf(0)) > 0)
                    toAdd = toAdd.subtract(storage.add(account.getUuid(), toAdd));
            }

            // Register transaction if player is not online
            account.setTransactions(account.getTransactions().add(toAdd));
            Banco.get().getAccountManager().getDatabase().update(account);
        } else { // Remove amount from account
            BigDecimal toRemove = account.amount().subtract(newAmount);
            
            // Online accounts
            if (Bukkit.getOfflinePlayer(account.getUuid()).isOnline()) {

                // Remove from all BancoStorage instances
                for (BancoStorage storage : Banco.get().getStorageRegistry().getByOrder())
                    if (toRemove.compareTo(BigDecimal.valueOf(0)) > 0)
                        toRemove = storage.remove(account.getUuid(), toRemove);

                // Set transactions to remaining amount
                account.setTransactions(account.getTransactions().subtract(toRemove.setScale(2, RoundingMode.HALF_UP)));
                Banco.get().getAccountManager().getDatabase().update(account);
                return;
            }

            // Remove from all BancoStorage instances that do not require an online Player
            for (BancoStorage storage : Banco.get().getStorageRegistry().getByOrder()) {
                if (!storage.supportsOfflinePlayers())
                    continue;
            
                if (toRemove.compareTo(BigDecimal.valueOf(0)) > 0)
                    toRemove = storage.remove(account.getUuid(), toRemove);
            }

            // Register transaction if player is not online
            account.setTransactions(account.getTransactions().subtract(toRemove));
            Banco.get().getAccountManager().getDatabase().update(account);
        }
    }

    public enum Operation {
        DEPOSIT,
        WITHDRAW,
        SET
    }

    public static class ImmutableView {

        private final Transaction transaction;

        ImmutableView(Transaction transaction) {
            this.transaction = transaction;
        }

        public Account account() {
            return transaction.account;
        }

        public BigDecimal amount() {
            return transaction.amount;
        }

        public Operation operation() {
            return transaction.operation;
        }

    }

}
