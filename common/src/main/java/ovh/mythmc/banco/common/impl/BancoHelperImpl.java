package ovh.mythmc.banco.common.impl;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.economy.BancoHelper;
import ovh.mythmc.banco.api.economy.BancoHelperSupplier;
import ovh.mythmc.banco.api.economy.BancoItem;
import ovh.mythmc.banco.api.economy.accounts.Account;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BancoHelperImpl implements BancoHelper {

    public BancoHelperImpl() {
        BancoHelperSupplier.set(this);
    }

    @Override
    public final BigDecimal add(UUID uuid, BigDecimal amount) {
        Player player = Bukkit.getOfflinePlayer(uuid).getPlayer();
        if (player == null)
            return BigDecimal.valueOf(0);

        BigDecimal amountGiven = BigDecimal.valueOf(0);

        for (ItemStack item : convertAmountToItems(amount)) {
            String materialName = item.getType().name();
            String displayName = null;
            int customModelData = 0;

            if (item.hasItemMeta()) {
                displayName = item.getItemMeta().getDisplayName();
                if (item.getItemMeta().hasCustomModelData())
                    customModelData = item.getItemMeta().getCustomModelData();
            }

            BancoItem bancoItem = Banco.get().getEconomyManager().get(materialName, displayName, customModelData);
            if (bancoItem != null)
                amountGiven = amountGiven.add(Banco.get().getEconomyManager().value(bancoItem, item.getAmount()));

            if (!player.getInventory().addItem(item).isEmpty())
                player.getWorld().dropItemNaturally(player.getLocation(), item);
           }

        return amount.subtract(amountGiven);
    }

    // Todo: look for less valuable items first
    @Override
    public BigDecimal remove(UUID uuid, BigDecimal amount) {
        Player player = Bukkit.getOfflinePlayer(uuid).getPlayer();
        if (player == null)
            return BigDecimal.valueOf(0);

        if (Banco.get().getSettings().get().getCurrency().isCountEnderChest()) {
            BigDecimal remainingAmount = removeFromInventory(player.getEnderChest().getContents(), uuid, amount);

            if (remainingAmount.compareTo(BigDecimal.valueOf(0)) > 0)
                return removeFromInventory(player.getInventory().getContents(), uuid, remainingAmount);
        }

        return removeFromInventory(player.getInventory().getContents(), uuid, amount);
    }

    private BigDecimal removeFromInventory(ItemStack[] inventory, UUID uuid, BigDecimal amount) {
        for (ItemStack item : inventory) {
            if (item == null) continue;
            if (amount.compareTo(BigDecimal.valueOf(0)) < 0.01) continue;

            BigDecimal value = BigDecimal.valueOf(0);

            String materialName = item.getType().name();
            String displayName = null;
            int customModelData = 0;

            if (item.hasItemMeta()) {
                displayName = item.getItemMeta().getDisplayName();
                if (item.getItemMeta().hasCustomModelData())
                    customModelData = item.getItemMeta().getCustomModelData();
            }

            BancoItem bancoItem = Banco.get().getEconomyManager().get(materialName, displayName, customModelData);
            if (bancoItem != null)
                value = value.add(Banco.get().getEconomyManager().value(bancoItem, item.getAmount()));

            if (value.compareTo(BigDecimal.valueOf(0)) > 0) {
                item.setAmount(0);
                BigDecimal added = BigDecimal.valueOf(0);
                if (value.compareTo(amount) > 0) {
                    added = value.subtract(amount);
                    Account account = Banco.get().getAccountManager().get(uuid);
                    Banco.get().getAccountManager().set(account, account.amount().add(added));
                }

                amount = amount.subtract(value.subtract(added));
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
            if (item != null) {
                String materialName = item.getType().name();
                String displayName = null;
                int customModelData = 0;

                if (item.hasItemMeta()) {
                    displayName = item.getItemMeta().getDisplayName();
                    if (item.getItemMeta().hasCustomModelData())
                        customModelData = item.getItemMeta().getCustomModelData();
                }

                BancoItem bancoItem = Banco.get().getEconomyManager().get(materialName, displayName, customModelData);
                if (bancoItem != null)
                    value = value.add(Banco.get().getEconomyManager().value(bancoItem, item.getAmount()));
            }
        }

        if (Banco.get().getSettings().get().getCurrency().isCountEnderChest()) {
            for (ItemStack item : Objects.requireNonNull(Bukkit.getPlayer(uuid)).getEnderChest()) {
                if (item != null) {
                    String materialName = item.getType().name();
                    String displayName = null;
                    int customModelData = 0;

                    if (item.hasItemMeta()) {
                        displayName = item.getItemMeta().getDisplayName();
                        if (item.getItemMeta().hasCustomModelData())
                            customModelData = item.getItemMeta().getCustomModelData();
                    }

                    BancoItem bancoItem = Banco.get().getEconomyManager().get(materialName, displayName, customModelData);
                    if (bancoItem != null)
                        value = value.add(Banco.get().getEconomyManager().value(bancoItem, item.getAmount()));
                }
            }
        }

        return value;
    }

    public List<ItemStack> convertAmountToItems(BigDecimal amount) {
        List<ItemStack> items = new ArrayList<>();

        for (BancoItem bancoItem : Banco.get().getEconomyManager().get().reversed()) {
            if(bancoItem.value().compareTo(amount) > 0)
                continue;

            int itemAmount = (amount.divide(bancoItem.value(), RoundingMode.FLOOR)).intValue();

            if (itemAmount > 0) {
                ItemStack itemStack = new ItemStack(Material.getMaterial(bancoItem.name()), itemAmount);
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (bancoItem.displayName() != null)
                    itemMeta.setDisplayName(bancoItem.displayName());
                if (bancoItem.lore() != null)
                    itemMeta.setLore(bancoItem.lore().stream().map(string -> ChatColor.RESET + string).toList());
                if (bancoItem.customModelData() != 0)
                    itemMeta.setCustomModelData(bancoItem.customModelData());
                itemStack.setItemMeta(itemMeta);
                items.add(itemStack);

                amount = amount.subtract(Banco.get().getEconomyManager().value(bancoItem, itemAmount));
            }
        }

        return items;
    }
}
