package ovh.mythmc.banco.api.accounts;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.Transaction.Operation;
import ovh.mythmc.banco.api.accounts.service.LocalUUIDResolver;
import ovh.mythmc.banco.api.accounts.service.OfflinePlayerReference;
import ovh.mythmc.banco.api.callback.account.BancoAccountRegister;
import ovh.mythmc.banco.api.callback.account.BancoAccountUnregister;
import ovh.mythmc.banco.api.callback.account.BancoAccountRegisterCallback;
import ovh.mythmc.banco.api.callback.account.BancoAccountUnregisterCallback;
import ovh.mythmc.banco.api.storage.BancoStorage;
import ovh.mythmc.banco.api.scheduler.BancoScheduler;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Manages account operations including creation, deletion, and financial transactions.
 * <p>
 * This class provides a high-level API for managing player accounts and their balances.
 * All financial operations are processed through the transaction queue system, except
 * for accounts with null or "NULL" names which are processed synchronously.
 * </p>
 *
 * @since 1.0.0
 */
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public final class AccountManager {

    @Getter
    private final LocalUUIDResolver uuidResolver;

    @Getter
    private final AccountDatabase database = new AccountDatabase();

    @Getter
    private final TransactionHistory transactionHistory = new TransactionHistoryImpl();

    /**
     * Creates an account with the specified UUID.
     *
     * @param uuid uuid of account to create and register
     * @throws IllegalArgumentException if uuid is null
     */
    public synchronized void create(final @NotNull UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }

        final Account account = new Account();
        account.setUuid(uuid);
        account.setAmount(BigDecimal.ZERO);
        account.setTransactions(BigDecimal.ZERO);

        create(account);
    }

    /**
     * Creates an account with the specified UUID and name.
     *
     * @param uuid uuid of the account to create and register
     * @param name name of the account to create and register
     * @throws IllegalArgumentException if uuid is null or name is null/empty
     */
    public synchronized void create(final @NotNull UUID uuid, final @NotNull String name) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }

        final Account account = new Account();
        account.setUuid(uuid);
        account.setName(name);
        account.setAmount(BigDecimal.ZERO);
        account.setTransactions(BigDecimal.ZERO);

        create(account);
    }

    /**
     * Creates an account.
     *
     * @param account account to create and register
     * @throws IllegalArgumentException if account is null
     */
    public synchronized void create(final @NotNull Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }

        final var callback = new BancoAccountRegister(account);
        BancoAccountRegisterCallback.INSTANCE.invoke(callback, result -> database.create(result.account()));
    }

    /**
     * Deletes an account.
     *
     * @param account account to delete and unregister
     * @throws IllegalArgumentException if account is null
     */
    public synchronized void delete(final @NotNull Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }

        final var callback = new BancoAccountUnregister(account);
        BancoAccountUnregisterCallback.INSTANCE.invoke(callback, result -> database.delete(result.account()));
    }

    /**
     * Deletes an account.
     *
     * @param uuid uuid of account to delete and unregister
     * @throws IllegalArgumentException if uuid is null
     */
    public synchronized void delete(final @NotNull UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }

        final Account account = getByUuid(uuid);
        if (account != null) {
            delete(account);
        }
    }

    /**
     * Gets a list of registered accounts.
     *
     * @return List of registered accounts
     */
    @NotNull
    public List<Account> get() {
        final List<Account> accounts = database.get();
        return accounts != null ? accounts : List.of();
    }

    /**
     * Gets a specific account by its UUID.
     *
     * @param uuid UUID of the player
     * @return an account matching the UUID or null
     * @throws IllegalArgumentException if uuid is null
     */
    @Nullable
    public Account getByUuid(final @NotNull UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }
        return database.getByUuid(uuid);
    }

    /**
     * Gets a specific account by its name.
     *
     * @param name the name of the account
     * @return an account matching the name or null
     * @throws IllegalArgumentException if name is null or empty
     */
    @Nullable
    public Account getByName(final @NotNull String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return database.getByNameOrUuid(name, uuidResolver.resolve(name).orElse(null));
    }

    /**
     * Deposits an amount of money to an account.
     *
     * @param uuid account uuid that will be modified
     * @param amount amount of money to deposit
     * @throws IllegalArgumentException if uuid or amount is null
     */
    public void deposit(final @NotNull UUID uuid, final @NotNull BigDecimal amount) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }

        final Account account = getByUuid(uuid);
        if (account != null) {
            deposit(account, amount);
        }
    }

    /**
     * Deposits an amount of money to an account.
     *
     * @param uuid account uuid that will be modified
     * @param amount amount of money to deposit
     * @param loggable whether this transaction can be logged
     * @throws IllegalArgumentException if uuid or amount is null
     */
    public void deposit(final @NotNull UUID uuid, final @NotNull BigDecimal amount, final boolean loggable) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }

        final Account account = getByUuid(uuid);
        if (account != null) {
            deposit(account, amount, loggable);
        }
    }

    /**
     * Deposits an amount of money to an account.
     *
     * @param account account that will be modified
     * @param amount amount of money to deposit
     * @throws IllegalArgumentException if account or amount is null
     */
    public void deposit(final @NotNull Account account, final @NotNull BigDecimal amount) {
        deposit(account, amount, true);
    }

    /**
     * Deposits an amount of money to an account.
     * <p>
     * For accounts with null or "NULL" names, the transaction is executed synchronously
     * to avoid queue delays. For other accounts, the transaction is queued.
     * </p>
     *
     * @param account account that will be modified
     * @param amount amount of money to deposit
     * @param loggable whether this transaction can be logged
     * @throws IllegalArgumentException if account or amount is null
     */
    public void deposit(final @NotNull Account account, final @NotNull BigDecimal amount, final boolean loggable) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }

        final Transaction transaction = Transaction.builder()
            .account(account)
            .amount(amount)
            .operation(Operation.DEPOSIT)
            .loggable(loggable)
            .build();

        // Decide whether to perform synchronously or queue based on account name
        if (account.getName() == null || "NULL".equalsIgnoreCase(account.getName())) {
            Banco.get().getLogger().debug("AccountManager: performing synchronous DEPOSIT for acct={}", account.getUuid());
            // Remove pending queued transactions for this account to avoid stale duplicates
            if (BancoScheduler.get() != null) {
                BancoScheduler.get().cancelQueuedTransactionsFor(account.getUuid());
            }
            transaction.transact();
        } else {
            Banco.get().getLogger().debug("AccountManager: queueing DEPOSIT for acct={}", account.getUuid());
            transaction.queue();
        }
    }

    /**
     * Withdraws an amount of money from an account.
     *
     * @param uuid account uuid that will be modified
     * @param amount amount of money to withdraw
     * @throws IllegalArgumentException if uuid or amount is null
     */
    public void withdraw(final @NotNull UUID uuid, final @NotNull BigDecimal amount) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }

        final Account account = getByUuid(uuid);
        if (account != null) {
            withdraw(account, amount);
        }
    }

    /**
     * Withdraws an amount of money from an account.
     *
     * @param uuid account uuid that will be modified
     * @param amount amount of money to withdraw
     * @param loggable whether this transaction can be logged
     * @throws IllegalArgumentException if uuid or amount is null
     */
    public void withdraw(final @NotNull UUID uuid, final @NotNull BigDecimal amount, final boolean loggable) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }

        final Account account = getByUuid(uuid);
        if (account != null) {
            withdraw(account, amount, loggable);
        }
    }

    /**
     * Withdraws an amount of money from an account.
     *
     * @param account account that will be modified
     * @param amount amount of money to withdraw
     * @throws IllegalArgumentException if account or amount is null
     */
    public void withdraw(final @NotNull Account account, final @NotNull BigDecimal amount) {
        withdraw(account, amount, true);
    }

    /**
     * Withdraws an amount of money from an account.
     * <p>
     * For accounts with null or "NULL" names, the transaction is executed synchronously
     * to avoid queue delays. For other accounts, the transaction is queued.
     * </p>
     *
     * @param account account that will be modified
     * @param amount amount of money to withdraw
     * @param loggable whether this transaction can be logged
     * @throws IllegalArgumentException if account or amount is null
     */
    public void withdraw(final @NotNull Account account, final @NotNull BigDecimal amount, final boolean loggable) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }

        final Transaction transaction = Transaction.builder()
            .account(account)
            .amount(amount)
            .operation(Operation.WITHDRAW)
            .loggable(loggable)
            .build();

        if (account.getName() == null || "NULL".equalsIgnoreCase(account.getName())) {
            Banco.get().getLogger().debug("AccountManager: performing synchronous WITHDRAW for acct={}", account.getUuid());
            if (BancoScheduler.get() != null) {
                BancoScheduler.get().cancelQueuedTransactionsFor(account.getUuid());
            }
            transaction.transact();
        } else {
            Banco.get().getLogger().debug("AccountManager: queueing WITHDRAW for acct={}", account.getUuid());
            transaction.queue();
        }
    }

    /**
     * Sets an account's balance to a specific amount.
     *
     * @param uuid account uuid that will be modified
     * @param amount amount of money to set
     * @throws IllegalArgumentException if uuid or amount is null
     */
    public void set(final @NotNull UUID uuid, final @NotNull BigDecimal amount) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }

        final Account account = getByUuid(uuid);
        if (account != null) {
            set(account, amount);
        }
    }

    /**
     * Sets an account's balance to a specific amount.
     * <p>
     * For accounts with null or "NULL" names, the transaction is executed synchronously
     * to avoid queue delays. For other accounts, the transaction is queued.
     * </p>
     *
     * @param account account that will be modified
     * @param amount amount of money to set
     * @throws IllegalArgumentException if account or amount is null
     */
    public void set(final @NotNull Account account, final @NotNull BigDecimal amount) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }

        final Transaction transaction = Transaction.builder()
            .account(account)
            .amount(amount)
            .operation(Operation.SET)
            .build();

        if (account.getName() == null || "NULL".equalsIgnoreCase(account.getName())) {
            Banco.get().getLogger().debug("AccountManager: performing synchronous SET for acct={}", account.getUuid());
            if (BancoScheduler.get() != null) {
                BancoScheduler.get().cancelQueuedTransactionsFor(account.getUuid());
            }
            transaction.transact();
        } else {
            Banco.get().getLogger().debug("AccountManager: queueing SET for acct={}", account.getUuid());
            transaction.queue();
        }
    }

    /**
     * Checks if an account has an amount of money.
     *
     * @param uuid uuid of account to check
     * @param amount amount to check
     * @return true if account has more than the specified amount
     * @throws IllegalArgumentException if uuid or amount is null
     */
    public boolean has(final @NotNull UUID uuid, final @NotNull BigDecimal amount) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }

        final Account account = getByUuid(uuid);
        return account != null && has(account, amount);
    }

    /**
     * Checks if an account has an amount of money.
     *
     * @param account account to check
     * @param amount amount to check
     * @return true if account has more than the specified amount
     * @throws IllegalArgumentException if account or amount is null
     */
    public boolean has(final @NotNull Account account, final @NotNull BigDecimal amount) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }

        return account.balance().compareTo(amount) >= 0;
    }

    /**
     * Gets an account's balance.
     *
     * @param uuid uuid of account to check
     * @return Account's balance, or zero if account doesn't exist
     * @deprecated As of version 1.3.0, use {@link #balance(UUID)} instead.
     */
    @Deprecated
    public @NotNull BigDecimal amount(final @NotNull UUID uuid) {
        return balance(uuid);
    }

    /**
     * Gets an account's balance.
     *
     * @param account account to check
     * @return Account's balance
     * @deprecated As of version 1.3.0, use {@link #balance(Account)} instead.
     */
    @Deprecated
    public @NotNull BigDecimal amount(final @NotNull Account account) {
        return balance(account);
    }

    /**
     * Gets an account's balance.
     *
     * @param uuid uuid of account to check
     * @return Account's balance, or zero if account doesn't exist
     * @throws IllegalArgumentException if uuid is null
     */
    public @NotNull BigDecimal balance(final @NotNull UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }

        final Account account = getByUuid(uuid);
        return account != null ? balance(account) : BigDecimal.ZERO;
    }

    /**
     * Gets an account's balance.
     * <p>
     * The balance includes pending transactions, value stored in all registered storage
     * systems, and the account's base balance. For online players, the balance is
     * updated from storage systems before being returned.
     * </p>
     *
     * @param account account to check
     * @return Account's balance
     * @throws IllegalArgumentException if account is null
     */
    public @NotNull BigDecimal balance(final @NotNull Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }

        final Optional<OfflinePlayerReference> optionalOfflinePlayerReference = uuidResolver.resolveOfflinePlayer(account.getUuid());

        // Update balance for online players
        if (optionalOfflinePlayerReference.isPresent()) {
            final OfflinePlayerReference reference = optionalOfflinePlayerReference.get();
            if (reference.toOfflinePlayer().isOnline()) {
                account.setAmount(getValueOfPlayer(account.getUuid(), true));
                database.updateCache(account);
            }
        }

        // Calculate total balance: transactions + storage value + base balance
        return account.getTransactions()
            .add(getValueOfPlayer(account.getUuid(), false))
            .add(account.getAmount());
    }

    /**
     * Calculates the total value stored in all registered storage systems for a player.
     *
     * @param uuid the UUID of the player
     * @param isOnline whether the player is currently online
     * @return the total value stored in all applicable storage systems
     */
    @Internal
    private synchronized BigDecimal getValueOfPlayer(final @NotNull UUID uuid, boolean isOnline) {
        BigDecimal value = BigDecimal.ZERO;

        for (final BancoStorage storage : Banco.get().getStorageRegistry().getByOrder()) {
            if (!isOnline && !storage.supportsOfflinePlayers()) {
                continue;
            }

            value = value.add(storage.value(uuid));
        }

        return value;
    }

    /**
     * Updates an account's transactions by consolidating the current balance.
     *
     * @param account the account to update
     * @throws IllegalArgumentException if account is null
     */
    @ApiStatus.Internal
    public synchronized void updateTransactions(final @NotNull Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }

        final BigDecimal currentAmount = balance(account);
        account.setTransactions(BigDecimal.ZERO);
        database.updateCache(account);

        set(account.getUuid(), currentAmount);
    }

    /**
     * Updates an account's name.
     *
     * @param account the account to update
     * @param newName the new name for the account
     * @throws IllegalArgumentException if account is null or newName is null/empty
     */
    @ApiStatus.Internal
    public void updateName(final @NotNull Account account, @NotNull String newName) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("New name cannot be null or empty");
        }

        account.setName(newName);
        database.updateCache(account);
    }

    /**
     * Gets a CompletableFuture containing a LinkedHashMap ordered by players with the highest balance.
     *
     * @param limit how many entries should we look before returning the LinkedHashMap
     * @return A LinkedHashMap ordered by players with the highest balance
     * @throws IllegalArgumentException if limit is negative
     */
    public CompletableFuture<LinkedHashMap<UUID, BigDecimal>> getTopAsync(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("Limit cannot be negative");
        }

        return CompletableFuture.supplyAsync(() -> getTop(limit));
    }

    /**
     * Gets a LinkedHashMap ordered by players with the highest balance.
     *
     * @param limit how many entries should we look before returning the LinkedHashMap
     * @return A LinkedHashMap ordered by players with the highest balance
     * @throws IllegalArgumentException if limit is negative
     */
    public LinkedHashMap<UUID, BigDecimal> getTop(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("Limit cannot be negative");
        }

        final Map<UUID, BigDecimal> values = new LinkedHashMap<>();
        for (final Account account : get()) {
            values.put(account.getUuid(), account.balance());
        }

        return values.entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .limit(limit)
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new
            ));
    }

    /**
     * Gets the player at top position.
     *
     * @param position position at the top ladder (1-indexed)
     * @return the entry at the specified position, or null if position doesn't exist
     * @throws IllegalArgumentException if position is less than 1
     */
    @Nullable
    public Map.Entry<UUID, BigDecimal> getTopPosition(int position) {
        if (position < 1) {
            throw new IllegalArgumentException("Position must be at least 1");
        }

        final LinkedHashMap<UUID, BigDecimal> top = getTop(position);
        if (top.isEmpty()) {
            return null;
        }

        return top.entrySet().stream()
            .skip(position - 1)
            .findFirst()
            .orElse(null);
    }
}
