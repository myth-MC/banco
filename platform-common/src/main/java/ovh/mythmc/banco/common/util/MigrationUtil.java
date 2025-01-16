package ovh.mythmc.banco.common.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.Account;
import ovh.mythmc.banco.api.data.BancoDataProvider;

import java.io.File;
import java.nio.file.Files;

@SuppressWarnings("deprecation")
public final class MigrationUtil {

    private final File pluginFolder;

    public MigrationUtil(final @NotNull File pluginFolder) {
        this.pluginFolder = pluginFolder;
    }

    public void data() {
        BancoDataProvider provider = new BancoDataProvider(pluginFolder);
        if (!Files.exists(provider.getDataFilePath()))
            return;
            
        provider.load();

        provider.get().getAccounts().forEach(legacyAccount -> {
            Player player = Bukkit.getPlayer(legacyAccount.getUuid());
            String name = player.getName();
            Account account = new Account(legacyAccount.getUuid(),name, legacyAccount.getAmount(), legacyAccount.getTransactions());
            Banco.get().getAccountManager().create(account);
        });

        provider.move();
    }

}
