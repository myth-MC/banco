package ovh.mythmc.banco.api.bukkit.inventories;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.Account;
import ovh.mythmc.banco.api.bukkit.util.ItemUtil;
import ovh.mythmc.banco.api.storage.BancoInventory;
import ovh.mythmc.banco.api.items.BancoItem;

import java.math.BigDecimal;
import java.util.UUID;

public abstract class BancoInventoryBukkit implements BancoInventory<Inventory> {

    /**
     *
     * @param uuid UUID of the account where items will be added
     * @param amount amount of money to add to this BancoStorage
     * @return Total amount of money that has been added
     */
    @Override
    public @NotNull BigDecimal add(UUID uuid, BigDecimal amount) {
        BigDecimal amountGiven = BigDecimal.valueOf(0);

        for (ItemStack item : ItemUtil.convertAmountToItems(amount)) {
            BancoItem bancoItem = ItemUtil.getBancoItem(item);
            if (bancoItem != null)
                amountGiven = amountGiven.add(Banco.get().getItemManager().value(bancoItem, item.getAmount()));

            Player player = Bukkit.getPlayer(uuid);
            if (!get(uuid).addItem(item).isEmpty())
                player.getWorld().dropItemNaturally(player.getLocation(), item);
        }

        return amountGiven;
    }

    /**
     *
     * @param uuid UUID of the account where items will be removed
     * @param amount amount of money to remove from this BancoStorage
     * @return Amount of money that has not been removed
     */
    @Override
    public @NotNull BigDecimal remove(UUID uuid, BigDecimal amount) {
        for (ItemStack item : get(uuid)) {
            if (item == null) continue;
            if (amount.compareTo(BigDecimal.valueOf(0.01)) < 0) continue;

            BigDecimal value = BigDecimal.valueOf(0);

            BancoItem bancoItem = ItemUtil.getBancoItem(item);
            if (bancoItem != null)
                value = value.add(Banco.get().getItemManager().value(bancoItem, item.getAmount()));

            if (value.compareTo(BigDecimal.valueOf(0)) > 0) {
                item.setAmount(0);
                BigDecimal added = BigDecimal.valueOf(0);
                if (value.compareTo(amount) > 0) {
                    added = value.subtract(amount);
                    Account account = Banco.get().getAccountManager().get(uuid);
                    if (account != null)
                        add(uuid, added);
                }

                amount = amount.subtract(value.subtract(added));
            }
        }

        return amount;
    }

}
