package ovh.mythmc.banco;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import ovh.mythmc.banco.commands.BalanceCommand;
import ovh.mythmc.banco.commands.BancoCommand;
import ovh.mythmc.banco.commands.PayCommand;
import ovh.mythmc.banco.economy.AccountManager;
import ovh.mythmc.banco.listeners.EntityDeathListener;
import ovh.mythmc.banco.listeners.PlayerJoinListener;
import ovh.mythmc.banco.listeners.PlayerQuitListener;
import ovh.mythmc.banco.utils.TranslationUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.Scanner;

public final class Banco extends JavaPlugin {

    private static Banco instance;

    private AccountManager accountManager;
    private BancoVaultImpl vaultImpl;

    private BukkitTask autoSaveTask;
    private BukkitTask updaterTask;

    @Override
    public void onEnable() {
        instance = this;
        vaultImpl = new BancoVaultImpl();
        accountManager = new AccountManager();

        saveDefaultResources();
        TranslationUtils.register();

        registerListeners();
        registerCommands();

        loadData();

        if (getConfig().getBoolean("auto-save.enabled"))
            startAutoSaver();

        if (getConfig().getBoolean("update-tracker.enabled"))
            startUpdateTracker();

        hook();

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
            new BancoPlaceholderExpansion().register();
    }

    @Override
    public void onDisable() {
        unhook();

        stopAutoSaver();
        try {
            saveData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveDefaultResources() {
        File config = new File(getDataFolder(), "config.yml");
        File data = new File(getDataFolder(), "data.yml");

        if (!config.exists())
            saveResource("config.yml", false);
        if (!data.exists())
            saveResource("data.yml", false);
    }

    public void reload() {
        reloadConfig();

        if (autoSaveTask != null)
            stopAutoSaver();

        if (updaterTask != null)
            stopUpdateTracker();

        startAutoSaver();
        startUpdateTracker();
    }

    public void loadData() {
        File dataFile = new File(getDataFolder(), "data.yml");
        if (!dataFile.exists())
            return;

        YamlConfiguration dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        getAccountManager().loadData(dataConfig);
    }

    public void saveData() throws IOException {
        File dataFile = new File(getDataFolder(), "data.yml");
        if (!dataFile.exists())
            dataFile.createNewFile();

        YamlConfiguration dataConfig = new YamlConfiguration();
        getAccountManager().saveData(dataConfig);
        dataConfig.save(dataFile);
    }

    private void startAutoSaver() {
        this.autoSaveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            try {
                saveData();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, 0, getConfig().getInt("auto-save.frequency") * 20L);
    }

    private void stopAutoSaver() {
        this.autoSaveTask.cancel();
    }

    private void startUpdateTracker() {
        this.updaterTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            URLConnection connection = null;
            try {
                String url = "https://raw.githubusercontent.com/myth-MC/banco/main/VERSION";
                connection = new URL(url).openConnection();
            } catch (IOException e) {
                if (getConfig().getBoolean("debug"))
                    getLogger().warning(e.getMessage());
            }

            try (Scanner scanner = new Scanner(Objects.requireNonNull(connection).getInputStream())) {
                if (getConfig().getBoolean("debug"))
                    getLogger().info("Checking for updates...");

                String latest = scanner.next();
                String current = getPluginMeta().getVersion();

                if (!current.equals(latest)) {
                    getLogger().info("A new update has been found: " + latest);
                    getLogger().info("You are currently running banco v" + current);
                    return;
                }

                if (getConfig().getBoolean("debug"))
                    getLogger().info("No updates have been found.");
            } catch (IOException e) {
                if (getConfig().getBoolean("debug"))
                    getLogger().warning(e.getMessage());
            }
        }, 0, getConfig().getInt("update-tracker.frequency") * 20L);
    }

    private void stopUpdateTracker() {
        this.updaterTask.cancel();
    }

    private void registerListeners() {
        if (getConfig().getBoolean("currency.remove-drops"))
            Bukkit.getPluginManager().registerEvents(new EntityDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), this);
    }

    private void registerCommands() {
        PluginCommand banco = getCommand("banco");
        PluginCommand balance = getCommand("balance");
        PluginCommand pay = getCommand("pay");

        banco.setExecutor(new BancoCommand());
        balance.setExecutor(new BalanceCommand());
        pay.setExecutor(new PayCommand());

        if (!getConfig().getBoolean("commands.balance.enabled"))
            balance.setPermission("banco.admin");

        if (!getConfig().getBoolean("commands.pay.enabled"))
            pay.setPermission("banco.admin");
    }

    private void hook() {
        Bukkit.getServicesManager().register(Economy.class, vaultImpl, this, ServicePriority.Normal);
    }

    private void unhook() {
        Bukkit.getServicesManager().unregister(Economy.class, vaultImpl);
    }

    public static Banco get() { return instance; }

    public AccountManager getAccountManager() { return accountManager; }

}
