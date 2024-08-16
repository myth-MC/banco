package ovh.mythmc.banco.common.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.economy.BancoHelper;
import ovh.mythmc.banco.api.economy.BancoHelperSupplier;
import ovh.mythmc.banco.api.inventories.BancoInventory;
import ovh.mythmc.banco.api.items.BancoItem;
import ovh.mythmc.banco.api.accounts.Account;
import ovh.mythmc.banco.common.inventories.PlayerInventoryImpl;
import ovh.mythmc.banco.common.util.ItemUtil;

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
            BancoItem bancoItem = ItemUtil.getBancoItem(item);
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

            BancoItem bancoItem = ItemUtil.getBancoItem(item);
            if (bancoItem != null)
                value = value.add(Banco.get().getEconomyManager().value(bancoItem, item.getAmount()));

            if (value.compareTo(BigDecimal.valueOf(0)) > 0) {
                item.setAmount(0);
                BigDecimal added = BigDecimal.valueOf(0);
                if (value.compareTo(amount) > 0) {
                    added = value.subtract(amount);
                    Account account = Banco.get().getAccountManager().get(uuid);
                    if (account != null)
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

        for (BancoInventory<Inventory> inventory : Banco.get().getInventoryManager().get()) {
            for (ItemStack item : inventory.get(uuid)) {
                if (item != null) {
                    BancoItem bancoItem = ItemUtil.getBancoItem(item);
                    if (bancoItem != null)
                        value = value.add(Banco.get().getEconomyManager().value(bancoItem, item.getAmount()));
                }
            }
        }

        return value;
    }

    public static List<ItemStack> convertAmountToItems(BigDecimal amount) {
        List<ItemStack> items = new ArrayList<>();

        for (BancoItem bancoItem : Banco.get().getEconomyManager().get().reversed()) {
            if(bancoItem.value().compareTo(amount) > 0)
                continue;

            int itemAmount = (amount.divide(bancoItem.value(), RoundingMode.FLOOR)).intValue();

            if (itemAmount > 0) {
                items.add(ItemUtil.getItemStack(bancoItem, itemAmount));

                amount = amount.subtract(Banco.get().getEconomyManager().value(bancoItem, itemAmount));
            }
        }

        return items;
    }

}
