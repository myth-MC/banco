package ovh.mythmc.banco.paper;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.plugin.Plugin;
import ovh.mythmc.banco.common.BancoPlaceholderExpansion;
import ovh.mythmc.banco.common.impl.BancoVaultImpl;
import ovh.mythmc.banco.common.boot.BancoBootstrap;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.logger.LoggerWrapper;
import ovh.mythmc.banco.common.listeners.BancoListener;
import ovh.mythmc.banco.common.listeners.EntityDeathListener;
import ovh.mythmc.banco.common.listeners.PlayerJoinListener;
import ovh.mythmc.banco.common.listeners.PlayerQuitListener;
import ovh.mythmc.banco.common.translation.BancoLocalization;
import ovh.mythmc.banco.paper.commands.BalanceCommand;
import ovh.mythmc.banco.paper.commands.BancoCommand;
import ovh.mythmc.banco.paper.commands.PayCommand;
import ovh.mythmc.banco.common.impl.BancoHelperImpl;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Getter
public final class BancoPaper extends BancoBootstrap<BancoPaperPlugin> {

    public static BancoPaper instance;

    private BancoVaultImpl vaultImpl;

    private ScheduledTask autoSaveTask;

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
        new BancoLocalization().load(getPlugin().getDataFolder());

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
            new BancoPlaceholderExpansion();

        new BancoHelperImpl(); // BancoHelper.get()

        vaultImpl = new BancoVaultImpl();
        vaultImpl.hook(getPlugin());

        registerCommands();
        registerListeners();

        if (Banco.get().getSettings().get().getAutoSave().isEnabled())
            startAutoSaver();
    }

    @Override
    public void shutdown() {
        vaultImpl.unhook();

        if (autoSaveTask != null)
            stopAutoSaver();

        Banco.get().getData().save();
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

        // banco listeners
        Banco.get().getEventManager().registerListener(new BancoListener());
    }

    @SuppressWarnings("UnstableApiUsage")
    private void registerCommands() {
        LifecycleEventManager<Plugin> manager = getPlugin().getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register("banco", "Main command for managing banco accounts", new BancoCommand());
            if (Banco.get().getSettings().get().getCommands().getBalance().enabled())
                commands.register("balance", List.of("bal", "money"), new BalanceCommand());
            if (Banco.get().getSettings().get().getCommands().getPay().enabled())
                commands.register("pay", new PayCommand());
        });
    }

    private void startAutoSaver() {
        this.autoSaveTask = Bukkit.getAsyncScheduler().runAtFixedRate(getPlugin(), scheduledTask -> Banco.get().getData().save(), 0, Banco.get().getSettings().get().getAutoSave().getFrequency(), TimeUnit.SECONDS);
    }

    private void stopAutoSaver() {
        this.autoSaveTask.cancel();
    }

}
