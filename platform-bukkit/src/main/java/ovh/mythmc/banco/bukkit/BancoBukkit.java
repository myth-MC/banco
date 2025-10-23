package ovh.mythmc.banco.bukkit;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;

import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

import ovh.mythmc.banco.common.boot.BancoBootstrap;
import ovh.mythmc.banco.common.command.BancoCommandProvider;
import ovh.mythmc.banco.common.command.sender.BancoCommandSource;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.AccountManager;
import ovh.mythmc.banco.api.accounts.service.defaults.BukkitLocalUUIDResolver;
import ovh.mythmc.banco.api.logger.LoggerWrapper;
import ovh.mythmc.banco.api.scheduler.BancoScheduler;
import ovh.mythmc.banco.common.listeners.*;
import ovh.mythmc.banco.common.menu.MenuDispatcher;
import ovh.mythmc.gestalt.loader.BukkitGestaltLoader;
import ovh.mythmc.banco.bukkit.command.BukkitCommandProvider;
import ovh.mythmc.banco.bukkit.listener.InventoryListener;
import ovh.mythmc.banco.bukkit.menu.BukkitMenuDispatcher;
import ovh.mythmc.banco.bukkit.scheduler.BancoSchedulerBukkit;

@Getter
public final class BancoBukkit extends BancoBootstrap {

    public static BancoBukkit instance;

    private static BukkitAudiences adventure;

    private BukkitGestaltLoader gestalt;

    private final BancoScheduler scheduler = new BancoSchedulerBukkit(getPlugin());

    private final MenuDispatcher menuDispatcher = new BukkitMenuDispatcher();

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

    public BancoBukkit(final @NotNull BancoBukkitPlugin plugin) {
        super(plugin, plugin.getDataFolder(), provider(plugin));

        instance = this;

        // Register platform UUID resolver
        this.uuidResolver = new BukkitLocalUUIDResolver(scheduler);

        // Register platform account manager
        this.accountManager = new AccountManager(uuidResolver);
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
    public MenuDispatcher menuDispatcher() {
        return menuDispatcher;
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
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), getPlugin());
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), getPlugin());

        // UUID resolver
        Bukkit.getPluginManager().registerEvents(uuidResolver, getPlugin());

        // Inventory listener
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), getPlugin());
    }

    public static @NotNull BukkitAudiences adventure() {
        if(adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return adventure;
    }

    private static BancoCommandProvider provider(@NotNull JavaPlugin plugin) {
        final var commandManager = new LegacyPaperCommandManager<BancoCommandSource>(
            plugin, 
            ExecutionCoordinator.simpleCoordinator(), 
            SenderMapper.create(
                commandSender -> new BukkitCommandSource(commandSender),
                bancoSource -> (CommandSender) bancoSource.source()
            )
        );

        return new BukkitCommandProvider(commandManager);
    }

}
