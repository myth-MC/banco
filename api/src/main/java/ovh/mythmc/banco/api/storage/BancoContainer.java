package ovh.mythmc.banco.api.storage;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.items.BancoItem;
import ovh.mythmc.banco.api.util.ItemUtil;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

/**
 * Abstract base class for storage systems that use ItemStacks as containers.
 * <p>
 * This class provides a foundation for storage implementations that store currency
 * as physical ItemStacks. It handles the conversion between currency amounts and
 * ItemStacks, and manages the distribution of items across storage containers.
 * </p>
 *
 * @since 1.0.0
 */
public abstract class BancoContainer implements BancoStorage {

    /**
     * Gets all ItemStacks from the container for the specified account.
     *
     * @param uuid UUID of the account to get items from
     * @return a collection of ItemStacks in the container
     * @throws IllegalArgumentException if uuid is null
     */
    @NotNull
    protected abstract Collection<ItemStack> get(@NotNull UUID uuid);

    /**
     * Adds an ItemStack to the container for the specified account.
     *
     * @param uuid UUID of the account
     * @param itemStack ItemStack to add
     * @return the remaining ItemStack that couldn't be added, or null if all was added
     * @throws IllegalArgumentException if uuid is null or itemStack is null
     */
    @Nullable
    protected abstract ItemStack addItem(@NotNull UUID uuid, @NotNull ItemStack itemStack);

    /**
     * Removes an ItemStack from the container for the specified account.
     *
     * @param uuid UUID of the account
     * @param itemStack ItemStack to remove
     * @return the ItemStack that couldn't be removed, or null if all was removed
     * @throws IllegalArgumentException if uuid is null or itemStack is null
     */
    @Nullable
    protected abstract ItemStack removeItem(@NotNull UUID uuid, @NotNull ItemStack itemStack);

    @Override
    @NotNull
    public BigDecimal value(@NotNull UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }

        BigDecimal value = BigDecimal.ZERO;

        for (final ItemStack item : get(uuid)) {
            if (item == null) {
                continue;
            }

            final BancoItem bancoItem = Banco.get().getItemRegistry().getByItemStack(item);
            if (bancoItem == null) {
                continue;
            }

            value = value.add(bancoItem.value(item.getAmount()));
        }

        return value;
    }

    @Override
    @NotNull
    public BigDecimal add(@NotNull UUID uuid, @NotNull BigDecimal amount) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }

        BigDecimal amountGiven = BigDecimal.ZERO;

        for (final ItemStack itemStack : ItemUtil.convertAmountToItems(amount)) {
            final BancoItem bancoItem = Banco.get().getItemRegistry().getByItemStack(itemStack);
            if (bancoItem == null) {
                continue;
            }

            amountGiven = amountGiven.add(bancoItem.value(itemStack.getAmount()));

            final ItemStack remainderItemStack = addItem(uuid, itemStack);
            if (remainderItemStack == null) {
                continue;
            }

            final BancoItem remainderBancoItem = Banco.get().getItemRegistry().getByItemStack(remainderItemStack);
            if (remainderBancoItem != null) {
                amountGiven = amountGiven.subtract(remainderBancoItem.value(remainderItemStack.getAmount()));
            }
        }

        return amountGiven;
    }

    @Override
    @NotNull
    public BigDecimal remove(@NotNull UUID uuid, @NotNull BigDecimal amount) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }

        for (final BancoItem bancoItem : Banco.get().getItemRegistry().get()) {
            for (final ItemStack item : get(uuid)) {
                if (item == null || !Objects.equals(Banco.get().getItemRegistry().getByItemStack(item), bancoItem)) {
                    continue;
                }

                if (amount.compareTo(BigDecimal.valueOf(0.01)) < 0) {
                    continue;
                }

                final BigDecimal value = bancoItem.value(item.getAmount());

                if (value.compareTo(BigDecimal.ZERO) > 0) {
                    removeItem(uuid, item);

                    BigDecimal removed = BigDecimal.ZERO;
                    if (value.compareTo(amount) > 0) {
                        removed = value.subtract(amount);

                        // Try returning change to the storage that supplied the payment first.
                        // If it doesn't fit, try other storages; any leftover is deposited.
                        BigDecimal toReturn = removed;

                        try {
                            final BigDecimal addedBack = this.add(uuid, toReturn);
                            toReturn = toReturn.subtract(addedBack);
                        } catch (Exception e) {
                            // If adding back to this storage fails for any reason, continue to fallback
                        }

                        if (toReturn.compareTo(BigDecimal.ZERO) > 0) {
                            for (final BancoStorage storage : Banco.get().getStorageRegistry().getByOrder()) {
                                if (storage == this) {
                                    continue;
                                }

                                try {
                                    final BigDecimal added = storage.add(uuid, toReturn);
                                    toReturn = toReturn.subtract(added);
                                } catch (Exception e) {
                                    // Ignore and try next storage
                                }

                                if (toReturn.compareTo(BigDecimal.ZERO) <= 0) {
                                    break;
                                }
                            }
                        }

                        if (toReturn.compareTo(BigDecimal.ZERO) > 0) {
                            // Fallback to account transactions if some remainder couldn't be placed in
                            // any storage.
                            Banco.get().getAccountManager().deposit(uuid, toReturn, false);
                        }
                    }

                    amount = amount.subtract(value.subtract(removed));
                }
            }
        }

        return amount;
    }
}
