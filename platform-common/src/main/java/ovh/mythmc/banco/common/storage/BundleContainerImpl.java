package ovh.mythmc.banco.common.storage;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BundleMeta;
import ovh.mythmc.banco.api.storage.BancoContainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public final class BundleContainerImpl extends BancoContainer {

    @Override
    public String friendlyName() {
        return "BUNDLE";
    }

    @Override
    public Collection<ItemStack> get(UUID uuid) {
        List<ItemStack> items = new ArrayList<>();
        var player = Bukkit.getPlayer(uuid);
        if (player == null) return items;

        items.addAll(getBundlesContent(player.getInventory().getContents()));
        items.addAll(getBundlesContent(player.getEnderChest().getContents()));
        return items;
    }

    @Override
    protected ItemStack addItem(UUID uuid, ItemStack itemStack) {
        var player = Bukkit.getPlayer(uuid);
        if (player == null || itemStack == null) return itemStack;

        itemStack = addToBundlesInContainer(player.getInventory().getContents(), itemStack);
        if (itemStack == null || itemStack.getAmount() <= 0) return null;

        itemStack = addToBundlesInContainer(player.getEnderChest().getContents(), itemStack);
        return (itemStack == null || itemStack.getAmount() <= 0) ? null : itemStack;
    }

    @Override
    protected ItemStack removeItem(UUID uuid, ItemStack itemStack) {
        var player = Bukkit.getPlayer(uuid);
        if (player == null || itemStack == null) return null;

        itemStack = removeFromBundlesInContainer(player.getInventory().getContents(), itemStack);
        if (itemStack == null || itemStack.getAmount() <= 0) return null;

        itemStack = removeFromBundlesInContainer(player.getEnderChest().getContents(), itemStack);
        return (itemStack == null || itemStack.getAmount() <= 0) ? null : itemStack;
    }

    private List<ItemStack> getBundlesContent(ItemStack[] container) {
        List<ItemStack> content = new ArrayList<>();

        for (ItemStack item : container) {
            if (item == null || item.getType().isAir()) continue;

            if (isBundle(item)) {
                BundleMeta meta = (BundleMeta) item.getItemMeta();
                content.addAll(getItemsFromBundle(meta));
            }

            BlockStateMeta shulkerMeta = getShulkerMeta(item);
            if (shulkerMeta != null && shulkerMeta.getBlockState() instanceof ShulkerBox box) {
                for (ItemStack inner : box.getInventory().getContents()) {
                    if (isBundle(inner)) {
                        BundleMeta meta = (BundleMeta) inner.getItemMeta();
                        content.addAll(getItemsFromBundle(meta));
                    }
                }
            }
        }

        return content;
    }

    private ItemStack addToBundlesInContainer(ItemStack[] container, ItemStack itemStack) {
        for (ItemStack item : container) {
            if (item == null || item.getType().isAir()) continue;

            if (isBundle(item)) {
                itemStack = addItemToBundleStack(item, itemStack);
                if (itemStack == null || itemStack.getAmount() <= 0) return null;
            }

            BlockStateMeta meta = getShulkerMeta(item);
            if (meta != null && meta.getBlockState() instanceof ShulkerBox box) {
                Inventory inv = box.getInventory();
                for (int i = 0; i < inv.getSize(); i++) {
                    ItemStack inner = inv.getItem(i);
                    if (!isBundle(inner)) continue;

                    itemStack = addItemToBundleStack(inner, itemStack);
                    inv.setItem(i, inner);
                    meta.setBlockState(box);
                    item.setItemMeta(meta);

                    if (itemStack == null || itemStack.getAmount() <= 0) return null;
                }
            }
        }
        return itemStack;
    }

    private ItemStack removeFromBundlesInContainer(ItemStack[] container, ItemStack target) {
        for (ItemStack item : container) {
            if (item == null || item.getType().isAir()) continue;

            if (isBundle(item)) {
                if (removeItemFromBundleStack(item, target)) return null;
            }

            BlockStateMeta meta = getShulkerMeta(item);
            if (meta != null && meta.getBlockState() instanceof ShulkerBox box) {
                Inventory inv = box.getInventory();
                for (int i = 0; i < inv.getSize(); i++) {
                    ItemStack inner = inv.getItem(i);
                    if (!isBundle(inner)) continue;

                    if (removeItemFromBundleStack(inner, target)) {
                        inv.setItem(i, inner);
                        meta.setBlockState(box);
                        item.setItemMeta(meta);
                        return null;
                    }
                }
            }
        }
        return target;
    }

    private ItemStack addItemToBundleStack(ItemStack bundleItem, ItemStack toAdd) {
        BundleMeta meta = (BundleMeta) bundleItem.getItemMeta();
        if (meta == null) return toAdd;

        ItemStack result = addItemToBundleMeta(meta, toAdd);
        bundleItem.setItemMeta(meta);
        return result;
    }

    private boolean removeItemFromBundleStack(ItemStack bundleItem, ItemStack toRemove) {
        BundleMeta meta = (BundleMeta) bundleItem.getItemMeta();
        if (meta == null) return false;

        boolean removed = removeItemFromBundleMeta(meta, toRemove);
        if (removed) bundleItem.setItemMeta(meta);
        return removed;
    }

    private ItemStack addItemToBundleMeta(BundleMeta meta, ItemStack itemStack) {
        if (itemStack == null) return null;
        List<ItemStack> contents = new ArrayList<>(meta.getItems());
        int pending = itemStack.getAmount();

        for (ItemStack inside : contents) {
            if (!inside.isSimilar(itemStack)) continue;
            int canAdd = Math.min(
                    inside.getMaxStackSize() - inside.getAmount(),
                    pending
            );
            inside.setAmount(inside.getAmount() + canAdd);
            pending -= canAdd;
            if (pending <= 0) break;
        }

        if (pending > 0 && getBundleListFreeCapacity(contents) > 0) {
            int free = Math.min(pending, getBundleListFreeCapacity(contents));
            ItemStack clone = itemStack.clone();
            clone.setAmount(free);
            contents.add(clone);
            pending -= free;
        }

        meta.setItems(contents.stream().filter(i -> !i.getType().isAir()).toList());
        if (pending <= 0) return null;

        ItemStack remainder = itemStack.clone();
        remainder.setAmount(pending);
        return remainder;
    }

    private boolean removeItemFromBundleMeta(BundleMeta meta, ItemStack target) {
        List<ItemStack> contents = new ArrayList<>(meta.getItems());
        for (int i = 0; i < contents.size(); i++) {
            ItemStack inside = contents.get(i);
            if (!inside.isSimilar(target)) continue;

            int removeAmount = Math.min(inside.getAmount(), target.getAmount());
            inside.setAmount(inside.getAmount() - removeAmount);

            if (inside.getAmount() <= 0) contents.remove(i);
            meta.setItems(contents);
            return true;
        }
        return false;
    }

    private List<ItemStack> getItemsFromBundle(BundleMeta meta) {
        return new ArrayList<>(meta.getItems());
    }

    private boolean isBundle(ItemStack item) {
        return item != null && item.getType().name().contains("BUNDLE");
    }

    private boolean isShulkerBox(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        return item.getType().name().endsWith("SHULKER_BOX");
    }

    private BlockStateMeta getShulkerMeta(ItemStack item) {
        if (!isShulkerBox(item)) return null;
        try {
            var meta = item.getItemMeta();
            if (!(meta instanceof BlockStateMeta)) {
                item.setItemMeta(Bukkit.getItemFactory().asMetaFor(meta, item.getType()));
                meta = item.getItemMeta();
            }
            return (meta instanceof BlockStateMeta) ? (BlockStateMeta) meta : null;
        } catch (Exception e) {
            return null;
        }
    }

    private int getBundleListFreeCapacity(List<ItemStack> contents) {
        int free = 64;
        for (ItemStack i : contents) free -= i.getAmount();
        return Math.max(free, 0);
    }
}
