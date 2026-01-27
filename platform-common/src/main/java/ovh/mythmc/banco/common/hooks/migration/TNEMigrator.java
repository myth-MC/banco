package ovh.mythmc.banco.common.hooks.migration;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

import net.tnemc.core.TNECore;
import net.tnemc.core.currency.Currency;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.AccountIdentifierKey;

public class TNEMigrator implements Migrator {

    @Override
    public @NotNull String pluginName() {
        return "TheNewEconomy";
    }

    @Override
    public @NotNull CompletableFuture<Map<AccountIdentifierKey, BigDecimal>> asCompletableFuture() {
        return CompletableFuture.supplyAsync(() -> {
            Map<AccountIdentifierKey, BigDecimal> accountMap = new HashMap<>();

            Currency defaultCurrency = TNECore.eco().currency().defaultCurrency();

            TNECore.eco().account().getAccounts().values().forEach(account -> {
                UUID uuid = account.getIdentifier();
                String name = account.getName();
                BigDecimal balance = BigDecimal.ZERO;

                for (String worldName : Banco.get().getSettings().get().getMigration().getWorlds()) {
                    BigDecimal worldHoldingsTotal = account.getHoldingsTotal(worldName, defaultCurrency.getUid());
                    if (worldHoldingsTotal == null)
                        continue;

                    balance = balance.add(account.getHoldingsTotal(worldName, defaultCurrency.getUid()));
                }
    
                accountMap.put(AccountIdentifierKey.of(uuid, name), balance);
            });

            return accountMap;
        });
    }
    
}
