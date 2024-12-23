package ovh.mythmc.banco.bukkit;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.PluginCommand;

import ovh.mythmc.banco.bukkit.commands.BalanceCommandImpl;
import ovh.mythmc.banco.bukkit.commands.BalanceTopCommandImpl;
import ovh.mythmc.banco.bukkit.commands.BancoCommandImpl;
import ovh.mythmc.banco.common.boot.BancoBootstrap;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.logger.LoggerWrapper;
import ovh.mythmc.banco.api.scheduler.BancoScheduler;
import ovh.mythmc.banco.common.listeners.*;
import ovh.mythmc.gestalt.loader.BukkitGestaltLoader;
import ovh.mythmc.banco.bukkit.commands.PayCommandImpl;
import ovh.mythmc.banco.bukkit.scheduler.BancoSchedulerBukkit;

import java.util.*;

@Getter
public final class BancoBukkit extends BancoBootstrap {

    public static BancoBukkit instance;

    private static BukkitAudiences adventure;

    private BukkitGestaltLoader gestalt;

    private final BancoScheduler scheduler = new BancoSchedulerBukkit(getPlugin());

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
    public void loadGestalt() {
        gestalt = BukkitGestaltLoader.builder()
            .initializer(getPlugin())
            .build();

        gestalt.initialize();
    }

    @Override
    public void enable() {
        adventure = BukkitAudiences.create(getPlugin());

        registerListeners();
        registerCommands();

        scheduler.initialize();
    }

    @Override
    public void disable() {
        gestalt.terminate();
        scheduler.terminate();
    }

    @Override
    public BancoScheduler scheduler() {
        return scheduler;
    }

    @Override
    public String version() {
        return getPlugin().getDescription().getVersion();
    }

    private void registerListeners() {
        // Bukkit listeners
        if (Banco.get().getSettings().get().getCurrency().isRemoveDrops())
            Bukkit.getPluginManager().registerEvents(new ItemDropListener(), getPlugin());
        Bukkit.getPluginManager().registerEvents(new CustomItemListener(), getPlugin());
        Bukkit.getPluginManager().registerEvents(new PlayerListener(getPlugin()), getPlugin());
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), getPlugin());

        Bukkit.getPluginManager().registerEvents(new BancoListener(), getPlugin());
    }

    private void registerCommands() {
        PluginCommand banco = getPlugin().getCommand("banco");
        PluginCommand balance = getPlugin().getCommand("balance");
        PluginCommand balanceTop = getPlugin().getCommand("balancetop");
        PluginCommand pay = getPlugin().getCommand("pay");

        Objects.requireNonNull(banco).setExecutor(new BancoCommandImpl());
        Objects.requireNonNull(balance).setExecutor(new BalanceCommandImpl());
        Objects.requireNonNull(balanceTop).setExecutor(new BalanceTopCommandImpl());
        Objects.requireNonNull(pay).setExecutor(new PayCommandImpl());
    }

    public static @NotNull BukkitAudiences adventure() {
        if(adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return adventure;
    }

}
