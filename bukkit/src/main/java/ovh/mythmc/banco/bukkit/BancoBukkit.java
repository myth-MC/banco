package ovh.mythmc.banco.bukkit;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.PluginCommand;
import org.checkerframework.checker.nullness.qual.NonNull;
import ovh.mythmc.banco.bukkit.commands.BalanceCommand;
import ovh.mythmc.banco.bukkit.commands.BancoCommand;
import ovh.mythmc.banco.common.impl.BancoHelperImpl;
import ovh.mythmc.banco.common.BancoPlaceholderExpansion;
import ovh.mythmc.banco.common.impl.BancoVaultImpl;
import ovh.mythmc.banco.common.boot.BancoBootstrap;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.logger.LoggerWrapper;
import ovh.mythmc.banco.common.listeners.BancoListener;
import ovh.mythmc.banco.common.listeners.EntityDeathListener;
import ovh.mythmc.banco.common.listeners.PlayerJoinListener;
import ovh.mythmc.banco.common.listeners.PlayerQuitListener;
import ovh.mythmc.banco.common.translation.BancoTranslation;
import ovh.mythmc.banco.bukkit.commands.PayCommand;

import java.io.IOException;
import java.util.*;

@Getter
public final class BancoBukkit extends BancoBootstrap<BancoBukkitPlugin> {

    public static BancoBukkit instance;

    private static BukkitAudiences adventure;

    private BancoVaultImpl vaultImpl;

    private BukkitTask autoSaveTask;

    private final LoggerWrapper logger = new LoggerWrapper() {
        @Override
        public void info(String message, Object... args) {
            getPlugin().getLogger().info(buildFullMessage(message, args));
        }

        @Override
        public void warn(String message, Object... args) {
            getPlugin().getLogger().warning(buildFullMessage(message, args));
        }

        @Override
        public void error(String message, Object... args) {
            getPlugin().getLogger().severe(buildFullMessage(message, args));
        }
    };

    public BancoBukkit(final @NotNull BancoBukkitPlugin plugin) {
        super(plugin, plugin.getDataFolder());
        instance = this;
    }

    @Override
    public void enable() {
        new BancoTranslation().load(getPlugin().getDataFolder());

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
            new BancoPlaceholderExpansion();

        vaultImpl = new BancoVaultImpl();
        vaultImpl.hook(getPlugin());

        new BancoHelperImpl(getPlugin()); // BancoHelper.get()

        adventure = BukkitAudiences.create(getPlugin());

        registerListeners();
        registerCommands();

        if (Banco.get().getConfig().getSettings().getAutoSave().enabled())
            startAutoSaver();
    }

    @Override
    public void shutdown() {
        vaultImpl.unhook();

        if (autoSaveTask != null)
            stopAutoSaver();

        try {
            Banco.get().getStorage().save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String version() {
        return getPlugin().getDescription().getVersion();
    }

    private void registerListeners() {
        // Bukkit listeners
        if (Banco.get().getConfig().getSettings().getCurrency().removeDrops())
            Bukkit.getPluginManager().registerEvents(new EntityDeathListener(), getPlugin());
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), getPlugin());
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), getPlugin());

        // banco listeners
        Banco.get().getEventManager().registerListener(new BancoListener());
    }

    private void registerCommands() {
        PluginCommand banco = getPlugin().getCommand("banco");
        PluginCommand balance = getPlugin().getCommand("balance");
        PluginCommand pay = getPlugin().getCommand("pay");

        Objects.requireNonNull(banco).setExecutor(new BancoCommand());
        Objects.requireNonNull(balance).setExecutor(new BalanceCommand());
        Objects.requireNonNull(pay).setExecutor(new PayCommand());

        if (!Banco.get().getConfig().getSettings().getCommands().balanceEnabled())
            balance.setPermission("banco.admin");

        if (!Banco.get().getConfig().getSettings().getCommands().payEnabled())
            pay.setPermission("banco.admin");
    }

    private void startAutoSaver() {
        this.autoSaveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(getPlugin(), () -> {
            try {
                Banco.get().getStorage().save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, 0, Banco.get().getConfig().getSettings().getAutoSave().frequency() * 20L);
    }

    private void stopAutoSaver() {
        this.autoSaveTask.cancel();
    }

    public static @NonNull BukkitAudiences adventure() {
        if(adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return adventure;
    }

}
