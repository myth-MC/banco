package ovh.mythmc.banco.common.storage;

import java.math.BigDecimal;
import java.util.ArrayList;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;

import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.Account;
import ovh.mythmc.banco.api.items.BancoItem;
import ovh.mythmc.banco.api.storage.BancoStorage;
import ovh.mythmc.banco.api.util.ItemUtil;

public final class BundleStorageImpl implements BancoStorage {

    @Override
    public BigDecimal value(UUID uuid) {
        BigDecimal value = BigDecimal.valueOf(0);

        for (ItemStack item : getCombinedBundlesContent(uuid)) {
            if (item == null)
                continue;

            BancoItem bancoItem = Banco.get().getItemManager().get(item);
            if (bancoItem == null)
                continue;
                
            value = value.add(Banco.get().getItemManager().value(bancoItem, item.getAmount()));
        }

        return value;
    }

    @Override
    public BigDecimal add(UUID uuid, BigDecimal amount) {
        BigDecimal amountGiven = BigDecimal.valueOf(0);

        for (ItemStack item : ItemUtil.convertAmountToItems(amount)) {
            BancoItem bancoItem = Banco.get().getItemManager().get(item);

            if (addToBundle(uuid, item.clone()))
                amountGiven = amountGiven.add(Banco.get().getItemManager().value(bancoItem, item.getAmount()));
        }

        return amountGiven;
    }

    private boolean addToBundle(UUID uuid, ItemStack itemStack) {
        boolean added = false;

        for (ItemStack bundle : Bukkit.getPlayer(uuid).getInventory()) {
            if (getBundleFreeCapacity(bundle) < 1)
                continue;

            if (itemStack.getAmount() < 1)
                break;

            BundleMeta bundleMeta = (BundleMeta) bundle.getItemMeta();
            int pendingAmountToAdd = itemStack.getAmount();

            // Attempt to add to existing stacks
            List<ItemStack> newItemList = new ArrayList<>(bundleMeta.getItems());
            for (int i = 0; i < newItemList.size(); i++) {
                ItemStack bundleItemStack = newItemList.get(i);
                if (bundleItemStack.isSimilar(itemStack) && bundleItemStack.getAmount() < bundleItemStack.getMaxStackSize()) {
                    // Minimum between max stack size (64) and amount that we'd want to add ideally
                    int newAmount = Math.min(bundleItemStack.getMaxStackSize(), bundleItemStack.getAmount() + itemStack.getAmount());

                    // Minimum between existing newAmount and bundle free capacity
                    newAmount = Math.min(newAmount, Math.max(getBundleListFreeCapacity(newItemList) - newAmount, 0));

                    Banco.get().getLogger().info(newAmount + " newAmount");

                    pendingAmountToAdd = pendingAmountToAdd - (newAmount - bundleItemStack.getAmount());
                    bundleItemStack.setAmount(newAmount);

                    itemStack.setAmount(pendingAmountToAdd);
                }
            }

            // Add the remaining amount that couldn't be added to existing stacks as new ones
            //itemStack.setAmount(pendingAmountToAdd);
            if (pendingAmountToAdd > 0) {
                int freeCapacity = Math.min(pendingAmountToAdd, getBundleListFreeCapacity(newItemList));
                itemStack.setAmount(freeCapacity);

                // Add to list
                newItemList.add(itemStack.clone());

                // Update itemStack amount
                itemStack.setAmount(pendingAmountToAdd - freeCapacity);
            }

            // Update bundle contents
            bundleMeta.setItems(newItemList.stream().filter(i -> !i.getType().isAir()).toList());
            bundle.setItemMeta(bundleMeta);
            added = true;
        }

        // We'll only drop items to the floor if any other items have already been added to a bundle
        if (added) {
            if (itemStack.getAmount() > 0) {
                Player player = Bukkit.getPlayer(uuid);
                player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
            }
        }

        return added;
    }

    @Override
    public BigDecimal remove(UUID uuid, BigDecimal amount) {
        for (BancoItem bancoItem : Banco.get().getItemManager().get()) {
            for (ItemStack item : getCombinedBundlesContent(uuid)) {
                if (item == null || !Banco.get().getItemManager().get(item).equals(bancoItem)) continue;
                if (amount.compareTo(BigDecimal.valueOf(0.01)) < 0) continue;
    
                BigDecimal value = Banco.get().getItemManager().value(bancoItem, item.getAmount());
    
                if (value.compareTo(BigDecimal.valueOf(0)) > 0) {
                    removeFromBundle(uuid, item);

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
        }

        return amount;
    }

    private boolean removeFromBundle(UUID uuid, ItemStack itemStack) {
        boolean removed = false;

        for (ItemStack bundle : Bukkit.getPlayer(uuid).getInventory()) {
            if (bundle == null || !bundle.getType().name().contains("BUNDLE"))
                continue;

            BundleMeta bundleMeta = (BundleMeta) bundle.getItemMeta();
            if (!bundleMeta.getItems().contains(itemStack))
                continue;

            List<ItemStack> updatedItems = new ArrayList<>(bundleMeta.getItems());
            updatedItems.remove(itemStack);

            bundleMeta.setItems(updatedItems);
            bundle.setItemMeta(bundleMeta);

            removed = true;
            break;
        }

        return removed;
    }

    private List<ItemStack> getCombinedBundlesContent(UUID uuid) {
        List<ItemStack> content = new ArrayList<>();

        for (ItemStack bundle : Bukkit.getPlayer(uuid).getInventory()) {
            if (bundle == null || !bundle.getType().name().contains("BUNDLE"))
                continue;

            BundleMeta bundleMeta = (BundleMeta) bundle.getItemMeta();
            content.addAll(bundleMeta.getItems());
        }

        return content;
    }

    private int getBundleFreeCapacity(ItemStack bundle) {
        if (bundle == null || !bundle.getType().name().contains("BUNDLE"))
            return 0;

        int freeCapacity = 64;

        BundleMeta bundleMeta = (BundleMeta) bundle.getItemMeta();
        for (ItemStack itemStack : bundleMeta.getItems()) {
            freeCapacity = freeCapacity - itemStack.getAmount();
        }

        return freeCapacity;
    }

    private int getBundleListFreeCapacity(List<ItemStack> bundleContent) {
        int freeCapacity = 64;

        for (ItemStack itemStack : bundleContent) {
            freeCapacity = freeCapacity - itemStack.getAmount();
        }

        return freeCapacity;
    }
    
}
