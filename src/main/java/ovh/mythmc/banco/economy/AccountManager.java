package ovh.mythmc.banco.economy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ovh.mythmc.banco.Banco;
import ovh.mythmc.banco.utils.MapUtils;
import ovh.mythmc.banco.utils.PlayerUtils;

import java.util.*;

public class AccountManager {

    private final Banco instance = Banco.get();

    private List<Account> accountsList;
    private final Map<Material, Integer> valuesMap;

    public AccountManager() {
        this.accountsList = new ArrayList<>();
        this.valuesMap = new HashMap<>();
    }

    public void loadData(YamlConfiguration dataConfig) {
        this.accountsList = new ArrayList<>();

        ConfigurationSection accounts = dataConfig.getConfigurationSection("accounts");
        accounts.getKeys(false).forEach(key -> {
            ConfigurationSection account = accounts.getConfigurationSection(key);
            UUID uuid = UUID.fromString(key);
            int amount = account.getInt("amount");
            int transactions = account.getInt("transactions");

            accountsList.add(new Account(uuid, amount, transactions));
        });

        if (instance.getConfig().getBoolean("debug"))
            instance.getLogger().info("Loaded " + getAccounts().size() + " account(s)!");

        ConfigurationSection values = instance.getConfig().getConfigurationSection("currency.value");
        for (String materialName : values.getKeys(false)) {

            Material material = Material.getMaterial(materialName);
            int value = instance.getConfig().getInt("currency.value." + materialName);

            if (instance.getConfig().getBoolean("debug"))
                instance.getLogger().info(materialName + ": " + value);

            valuesMap.put(material, value);
        }

        if (instance.getConfig().getBoolean("debug"))
            instance.getLogger().info("Loaded " + valuesMap.size() + " value(s)!");
    }

    public void saveData(YamlConfiguration dataConfig) {
        if (instance.getConfig().getBoolean("debug"))
            instance.getLogger().info("Saving " + getAccounts().size() + " account(s)...");

        ConfigurationSection accountsSection = dataConfig.createSection("accounts");
        getAccounts().forEach(account -> {
            ConfigurationSection accountSection = accountsSection.createSection(account.getUuid().toString());
            accountSection.set("amount", account.getAmount());
            accountSection.set("transactions", account.getTransactions());
        });

        if (instance.getConfig().getBoolean("debug"))
            instance.getLogger().info("Done!");
    }

    public List<Account> getAccounts() {
        return accountsList;
    }

    public Account getAccount(UUID uuid) {
        return accountsList.stream()
                .filter(account -> account.getUuid().equals(uuid))
                .findFirst()
                .orElse(null);
    }

    public void createAccount(UUID uuid) {
        Account account = new Account(uuid, 0, 0);
        accountsList.add(account);
    }

    public void removeAccount(UUID uuid) {
        accountsList.remove(getAccount(uuid));
    }

    public void add(OfflinePlayer player, int amount) {
        Account account = getAccount(player.getUniqueId());

        // Check whether player is online or not
        if (player.isOnline()) {
            account.setAmount(getAmount(player) + amount);
            // add gold to inv
            Bukkit.getScheduler().runTask(Banco.get(), () -> {
                for (ItemStack item : convertAmountToItems(amount)) {
                    player.getPlayer().getWorld().dropItem(player.getPlayer().getLocation(), item);
                }
            });
            return;
        }

        account.setTransactions(getTransactions(player) + amount);
    }

    public void add(UUID uuid, int amount) {
        add(Bukkit.getOfflinePlayer(uuid), amount);
    }

    public void remove(OfflinePlayer player, int amount) {
        Account account = getAccount(player.getUniqueId());

        // Check whether player is online or not
        if (player.isOnline()) {
            int newAmount = getAmount(player) - amount;

            // remove gold from inv
            withdrawAll(player.getPlayer());
            Bukkit.getScheduler().runTask(Banco.get(), () -> {
                for (ItemStack item : convertAmountToItems(newAmount)) {
                    player.getPlayer().getWorld().dropItem(player.getPlayer().getLocation(), item);
                }
            });

            account.setAmount(newAmount);
            return;
        }

        account.setTransactions(getTransactions(player) - amount);
    }

    public void remove(UUID uuid, int amount) {
        remove(Bukkit.getOfflinePlayer(uuid), amount);
    }

    public void set(OfflinePlayer player, int amount) {
        Account account = getAccount(player.getUniqueId());

        // Check whether player is online or not
        if (player.isOnline()) {
            // remove gold from inv
            withdrawAll(player.getPlayer());
            Bukkit.getScheduler().runTask(Banco.get(), () -> {
                for (ItemStack item : convertAmountToItems(amount)) {
                    player.getPlayer().getWorld().dropItem(player.getPlayer().getLocation(), item);
                }
            });

            account.setAmount(amount);
            return;
        }

        account.setTransactions(amount);
    }

    public void set(UUID uuid, int amount) {
        set(Bukkit.getOfflinePlayer(uuid), amount);
    }

    public List<ItemStack> convertAmountToItems(int amount) {
        List<ItemStack> items = new ArrayList<>();

        for (Material material : MapUtils.sortByValue(valuesMap).keySet()) {
            int itemAmount = amount / getValue(material);

            if (itemAmount > 0) {
                items.add(new ItemStack(material, itemAmount));
            }

            amount = amount - getValue(material, itemAmount);
        }

        return items;
    }

    public int convertItemsToAmount(ItemStack[] items) {
        int value = 0;

        for (ItemStack item : items) {
            if (item != null)
                value = value + getValue(item.getType(), item.getAmount());
        }

        return value;
    }

    private void withdrawAll(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null)
                return;
            if (valuesMap.containsKey(item.getType()))
                player.getInventory().remove(item);
        }
    }

    public int getAmount(OfflinePlayer offlinePlayer) {
        Account account = getAccount(offlinePlayer.getUniqueId());
        if (offlinePlayer.isOnline()) {
            account.setAmount(convertItemsToAmount(offlinePlayer.getPlayer().getInventory().getContents()));
        }

        return account.getAmount();
    }

    public int getAmount(UUID uuid) {
        return getAmount(Bukkit.getOfflinePlayer(uuid));
    }

    public int getTransactions(OfflinePlayer offlinePlayer) {
        Account account = getAccount(offlinePlayer.getUniqueId());
        return account.getTransactions();
    }

    public int getTransactions(UUID uuid) {
        return getTransactions(Bukkit.getOfflinePlayer(uuid));
    }

    public int getActualAmount(OfflinePlayer offlinePlayer) {
        return getAmount(offlinePlayer) + getTransactions(offlinePlayer);
    }

    public int getActualAmount(UUID uuid) {
        return getActualAmount(Bukkit.getOfflinePlayer(uuid));
    }

    public int getValue(Material material, int amount) {
        for (Map.Entry<Material, Integer> entry : valuesMap.entrySet()) {
            if (!material.equals(entry.getKey())) continue;
            return entry.getValue() * amount;
        }

        return 0;
    }

    public int getValue(Material material) {
        return getValue(material, 1);
    }

}
