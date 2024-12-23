package ovh.mythmc.banco.api.accounts;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.events.impl.BancoTransactionEvent;
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
        // Call event
        BancoTransactionEvent event = new BancoTransactionEvent(this);
        event.callAsync();

        // Update values in case they've been changed
        account = event.transaction().account();
        operation = event.transaction().operation();
        amount = event.transaction().amount();

        switch (operation) {
            case DEPOSIT -> {
                set(account.amount().add(amount));
            }
            case WITHDRAW -> {
                set(account.amount().subtract(amount));
            }
            case SET -> {
                set(amount);
            }
        }
    }

    public void queue() {
        BancoScheduler.get().queueTransaction(this);
    }

    private void set(@NotNull BigDecimal newAmount) {
        if (account.amount().compareTo(newAmount) == 0)
            return;

        if (account.amount().compareTo(newAmount) < 0) { // Add amount to account
            BigDecimal toAdd = newAmount.subtract(account.amount());

            // Online accounts
            if (Bukkit.getOfflinePlayer(account.getUuid()).isOnline()) {
                account.setTransactions(BigDecimal.valueOf(0));

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
                account.setTransactions(BigDecimal.valueOf(0));

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

}
