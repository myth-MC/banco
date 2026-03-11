package ovh.mythmc.banco.common.hooks.migration;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.api.accounts.AccountIdentifierKey;

public interface Migrator {

    @NotNull String pluginName();

    @NotNull CompletableFuture<Map<AccountIdentifierKey, BigDecimal>> asCompletableFuture();
    
}
