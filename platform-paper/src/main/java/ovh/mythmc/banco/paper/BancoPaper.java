package ovh.mythmc.banco.paper;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;
import ovh.mythmc.banco.common.boot.BancoBootstrap;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.AccountManager;
import ovh.mythmc.banco.api.accounts.service.defaults.BukkitLocalUUIDResolver;
import ovh.mythmc.banco.api.logger.LoggerWrapper;
import ovh.mythmc.banco.api.scheduler.BancoScheduler;
import ovh.mythmc.banco.common.listeners.*;
import ovh.mythmc.banco.paper.commands.BalanceCommandImpl;
import ovh.mythmc.banco.paper.commands.BalanceTopCommandImpl;
import ovh.mythmc.banco.paper.commands.BancoCommandImpl;
import ovh.mythmc.banco.paper.commands.PayCommandImpl;
import ovh.mythmc.banco.paper.scheduler.BancoSchedulerPaper;
import ovh.mythmc.gestalt.loader.PaperGestaltLoader;

import java.util.*;

@Getter
public final class BancoPaper extends BancoBootstrap {

    public static BancoPaper instance;

    private PaperGestaltLoader gestalt;

    private final BancoScheduler scheduler = new BancoSchedulerPaper(getPlugin());

    private final BukkitLocalUUIDResolver uuidResolver;

    private final AccountManager accountManager;

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

    public BancoPaper(final @NotNull BancoPaperPlugin plugin) {
        super(plugin, plugin.getDataFolder());
        instance = this;

        // Register platform UUID resolver
        this.uuidResolver = new BukkitLocalUUIDResolver(scheduler);

        // Register platform account manager
        this.accountManager = new AccountManager(uuidResolver);
    }

    @Override
    public void loadGestalt() {
        gestalt = PaperGestaltLoader.builder()
            .initializer(getPlugin())
            .build();

        gestalt.initialize();
    }

    @Override
    public void enable() {    
        registerCommands();
        registerListeners();

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

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public String version() {
        return getPlugin().getPluginMeta().getVersion();
    }

    private void registerListeners() {
        // Paper listeners
        if (Banco.get().getSettings().get().getCurrency().isRemoveDrops())
            Bukkit.getPluginManager().registerEvents(new ItemDropListener(), getPlugin());
        Bukkit.getPluginManager().registerEvents(new CustomItemListener(), getPlugin());
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), getPlugin());
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), getPlugin());

        // UUID resolver
        Bukkit.getPluginManager().registerEvents(uuidResolver, getPlugin());
    }

    @SuppressWarnings("UnstableApiUsage")
    private void registerCommands() {
        LifecycleEventManager<Plugin> manager = getPlugin().getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register("banco", "Main command for managing banco accounts", new BancoCommandImpl());

            // Optional commands
            if (Banco.get().getSettings().get().getCommands().getBalance().enabled())
                commands.register("balance", List.of("bal", "money"), new BalanceCommandImpl());
            if (Banco.get().getSettings().get().getCommands().getBalanceTop().enabled())
                commands.register("balancetop", List.of("baltop"), new BalanceTopCommandImpl());
            if (Banco.get().getSettings().get().getCommands().getPay().enabled())
                commands.register("pay", new PayCommandImpl());
        });
    }

}
