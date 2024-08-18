package ovh.mythmc.banco.common.inventories;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.Account;
import ovh.mythmc.banco.api.inventories.BancoInventory;
import ovh.mythmc.banco.api.items.BancoItem;
import ovh.mythmc.banco.common.util.ItemUtil;

import java.math.BigDecimal;
import java.util.UUID;

public final class PlayerInventoryImpl implements BancoInventory<Inventory> {

    @Override
    public @NotNull Inventory get(UUID uuid) {
        return Bukkit.getPlayer(uuid).getInventory();
    }

    @Override
    public BigDecimal add(UUID uuid, BigDecimal amount) {
        BigDecimal amountGiven = BigDecimal.valueOf(0);

        for (ItemStack item : ItemUtil.convertAmountToItems(amount)) {
            BancoItem bancoItem = ItemUtil.getBancoItem(item);
            if (bancoItem != null)
                amountGiven = amountGiven.add(Banco.get().getItemManager().value(bancoItem, item.getAmount()));

            Player player = Bukkit.getPlayer(uuid);
            if (!player.getInventory().addItem(item).isEmpty())
                player.getWorld().dropItemNaturally(player.getLocation(), item);
        }

        return amountGiven;
    }

    @Override
    public BigDecimal remove(UUID uuid, BigDecimal amount) {
        for (ItemStack item : Bukkit.getPlayer(uuid).getInventory()) {
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
                        Banco.get().getAccountManager().set(account, account.amount().add(added));
                }

                amount = amount.subtract(value.subtract(added));
            }
        }

        return amount;
    }

}
