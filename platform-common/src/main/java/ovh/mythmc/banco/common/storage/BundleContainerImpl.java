package ovh.mythmc.banco.common.storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;

import ovh.mythmc.banco.api.storage.BancoContainer;

public final class BundleContainerImpl extends BancoContainer {

    @Override
    public String friendlyName() {
        return "BUNDLE";
    }

    @Override
    public Collection<ItemStack> get(UUID uuid) {
        return getCombinedBundlesContent(uuid);
    }

    @Override
    protected ItemStack addItem(UUID uuid, ItemStack itemStack) {
        var player = Bukkit.getPlayer(uuid);
        if (player == null) return itemStack;

        List<ItemStack[]> containers = List.of(player.getInventory().getContents(), player.getEnderChest().getContents());

        for (ItemStack[] container : containers) {
            for (ItemStack bundle : container) {
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

                        pendingAmountToAdd -= (newAmount - bundleItemStack.getAmount());
                        bundleItemStack.setAmount(newAmount);

                        itemStack.setAmount(pendingAmountToAdd);
                    }
                }

                // Add the remaining amount that couldn't be added to existing stacks as new ones
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
            }
        }

        return itemStack;
    }

    @Override
    protected ItemStack removeItem(UUID uuid, ItemStack itemStack) {
        var player = Bukkit.getPlayer(uuid);
        if (player == null) return null;

        ItemStack removedItem = null;

        List<ItemStack[]> containers = List.of(player.getInventory().getContents(), player.getEnderChest().getContents());

        for (ItemStack[] container : containers) {
            for (ItemStack bundle : container) {
                if (bundle == null || !bundle.getType().name().contains("BUNDLE"))
                    continue;

                BundleMeta bundleMeta = (BundleMeta) bundle.getItemMeta();
                if (!bundleMeta.getItems().contains(itemStack))
                    continue;

                List<ItemStack> updatedItems = new ArrayList<>(bundleMeta.getItems());
                updatedItems.remove(itemStack);

                bundleMeta.setItems(updatedItems);
                bundle.setItemMeta(bundleMeta);

                removedItem = itemStack;
                break;
            }

            if (removedItem != null)
                break;
        }

        return removedItem;
    }

    private List<ItemStack> getCombinedBundlesContent(UUID uuid) {
        List<ItemStack> content = new ArrayList<>();
        var player = Bukkit.getPlayer(uuid);
        if (player == null) return content;

        // inventory
        for (ItemStack bundle : player.getInventory()) {
            if (bundle == null || !bundle.getType().name().contains("BUNDLE"))
                continue;

            BundleMeta bundleMeta = (BundleMeta) bundle.getItemMeta();
            content.addAll(bundleMeta.getItems());
        }

        // ender chest
        for (ItemStack bundle : player.getEnderChest()) {
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
