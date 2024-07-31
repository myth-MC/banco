package ovh.mythmc.banco.bukkit.impl;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.economy.BancoHelper;
import ovh.mythmc.banco.api.economy.BancoHelperSupplier;
import ovh.mythmc.banco.common.util.MathUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BancoHelperImpl implements BancoHelper {

    private final @NotNull Plugin plugin;

    public BancoHelperImpl(final @NotNull Plugin plugin) {
        this.plugin = plugin;
        BancoHelperSupplier.set(this);
    }

    @Override
    public void add(UUID uuid, BigDecimal amount) {
        Player player = Bukkit.getOfflinePlayer(uuid).getPlayer();
        if (player == null)
            return;

        Bukkit.getScheduler().runTask(plugin, scheduledTask -> {
            for (ItemStack item : convertAmountToItems(amount)) {
                if (!player.getInventory().addItem(item).isEmpty())
                    player.getWorld().dropItemNaturally(player.getLocation(), item);
            }
        });
    }

    // Todo: look for less valuable items first
    @Override
    public void remove(UUID uuid, BigDecimal amount) {
        Player player = Bukkit.getOfflinePlayer(uuid).getPlayer();
        if (player == null)
            return;

        Bukkit.getScheduler().runTask(plugin, scheduledTask -> {
            if (Banco.get().getConfig().getSettings().getCurrency().countEnderChest()) {
                if (removeFromInventory(player.getEnderChest().getContents(), uuid, amount).compareTo(BigDecimal.valueOf(0)) > 0)
                    removeFromInventory(player.getInventory().getContents(), uuid, amount);
                return;
            }

            removeFromInventory(player.getInventory().getContents(), uuid, amount);
        });
    }

    private BigDecimal removeFromInventory(ItemStack[] inventory, UUID uuid, BigDecimal amount) {
        for (ItemStack item : inventory) {
            if (item == null) continue;
            if (amount.compareTo(BigDecimal.valueOf(0.001)) < 0) continue;

            BigDecimal value = Banco.get().getEconomyManager().value(item.getType().name(), item.getAmount());

            if (value.compareTo(BigDecimal.valueOf(0)) > 0) {
                item.setAmount(0);
                if (value.compareTo(amount) > 0)
                    add(uuid, value.subtract(amount));
                amount = amount.subtract(value);
            }
        }

        return amount;
    }

    @Override
    public boolean isOnline(UUID uuid) {
        return Bukkit.getOfflinePlayer(uuid).isOnline();
    }

    @Override
    public BigDecimal getInventoryValue(UUID uuid) {
        BigDecimal value = BigDecimal.valueOf(0);

        for (ItemStack item : Objects.requireNonNull(Bukkit.getPlayer(uuid)).getInventory()) {
            if (item != null)
                value = value.add(Banco.get().getEconomyManager().value(item.getType().name(), item.getAmount()));
        }

        if (Banco.get().getConfig().getSettings().getCurrency().countEnderChest()) {
            for (ItemStack item : Objects.requireNonNull(Bukkit.getPlayer(uuid)).getEnderChest()) {
                if (item != null)
                    value = value.add(Banco.get().getEconomyManager().value(item.getType().name(), item.getAmount()));
            }
        }

        return value;
    }

    public List<ItemStack> convertAmountToItems(BigDecimal amount) {
        List<ItemStack> items = new ArrayList<>();

        for (String materialName : MathUtil.sortByValue(Banco.get().getEconomyManager().values()).keySet()) {
            BigDecimal itemValue = Banco.get().getEconomyManager().value(materialName);

            if (itemValue.compareTo(amount) > 0)
                continue;

            int itemAmount = (amount.divide(itemValue, RoundingMode.FLOOR)).intValue();

            if (itemAmount > 0) {
                items.add(new ItemStack(Objects.requireNonNull(Material.getMaterial(materialName)), itemAmount));
            }

            amount = amount.subtract(Banco.get().getEconomyManager().value(materialName, itemAmount));
        }

        return items;
    }
}
