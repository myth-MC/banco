package ovh.mythmc.banco.bukkit;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.PluginCommand;
import org.checkerframework.checker.nullness.qual.NonNull;
import ovh.mythmc.banco.bukkit.commands.BalanceCommand;
import ovh.mythmc.banco.bukkit.commands.BancoCommand;
import ovh.mythmc.banco.common.BancoPlaceholderExpansion;
import ovh.mythmc.banco.common.BancoVaultImpl;
import ovh.mythmc.banco.common.boot.BancoBootstrap;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.logger.LoggerWrapper;
import ovh.mythmc.banco.common.listeners.EntityDeathListener;
import ovh.mythmc.banco.common.listeners.PlayerJoinListener;
import ovh.mythmc.banco.common.listeners.PlayerQuitListener;
import ovh.mythmc.banco.common.util.MapUtil;
import ovh.mythmc.banco.common.util.TranslationUtil;
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
        TranslationUtil.register();
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
            new BancoPlaceholderExpansion();

        vaultImpl = new BancoVaultImpl();
        vaultImpl.hook(getPlugin());

        adventure = BukkitAudiences.create(getPlugin());

        registerListeners();
        registerCommands();

        if (Banco.get().getConfig().getSettings().getAutoSave().getBoolean("enabled"))
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

    @Override
    public boolean isOnline(UUID uuid) {
        return Bukkit.getOfflinePlayer(uuid).isOnline();
    }

    @Override
    public int getInventoryValue(UUID uuid) {
        int value = 0;

        for (ItemStack item : Objects.requireNonNull(Bukkit.getPlayer(uuid)).getInventory()) {
            if (item != null)
                value = value + Banco.get().getEconomyManager().value(item.getType().name(), item.getAmount());
        }

        return value;
    }

    @Override
    public void clearInventory(UUID uuid) {
        Player player = Bukkit.getOfflinePlayer(uuid).getPlayer();

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null)
                return;
            if (Banco.get().getEconomyManager().value(item.getType().name()) > 0)
                player.getInventory().remove(item);
        }
    }

    @Override
    public void setInventory(UUID uuid, int amount) {
        Player player = Bukkit.getOfflinePlayer(uuid).getPlayer();

        Bukkit.getScheduler().runTask(getPlugin(), () -> {
            for (ItemStack item : convertAmountToItems(amount)) {
                if (!player.getPlayer().getInventory().addItem(item).isEmpty()) {
                    player.getPlayer().getWorld().dropItemNaturally(player.getPlayer().getLocation(), item);
                }
            }
        });
    }

    public List<ItemStack> convertAmountToItems(int amount) {
        List<ItemStack> items = new ArrayList<>();

        for (String materialName : MapUtil.sortByValue(Banco.get().getEconomyManager().values()).keySet()) {
            int itemAmount = amount / Banco.get().getEconomyManager().value(materialName);

            if (itemAmount > 0) {
                items.add(new ItemStack(Material.getMaterial(materialName), itemAmount));
            }

            amount = amount - Banco.get().getEconomyManager().value(materialName, itemAmount);
        }

        return items;
    }

    private void registerListeners() {
        if (Banco.get().getConfig().getSettings().getCurrency().getBoolean("remove-drops"))
            Bukkit.getPluginManager().registerEvents(new EntityDeathListener(), getPlugin());
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), getPlugin());
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), getPlugin());
    }

    private void registerCommands() {
        PluginCommand banco = getPlugin().getCommand("banco");
        PluginCommand balance = getPlugin().getCommand("balance");
        PluginCommand pay = getPlugin().getCommand("pay");

        banco.setExecutor(new BancoCommand());
        balance.setExecutor(new BalanceCommand());
        pay.setExecutor(new PayCommand());

        if (!Banco.get().getConfig().getSettings().getCommands().getBoolean("balance.enabled"))
            balance.setPermission("banco.admin");

        if (!Banco.get().getConfig().getSettings().getCommands().getBoolean("pay.enabled"))
            pay.setPermission("banco.admin");
    }

    private void startAutoSaver() {
        this.autoSaveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(getPlugin(), () -> {
            try {
                Banco.get().getStorage().save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, 0, Banco.get().getConfig().getSettings().getAutoSave().getInt("frequency") * 20L);
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
