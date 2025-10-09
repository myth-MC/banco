package ovh.mythmc.banco.common.storage;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import ovh.mythmc.banco.api.storage.BancoContainer;

import java.util.*;

/**
 * BancoContainer implementation for Shulker boxes.
 * scans inside the players inventory and ender chest!!
 */
public final class ShulkerBoxContainerImpl extends BancoContainer {

    @Override
    public String friendlyName() {
        return "SHULKER_BOX";
    }

    @Override
    public Collection<ItemStack> get(UUID uuid) {
        List<ItemStack> allItems = new ArrayList<>();
        var player = Bukkit.getPlayer(uuid);
        if (player == null) return allItems;

        allItems.addAll(getItemsFromShulkers(player.getInventory().getContents()));
        allItems.addAll(getItemsFromShulkers(player.getEnderChest().getContents()));

        return allItems;
    }

    @Override
    protected ItemStack addItem(UUID uuid, ItemStack itemStack) {
        if (itemStack == null) return null;

        var player = Bukkit.getPlayer(uuid);
        if (player == null) return itemStack;

        itemStack = addToShulkers(player.getInventory().getContents(), itemStack);
        if (itemStack == null || itemStack.getAmount() <= 0)
            return null;

        itemStack = addToShulkers(player.getEnderChest().getContents(), itemStack);
        return itemStack;
    }

    @Override
    protected ItemStack removeItem(UUID uuid, ItemStack itemStack) {
        if (itemStack == null) return null;

        var player = Bukkit.getPlayer(uuid);
        if (player == null) return itemStack;

        itemStack = removeFromShulkers(player.getInventory().getContents(), itemStack);
        if (itemStack == null || itemStack.getAmount() <= 0)
            return null;

        itemStack = removeFromShulkers(player.getEnderChest().getContents(), itemStack);
        return itemStack;
    }

    private ItemStack addToShulkers(ItemStack[] stacks, ItemStack itemStack) {
        if (itemStack == null) return null;

        for (ItemStack shulker : stacks) {
            BlockStateMeta meta = getShulkerMeta(shulker);
            if (meta == null) continue;
            if (!(meta.getBlockState() instanceof ShulkerBox)) continue;

            ShulkerBox box = (ShulkerBox) meta.getBlockState();
            Inventory inv = box.getInventory();

            Map<Integer, ItemStack> leftovers = inv.addItem(itemStack.clone());

            meta.setBlockState(box);
            shulker.setItemMeta(meta);

            if (leftovers.isEmpty()) return null;

            int remaining = leftovers.values().stream().mapToInt(ItemStack::getAmount).sum();
            itemStack = itemStack.clone();
            itemStack.setAmount(remaining);
        }

        return itemStack;
    }

    private ItemStack removeFromShulkers(ItemStack[] stacks, ItemStack itemStack) {
        if (itemStack == null) return null;

        int toRemove = itemStack.getAmount();

        for (ItemStack shulker : stacks) {
            BlockStateMeta meta = getShulkerMeta(shulker);
            if (meta == null || !(meta.getBlockState() instanceof ShulkerBox)) continue;

            ShulkerBox box = (ShulkerBox) meta.getBlockState();
            Inventory inv = box.getInventory();

            for (int i = 0; i < inv.getSize(); i++) {
                ItemStack content = inv.getItem(i);
                if (content == null) continue;

                if (content.isSimilar(itemStack)) {
                    int removed = Math.min(content.getAmount(), toRemove);
                    content.setAmount(content.getAmount() - removed);
                    toRemove -= removed;

                    if (content.getAmount() <= 0) inv.setItem(i, null);

                    meta.setBlockState(box);
                    shulker.setItemMeta(meta);

                    if (toRemove <= 0) return null;
                }
            }
        }

        if (toRemove <= 0) return null;

        ItemStack remainder = itemStack.clone();
        remainder.setAmount(toRemove);
        return remainder;
    }

    private Collection<ItemStack> getItemsFromShulkers(ItemStack[] stacks) {
        List<ItemStack> items = new ArrayList<>();
        for (ItemStack shulker : stacks) {
            BlockStateMeta meta = getShulkerMeta(shulker);
            if (meta == null || !(meta.getBlockState() instanceof ShulkerBox)) continue;

            ShulkerBox box = (ShulkerBox) meta.getBlockState();

            for (ItemStack item : box.getInventory().getContents()) {
                if (item != null && !item.getType().isAir()) {
                    items.add(item.clone());
                }
            }
        }
        return items;
    }

    private boolean isShulkerBox(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        return item.getType().name().endsWith("SHULKER_BOX");
    }

    private BlockStateMeta getShulkerMeta(ItemStack shulker) {
        if (shulker == null || !isShulkerBox(shulker)) return null;
        try {
            var meta = shulker.getItemMeta();
            if (!(meta instanceof BlockStateMeta)) {
                shulker.setItemMeta(Bukkit.getItemFactory().asMetaFor(meta, shulker.getType()));
                meta = shulker.getItemMeta();
            }
            return (meta instanceof BlockStateMeta) ? (BlockStateMeta) meta : null;
        } catch (Exception e) {
            return null;
        }
    }
}
