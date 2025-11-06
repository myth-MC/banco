package ovh.mythmc.banco.api.accounts;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
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

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public final class AccountManager {

    @Getter
    private final LocalUUIDResolver uuidResolver;

    @Getter
    private final AccountDatabase database = new AccountDatabase();

    @Getter
    private final TransactionHistory transactionHistory = new TransactionHistoryImpl();

    /**
     * Creates an account
     * @param uuid uuid of account to create and register
     */
    public synchronized void create(final @NotNull UUID uuid) {
        Account account = new Account();
        account.setUuid(uuid);

        create(account);
    }

    /**
     * Creates an account
     * @param uuid uuid of the account to create and register
     * @param name name of the account to create and register
     */
    public synchronized void create(final @NotNull UUID uuid, final @NotNull String name) {
        Account account = new Account();
        account.setUuid(uuid);
        account.setName(name);

        create(account);
    }

    /**
     * Creates an account
     * @param account account to create and register
     */
    public synchronized void create(final @NotNull Account account) {
        var callback = new BancoAccountRegister(account);
        BancoAccountRegisterCallback.INSTANCE.invoke(callback, result -> database.create(result.account()));
    }

    /**
     * Deletes an account
     * @param account account to delete and unregister
     */
    public synchronized void delete(final @NotNull Account account) {
        var callback = new BancoAccountUnregister(account);
        BancoAccountUnregisterCallback.INSTANCE.invoke(callback, result -> database.delete(result.account()));
    }

    /**
     * Deletes an account
     * @param uuid uuid of account to delete and unregister
     */
    public synchronized void delete(final @NotNull UUID uuid) {
        delete(getByUuid(uuid));
    }

    /**
     * Gets a list of registered accounts
     * @return List of registered accounts
     */
    public @NotNull List<Account> get() { return database.get(); }

    /**
     * Gets a specific account by its UUID
     * @param uuid UUID of the player
     * @return an account matching the UUID or null
     */
    public Account getByUuid(final @NotNull UUID uuid) {
        return database.getByUuid(uuid);
    }

    /**
     * Gets a specific account by its UUID
     * @param uuid UUID of the player
     * @return an account matching the UUID or null
     */
    public Account getByName(final @NotNull String name) {
        return database.getByNameOrUuid(name, uuidResolver.resolve(name).orElse(null));
    }

    /**
     * Deposits an amount of money to an account
     * @param uuid account uuid that will be modified
     * @param amount amount of money to deposit
     */
    public void deposit(final @NotNull UUID uuid, final @NotNull BigDecimal amount) {
        deposit(getByUuid(uuid), amount);
    }

    /**
     * Deposits an amount of money to an account
     * @param uuid account uuid that will be modified
     * @param amount amount of money to deposit
     * @param loggable whether this transaction can be logged
     */
    public void deposit(final @NotNull UUID uuid, final @NotNull BigDecimal amount, final boolean loggable) {
        deposit(getByUuid(uuid), amount, loggable);
    }

    /**
     * Deposits an amount of money to an account
     * @param account account that will be modified
     * @param amount amount of money to deposit
     */
    public void deposit(final @NotNull Account account, final @NotNull BigDecimal amount) {
        deposit(account, amount, true);
    }

    /**
     * Deposits an amount of money to an account
     * @param account account that will be modified
     * @param amount amount of money to deposit
     * @param loggable whether this transaction can be logged
     */
    public void deposit(final @NotNull Account account, final @NotNull BigDecimal amount, final boolean loggable) {
        Transaction transaction = Transaction.builder()
            .account(account)
            .amount(amount)
            .operation(Operation.DEPOSIT)
            .loggable(loggable)
            .build();

        transaction.queue();
    }

    /**
     * Withdraws an amount of money from an account
     * @param account account uuid that will be modified
     * @param amount amount of money to withdraw
     */
    public void withdraw(final @NotNull UUID uuid, final @NotNull BigDecimal amount) {
        withdraw(getByUuid(uuid), amount);
    }

    /**
     * Withdraws an amount of money from an account
     * @param account account uuid that will be modified
     * @param amount amount of money to withdraw
     * @param loggable whether this transaction can be logged
     */
    public void withdraw(final @NotNull UUID uuid, final @NotNull BigDecimal amount, final boolean loggable) {
        withdraw(getByUuid(uuid), amount, loggable);
    }

    /**
     * Withdraws an amount of money from an account
     * @param account account that will be modified
     * @param amount amount of money to withdraw
     */
    public void withdraw(final @NotNull Account account, final @NotNull BigDecimal amount) {
        withdraw(account, amount, true);
    }

    /**
     * Withdraws an amount of money from an account
     * @param account account that will be modified
     * @param amount amount of money to withdraw
     * @param loggable whether this transaction can be logged
     */
    public void withdraw(final @NotNull Account account, final @NotNull BigDecimal amount, final boolean loggable) {
        Transaction transaction = Transaction.builder()
            .account(account)
            .amount(amount)
            .operation(Operation.WITHDRAW)
            .loggable(loggable)
            .build();

        transaction.queue();
    }

    /**
     * Sets an account's balance to a specfic amount
     * @param account account that will be modified
     * @param amount amount of money to set
     */
    public void set(final @NotNull UUID uuid, final @NotNull BigDecimal amount) {
        set(getByUuid(uuid), amount);
    }

    /**
     * Sets an account's balance to a specific amount
     * @param account account that will be modified
     * @param amount amount of money to set
     */
    public void set(final @NotNull Account account, final @NotNull BigDecimal amount) {
        Transaction transaction = Transaction.builder()
            .account(account)
            .amount(amount)
            .operation(Operation.SET)
            .build();

        transaction.queue();
    }

    /**
     * Checks if an account has an amount of money
     * @param uuid uuid of account to check
     * @param amount amount to check
     * @return true if account has more than the specified amount
     */
    public boolean has(final @NotNull UUID uuid, final @NotNull BigDecimal amount) {
        return has(getByUuid(uuid), amount);
    }

    /**
     * Checks if an account has an amount of money
     * @param uuid uuid of account to check
     * @param amount amount to check
     * @return true if account has more than the specified amount
     */
    public boolean has(final @NotNull Account account, final @NotNull BigDecimal amount) {
        return account.amount().compareTo(amount) >= 0;
    }

    /**
     * Gets an account's balance
     * @param uuid uuid of account to check
     * @return Account's balance
     */
    public @NotNull BigDecimal amount(final @NotNull UUID uuid) {
        return amount(getByUuid(uuid));
    }

    /**
     * Gets an account's balance
     * @param account account to check
     * @return Account's balance
     */
    public @NotNull BigDecimal amount(final @NotNull Account account) {
        // Fake players / accounts
        if (!Bukkit.getOfflinePlayer(account.getUuid()).hasPlayedBefore())
            return account.getTransactions().add(getValueOfPlayer(account.getUuid(), false));

        final Optional<OfflinePlayerReference> optionalOfflinePlayerReference = uuidResolver.resolveOfflinePlayer(account.getUuid());

        // Update balance
        if (optionalOfflinePlayerReference.isPresent() && // Safeguard against weird thing that apparently can happen in laggy server environments?
            uuidResolver.resolveOfflinePlayer(account.getUuid()).get().toOfflinePlayer().isOnline()) {

            account.setAmount(getValueOfPlayer(account.getUuid(), true));
            database.updateCache(account);
        }

        // Offline players
        return account.getAmount().add(account.getTransactions());
    }

    @Internal
    private synchronized BigDecimal getValueOfPlayer(final @NotNull UUID uuid, boolean isOnline) {
        BigDecimal value = BigDecimal.valueOf(0);

        for (BancoStorage storage : Banco.get().getStorageRegistry().getByOrder()) {
            if (!isOnline && !storage.supportsOfflinePlayers())
                continue;

            value = value.add(storage.value(uuid));
        }

        return value;
    }

    @ApiStatus.Internal
    public synchronized void updateTransactions(final @NotNull Account account) {
        BigDecimal amount = account.amount();
        account.setTransactions(BigDecimal.valueOf(0));
        database.updateCache(account);

        set(account.getUuid(), amount);
    }

    @ApiStatus.Internal
    public void updateName(final @NotNull Account account, String newName) {
        account.setName(newName);
        database.updateCache(account);
    }

    /**
     * Gets a CompletableFuture containing a LinkedHashMap ordered by players with the highest balance
     * @param limit how many entries should we look before returning the LinkedHashMap
     * @return A LinkedHashMap ordered by players with the highest balance
     */
    public CompletableFuture<LinkedHashMap<UUID, BigDecimal>> getTopAsync(int limit) {
        return CompletableFuture.supplyAsync(() -> {
            return getTop(limit);
        });
    }

    /**
     * Gets a LinkedHashMap ordered by players with the highest balance
     * @param limit how many entries should we look before returning the LinkedHashMap
     * @return A LinkedHashMap ordered by players with the highest balance
     */
    public LinkedHashMap<UUID, BigDecimal> getTop(int limit) {
        Map<UUID, BigDecimal> values = new LinkedHashMap<>();
        for (Account account : get()) {
            values.put(account.getUuid(), account.amount());
        }

        return values.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(limit)
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new
                ));
    }

    /**
     * Gets the player at top position
     * @param position position at the top ladder
     * @return A LinkedHashMap ordered by players with the highest balance
     */
    public Map.Entry<UUID, BigDecimal> getTopPosition(int position) {
        return getTop(position).lastEntry();
    }

}
