package ovh.mythmc.banco.api.storage;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.items.BancoItem;
import ovh.mythmc.banco.api.util.ItemUtil;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

/**
 * A container which provides a list of ItemStack
 */
public abstract class BancoContainer implements BancoStorage {

    /**
     *
     * @param uuid UUID of the account to get this BancoContainer from
     * @return A list of ItemStack
     */
    protected abstract Collection<ItemStack> get(UUID uuid);

    /**
     *
     * @param uuid UUID of the account to get this BancoContainer from
     * @param itemStack ItemStack to add
     * @return Remaining ItemStack
     */
    protected abstract ItemStack addItem(UUID uuid, ItemStack itemStack);

    /**
     *
     * @param uuid UUID of the account to get this BancoContainer from
     * @param itemStack ItemStack to remove
     * @return ItemStack that could not be removed
     */
    protected abstract ItemStack removeItem(UUID uuid, ItemStack itemStack);

    @Override
    public @NotNull BigDecimal value(UUID uuid) {
        BigDecimal value = BigDecimal.valueOf(0);

        for (ItemStack item : get(uuid)) {
            if (item == null)
                continue;

            BancoItem bancoItem = Banco.get().getItemRegistry().getByItemStack(item);
            if (bancoItem == null)
                continue;

            value = value.add(bancoItem.value(item.getAmount()));
        }

        return value;
    }

    @Override
    public @NotNull BigDecimal add(UUID uuid, BigDecimal amount) {
        BigDecimal amountGiven = BigDecimal.valueOf(0);

        for (ItemStack itemStack : ItemUtil.convertAmountToItems(amount)) {
            BancoItem bancoItem = Banco.get().getItemRegistry().getByItemStack(itemStack);
            amountGiven = amountGiven.add(bancoItem.value(itemStack.getAmount()));

            ItemStack remainderItemStack = addItem(uuid, itemStack);
            if (remainderItemStack == null)
                continue;

            BancoItem remainderBancoItem = Banco.get().getItemRegistry().getByItemStack(remainderItemStack);
            if (remainderBancoItem != null)
                amountGiven = amountGiven.subtract(remainderBancoItem.value(remainderItemStack.getAmount()));
        }

        return amountGiven;
    }

    @Override
    public @NotNull BigDecimal remove(UUID uuid, BigDecimal amount) {
        for (BancoItem bancoItem : Banco.get().getItemRegistry().get()) {
            for (ItemStack item : get(uuid)) {
                if (item == null || !Objects.equals(Banco.get().getItemRegistry().getByItemStack(item), bancoItem)) 
                    continue;

                if (amount.compareTo(BigDecimal.valueOf(0.01)) < 0) 
                    continue;
    
                BigDecimal value = bancoItem.value(item.getAmount());
    
                if (value.compareTo(BigDecimal.valueOf(0)) > 0) {
                    removeItem(uuid, item);

                    BigDecimal removed = BigDecimal.valueOf(0);
                    if (value.compareTo(amount) > 0) {
                        removed = value.subtract(amount);
                        Banco.get().getAccountManager().deposit(uuid, removed);
                    }
    
                    amount = amount.subtract(value.subtract(removed));

                }
            }
        }

        return amount;
    }

}

