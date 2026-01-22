package ovh.mythmc.banco.api.accounts;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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

/**
 * Represents a financial transaction on an account.
 * <p>
 * Transactions can be of three types:
 * <ul>
 *   <li>{@link Operation#DEPOSIT} - Adds money to an account</li>
 *   <li>{@link Operation#WITHDRAW} - Removes money from an account</li>
 *   <li>{@link Operation#SET} - Sets the account balance to a specific amount</li>
 * </ul>
 * </p>
 * <p>
 * Transactions are processed through the transaction queue system and can optionally
 * be logged in the transaction history. Each transaction can also have callbacks
 * that execute after the transaction completes.
 * </p>
 *
 * @since 1.0.0
 */
@Data
@Builder
@Accessors(fluent = true)
@AllArgsConstructor
public class Transaction {

    private @NotNull Account account;
    private @NotNull Operation operation;
    private @NotNull BigDecimal amount;

    @Builder.Default
    private List<Runnable> executeAfterTransaction = new ArrayList<>();

    @Builder.Default
    private boolean loggable = true;

    /**
     * Executes this transaction immediately.
     * <p>
     * This method invokes the transaction process callback before executing,
     * allowing plugins to modify or cancel the transaction. The transaction
     * is synchronized on a per-account lock to prevent concurrent modifications.
     * </p>
     */
    public void transact() {
        // Best-effort logging: mark the start of this transaction
        try {
            Banco.get().getLogger().info("Transaction START - acct={} op={} amount={} thread={} time={}",
                account.getUuid(), operation, amount, Thread.currentThread().getName(), Instant.now());
        } catch (Exception ignored) {
            // Logging failed - continue anyway
        }

        // Run transaction callbacks
        final var callback = new BancoTransactionProcess(this);
        BancoTransactionProcessCallback.INSTANCE.invoke(callback);

        if (callback.cancelled()) {
            // A callback cancelled this transaction, log and stop
            try {
                Banco.get().getLogger().info("Transaction CANCELLED - acct={} op={} amount={} time={}",
                    account.getUuid(), operation, amount, Instant.now());
            } catch (Exception ignored) {
                // Logging failed - continue anyway
            }
            return;
        }

        // Prevent concurrent updates to the same account: synchronize on a per-account lock
        final UUID accountUuid = account.getUuid();
        final Object lock = AccountLocks.lockFor(accountUuid);
        synchronized (lock) {
            switch (operation) {
                case DEPOSIT -> set(account.balance().add(amount));
                case WITHDRAW -> set(account.balance().subtract(amount));
                case SET -> set(amount);
            }

            try {
                Banco.get().getLogger().info("Transaction END   - acct={} op={} amount={} resulting={} time={}",
                    accountUuid, operation, amount, account.balance(), Instant.now());
            } catch (Exception ignored) {
                // Logging failed - continue anyway
            }
        }

        // Log transaction if enabled
        if (loggable) {
            Banco.get().getAccountManager().getTransactionHistory()
                .register(account.getIdentifier(), this);
        }
    }

    /**
     * Queues this transaction for later processing.
     *
     * @throws IllegalStateException if BancoScheduler is not initialized
     */
    public void queue() {
        BancoScheduler.get().queueTransaction(this);
    }

    /**
     * Creates an immutable view of this transaction.
     *
     * @return an immutable view of this transaction
     */
    @NotNull
    public ImmutableView asImmutable() {
        return new ImmutableView(this);
    }

    /**
     * Sets the account balance to the specified amount.
     * <p>
     * This method handles the distribution of funds across storage systems:
     * <ul>
     *   <li>For online players: funds are distributed to all storage systems</li>
     *   <li>For offline players: funds are only distributed to storage systems
     *       that support offline players</li>
     *   <li>Remaining funds are stored as pending transactions</li>
     * </ul>
     * </p>
     *
     * @param newAmount the new balance amount
     */
    private void set(@NotNull BigDecimal newAmount) {
        final BigDecimal currentBalance = account.balance();

        // Skip if balance hasn't changed
        if (currentBalance.compareTo(newAmount) == 0) {
            return;
        }

        if (currentBalance.compareTo(newAmount) < 0) {
            // Adding money to account
            handleDeposit(newAmount, currentBalance);
        } else {
            // Removing money from account
            handleWithdrawal(newAmount, currentBalance);
        }
    }

    /**
     * Handles adding money to an account.
     *
     * @param newAmount the target balance
     * @param currentBalance the current balance
     */
    private void handleDeposit(@NotNull BigDecimal newAmount, @NotNull BigDecimal currentBalance) {
        account.setTransactions(BigDecimal.ZERO);
        BigDecimal toAdd = newAmount.subtract(currentBalance);

        final boolean isOnline = Bukkit.getOfflinePlayer(account.getUuid()).isOnline();

        if (isOnline) {
            // Online players: add to all storage systems
            for (final BancoStorage storage : Banco.get().getStorageRegistry().getByOrder()) {
                if (toAdd.compareTo(BigDecimal.ZERO) > 0) {
                    toAdd = toAdd.subtract(storage.add(account.getUuid(), toAdd));
                }
            }

            // Store remaining as pending transaction
            account.setTransactions(account.getTransactions()
                .add(toAdd.setScale(2, RoundingMode.HALF_UP)));
        } else {
            // Offline players: only add to storage systems that support offline players
            for (final BancoStorage storage : Banco.get().getStorageRegistry().getByOrder()) {
                if (!storage.supportsOfflinePlayers()) {
                    continue;
                }

                if (toAdd.compareTo(BigDecimal.ZERO) > 0) {
                    toAdd = toAdd.subtract(storage.add(account.getUuid(), toAdd));
                }
            }

            // Store remaining as pending transaction
            account.setTransactions(account.getTransactions().add(toAdd));
        }

        Banco.get().getAccountManager().getDatabase().updateCache(account);
    }

    /**
     * Handles removing money from an account.
     *
     * @param newAmount the target balance
     * @param currentBalance the current balance
     */
    private void handleWithdrawal(@NotNull BigDecimal newAmount, @NotNull BigDecimal currentBalance) {
        BigDecimal toRemove = currentBalance.subtract(newAmount);

        final boolean isOnline = Bukkit.getOfflinePlayer(account.getUuid()).isOnline();

        if (isOnline) {
            // Online players: remove from all storage systems
            for (final BancoStorage storage : Banco.get().getStorageRegistry().getByOrder()) {
                if (toRemove.compareTo(BigDecimal.ZERO) > 0) {
                    toRemove = storage.remove(account.getUuid(), toRemove);
                }
            }

            // Adjust pending transactions
            account.setTransactions(account.getTransactions()
                .subtract(toRemove.setScale(2, RoundingMode.HALF_UP)));
        } else {
            // Offline players: only remove from storage systems that support offline players
            for (final BancoStorage storage : Banco.get().getStorageRegistry().getByOrder()) {
                if (!storage.supportsOfflinePlayers()) {
                    continue;
                }

                if (toRemove.compareTo(BigDecimal.ZERO) > 0) {
                    toRemove = storage.remove(account.getUuid(), toRemove);
                }
            }

            // Adjust pending transactions
            account.setTransactions(account.getTransactions().subtract(toRemove));
        }

        Banco.get().getAccountManager().getDatabase().updateCache(account);
    }

    /**
     * Represents the type of transaction operation.
     */
    public enum Operation {
        /**
         * Adds money to an account.
         */
        DEPOSIT,

        /**
         * Removes money from an account.
         */
        WITHDRAW,

        /**
         * Sets the account balance to a specific amount.
         */
        SET
    }

    /**
     * An immutable view of a transaction with a timestamp.
     */
    public static final class ImmutableView {

        private final Transaction transaction;
        private final Instant timestamp;

        /**
         * Creates a new immutable view of a transaction.
         *
         * @param transaction the transaction to create a view of
         */
        ImmutableView(@NotNull Transaction transaction) {
            this.transaction = transaction;
            this.timestamp = Instant.now();
        }

        /**
         * Gets the account associated with this transaction.
         *
         * @return the account
         */
        @NotNull
        public Account account() {
            return transaction.account;
        }

        /**
         * Gets the amount of this transaction.
         *
         * @return the amount
         */
        @NotNull
        public BigDecimal amount() {
            return transaction.amount;
        }

        /**
         * Gets the operation type of this transaction.
         *
         * @return the operation
         */
        @NotNull
        public Operation operation() {
            return transaction.operation;
        }

        /**
         * Gets the timestamp when this immutable view was created.
         *
         * @return the timestamp
         */
        @NotNull
        public Instant timestamp() {
            return timestamp;
        }
    }
}
