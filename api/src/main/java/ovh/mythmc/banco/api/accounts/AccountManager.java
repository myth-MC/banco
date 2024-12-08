package ovh.mythmc.banco.api.accounts;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.storage.BancoStorage;
import ovh.mythmc.banco.api.events.impl.BancoAccountRegisterEvent;
import ovh.mythmc.banco.api.events.impl.BancoAccountUnregisterEvent;
import ovh.mythmc.banco.api.events.impl.BancoTransactionEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AccountManager {

    public static final AccountManager instance = new AccountManager();

    @Getter
    private final AccountDatabase database = new AccountDatabase();

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
     * @param account account to create and register
     */
    public synchronized void create(final @NotNull Account account) {
        BancoAccountRegisterEvent event = new BancoAccountRegisterEvent(account);
        event.callAsync();

        database.create(event.account());
    }

    /**
     * Deletes an account
     * @param account account to delete and unregister
     */
    public synchronized void delete(final @NotNull Account account) {
        BancoAccountUnregisterEvent event = new BancoAccountUnregisterEvent(account);
        event.callAsync();

        database.delete(event.account());
    }

    /**
     * Deletes an account
     * @param uuid uuid of account to delete and unregister
     */
    public synchronized void delete(final @NotNull UUID uuid) {
        delete(get(uuid));
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
    public Account get(final @NotNull UUID uuid) {
        return database.getByUuid(uuid);
    }

    /**
     * Deposits an amount of money to an account
     * @param uuid account uuid that will be modified
     * @param amount amount of money to deposit
     */
    public synchronized void deposit(final @NotNull UUID uuid, final @NotNull BigDecimal amount) {
        set(uuid, get(uuid).amount().add(amount));
    }

    /**
     * Deposits an amount of money to an account
     * @param account account that will be modified
     * @param amount amount of money to deposit
     */
    public synchronized void deposit(final @NotNull Account account, final @NotNull BigDecimal amount) {
        deposit(account.getUuid(), amount);
    }

    /**
     * Withdraws an amount of money from an account
     * @param account account uuid that will be modified
     * @param amount amount of money to withdraw
     */
    public synchronized void withdraw(final @NotNull UUID uuid, final @NotNull BigDecimal amount) {
        set(uuid, get(uuid).amount().subtract(amount));
    }

    /**
     * Withdraws an amount of money from an account
     * @param account account that will be modified
     * @param amount amount of money to withdraw
     */
    public synchronized void withdraw(final @NotNull Account account, final @NotNull BigDecimal amount) {
        withdraw(account.getUuid(), amount);
    }

    /**
     * Sets an account's balance to a specfic amount
     * @param account account that will be modified
     * @param amount amount of money to set
     */
    public synchronized void set(final @NotNull UUID uuid, final @NotNull BigDecimal amount) {
        Account account = get(uuid);

        if (account.amount().compareTo(amount) == 0)
            return;

        if (account.amount().compareTo(amount) < 0) { // Add amount to account
            if (Bukkit.getOfflinePlayer(account.getUuid()).isOnline()) {
                account.setTransactions(BigDecimal.valueOf(0));
                BigDecimal toAdd = amount.subtract(account.amount());

                // Call BancoTransactionEvent
                BancoTransactionEvent event = new BancoTransactionEvent(account, toAdd);
                event.callAsync();

                // Update values in case they've been changed
                account = event.account();
                toAdd = event.amount();

                // Add to all BancoStorage instances
                for (BancoStorage storage : Banco.get().getStorageManager().get())
                    if (toAdd.compareTo(BigDecimal.valueOf(0)) > 0)
                        toAdd = toAdd.subtract(storage.add(account.getUuid(), toAdd));

                // Set transactions to remaining amount
                account.setTransactions(account.getTransactions().add(toAdd.setScale(2, RoundingMode.HALF_UP)));
                database.update(account);
                return;
            }

            // Register transaction if player is not online
            account.setTransactions(account.getTransactions().add(amount.subtract(account.amount())));
            database.update(account);
        } else { // Remove amount from account
            if (Bukkit.getOfflinePlayer(account.getUuid()).isOnline()) {
                account.setTransactions(BigDecimal.valueOf(0));
                BigDecimal toRemove = account.amount().subtract(amount);

                // Call BancoTransactionEvent
                BancoTransactionEvent event = new BancoTransactionEvent(account, toRemove.negate());
                event.callAsync();

                // Update values in case they've been changed
                account = event.account();
                toRemove = event.amount().negate();

                // Remove from all BancoStorage instances
                for (BancoStorage storage : Banco.get().getStorageManager().get())
                    if (toRemove.compareTo(BigDecimal.valueOf(0)) > 0)
                        toRemove = storage.remove(account.getUuid(), toRemove);

                // Set transactions to remaining amount
                account.setTransactions(account.getTransactions().subtract(toRemove.setScale(2, RoundingMode.HALF_UP)));
                database.update(account);
                return;
            }

            // Register transaction if player is not online
            account.setTransactions(account.getTransactions().subtract(account.amount().subtract(amount)));
            database.update(account);
        }
    }

    /**
     * Sets an account's balance to a specific amount
     * @param account account that will be modified
     * @param amount amount of money to set
     */
    public synchronized void set(final @NotNull Account account, final @NotNull BigDecimal amount) {
        set(account.getUuid(), amount);
    }

    /**
     * Checks if an account has an amount of money
     * @param uuid uuid of account to check
     * @param amount amount to check
     * @return true if account has more than the specified amount
     */
    public boolean has(final @NotNull UUID uuid, final @NotNull BigDecimal amount) {
        return get(uuid).amount().compareTo(amount) >= 0;
    }

    /**
     * Checks if an account has an amount of money
     * @param uuid uuid of account to check
     * @param amount amount to check
     * @return true if account has more than the specified amount
     */
    public boolean has(final @NotNull Account account, final @NotNull BigDecimal amount) {
        return has(account.getUuid(), amount);
    }

    /**
     * Gets an account's balance
     * @param uuid uuid of account to check
     * @return Account's balance
     */
    public @NotNull BigDecimal amount(final @NotNull UUID uuid) {
        Account account = get(uuid);
        if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
            account.setAmount(getValueOfOnlinePlayer(uuid));
            database.update(account);
        }

        return account.getAmount().add(account.getTransactions());
    }

    /**
     * Gets an account's balance
     * @param account account to check
     * @return Account's balance
     */
    public @NotNull BigDecimal amount(final @NotNull Account account) {
        return amount(account.getUuid());
    }

    private synchronized BigDecimal getValueOfOnlinePlayer(final @NotNull UUID uuid) {
        BigDecimal value = BigDecimal.valueOf(0);

        for (BancoStorage storage : Banco.get().getStorageManager().get()) {
            value = value.add(storage.value(uuid));
        }

        return value;
    }

    @ApiStatus.Internal
    public synchronized void updateTransactions(final @NotNull Account account) {
        BigDecimal amount = account.amount();
        account.setTransactions(BigDecimal.valueOf(0));
        database.update(account);

        set(account.getUuid(), amount);
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
