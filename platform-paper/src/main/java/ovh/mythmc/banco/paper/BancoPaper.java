package ovh.mythmc.banco.paper;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.Plugin;
import ovh.mythmc.banco.common.hooks.BancoPlaceholderExpansion;
import ovh.mythmc.banco.common.hooks.BancoVaultHook;
import ovh.mythmc.banco.common.boot.BancoBootstrap;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.logger.LoggerWrapper;
import ovh.mythmc.banco.common.listeners.*;
import ovh.mythmc.banco.common.translation.BancoLocalization;
import ovh.mythmc.banco.paper.commands.BalanceCommandImpl;
import ovh.mythmc.banco.paper.commands.BalanceTopCommandImpl;
import ovh.mythmc.banco.paper.commands.BancoCommandImpl;
import ovh.mythmc.banco.paper.commands.PayCommandImpl;
import ovh.mythmc.gestalt.loader.PaperGestaltLoader;
import ovh.mythmc.banco.common.impl.BancoHelperImpl;

import java.util.*;

@Getter
public final class BancoPaper extends BancoBootstrap<BancoPaperPlugin> {

    public static BancoPaper instance;

    private BancoVaultHook vaultImpl;

    private ScheduledTask autoSaveTask;

    private PaperGestaltLoader gestalt;

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
    }

    @Override
    public void enable() {    
        // Gestalt
        gestalt = PaperGestaltLoader.builder()
            .initializer(getPlugin())
            .build();

        gestalt.initialize();

        vaultImpl = new BancoVaultHook();
        vaultImpl.hook(getPlugin());

        new Metrics(getPlugin(), 23496);

        new BancoLocalization().load(getPlugin().getDataFolder());

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
            new BancoPlaceholderExpansion();

        new BancoHelperImpl(); // BancoHelper.get()

        registerCommands();
        registerListeners();
    }

    @Override
    public void shutdown() {
        gestalt.terminate();
        
        vaultImpl.unhook();
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public String version() {
        return getPlugin().getPluginMeta().getVersion();
    }

    private void registerListeners() {
        // Paper listeners
        if (Banco.get().getSettings().get().getCurrency().isRemoveDrops())
            Bukkit.getPluginManager().registerEvents(new EntityDeathListener(), getPlugin());
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), getPlugin());
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), getPlugin());
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), getPlugin());

        // banco listeners
        Banco.get().getEventManager().registerListener(new BancoListener());
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
