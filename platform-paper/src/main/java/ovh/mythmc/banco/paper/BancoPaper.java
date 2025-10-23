package ovh.mythmc.banco.paper;

import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

import ovh.mythmc.banco.common.boot.BancoBootstrap;
import ovh.mythmc.banco.common.command.BancoCommandProvider;
import ovh.mythmc.banco.common.command.sender.BancoCommandSource;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.AccountManager;
import ovh.mythmc.banco.api.accounts.service.defaults.BukkitLocalUUIDResolver;
import ovh.mythmc.banco.api.logger.LoggerWrapper;
import ovh.mythmc.banco.api.scheduler.BancoScheduler;
import ovh.mythmc.banco.common.listeners.*;
import ovh.mythmc.banco.common.menu.MenuDispatcher;
import ovh.mythmc.banco.paper.command.PaperCommandProvider;
import ovh.mythmc.banco.paper.menu.PaperMenuDispatcher;
import ovh.mythmc.banco.paper.scheduler.BancoSchedulerPaper;
import ovh.mythmc.gestalt.loader.PaperGestaltLoader;

@Getter
public final class BancoPaper extends BancoBootstrap {

    public static BancoPaper instance;

    private PaperGestaltLoader gestalt;

    private final BancoScheduler scheduler = new BancoSchedulerPaper(getPlugin());

    private final MenuDispatcher menuDispatcher = new PaperMenuDispatcher();

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
        super(plugin, plugin.getDataFolder(), provider(plugin));
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

        // UUID resolver
        Bukkit.getPluginManager().registerEvents(uuidResolver, getPlugin());
    }

    private static BancoCommandProvider provider(@NotNull JavaPlugin plugin) {
        final var commandManager = new LegacyPaperCommandManager<BancoCommandSource>(
            plugin, 
            ExecutionCoordinator.simpleCoordinator(), 
            SenderMapper.create(
                commandSender -> new PaperCommandSource(commandSender),
                bancoSource -> (CommandSender) bancoSource.source()
            )
        );

        return new PaperCommandProvider(commandManager);
    }

}
