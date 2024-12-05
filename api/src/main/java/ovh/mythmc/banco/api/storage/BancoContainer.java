package ovh.mythmc.banco.api.storage;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.Account;
import ovh.mythmc.banco.api.items.BancoItem;
import ovh.mythmc.banco.api.util.ItemUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * A container which provides a mutable list of ItemStack
 * @param <T> Must be ItemStack for now
 */
public abstract class BancoContainer implements BancoStorage {

    /**
     *
     * @param uuid UUID of the account to get this BancoContainer from
     * @return A mutable list of ItemStack
     */
    public abstract @NotNull List<ItemStack> get(UUID uuid);

    /**
     *
     * @return Max amount of items that this container can hold
     */
    public abstract @NotNull Integer maxSize();

    @Override
    public @NotNull BigDecimal value(UUID uuid) {
        BigDecimal value = BigDecimal.valueOf(0);

        for (ItemStack item : get(uuid)) {
            if (item == null)
                continue;

            BancoItem bancoItem = Banco.get().getItemManager().get(item);
            if (bancoItem == null)
                continue;

            value = value.add(Banco.get().getItemManager().value(bancoItem, item.getAmount()));
        }

        return value;
    }

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
            BancoItem bancoItem = Banco.get().getItemManager().get(item);
            if (bancoItem != null)
                amountGiven = amountGiven.add(Banco.get().getItemManager().value(bancoItem, item.getAmount()));

            //Player player = Bukkit.getPlayer(uuid);
            get(uuid).add(item);
            // Todo: check max size
            //if (!get(uuid).add(item).isEmpty())
            //    player.getWorld().dropItemNaturally(player.getLocation(), item);
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

            BancoItem bancoItem = Banco.get().getItemManager().get(item);
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

