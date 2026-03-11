package ovh.mythmc.banco.api.items.impl;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import de.exlll.configlib.Configuration;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.With;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import ovh.mythmc.banco.api.items.BancoItem;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @param name a case-sensitive material's name, for example GOLD_INGOT
 * @param displayName this item's display name
 * @param lore this item's lore text
 * @param customModelData this item's custom model data
 * @param glowEffect whether item should glow or not
 * @param value value of this item
 */
@With(AccessLevel.PACKAGE)
@AllArgsConstructor
@Configuration
public final class LegacyBancoItem implements BancoItem {

    private @NotNull String name;

    private Component displayName;

    private List<String> lore;

    private Integer customModelData;

    private Boolean glowEffect;

    private @NotNull BigDecimal value;

    LegacyBancoItem() {
    }

    @Override
    public Component displayName() {
        return this.displayName;
    }

    @Override
    public BigDecimal value() {
        return this.value;
    }

    @Override
    public ItemStack asItemStack(int amount) {
        ItemStack itemStack = new ItemStack(Material.valueOf(name), amount);

        ItemMeta itemMeta = itemStack.getItemMeta();

        // Apply custom display name
        if (displayName != null)
            itemMeta.setDisplayName(PlainTextComponentSerializer.plainText().serialize(displayName));

        // Apply lore
        if (lore != null)
            itemMeta.setLore(lore.stream().toList());

        // Apply custom model data
        if (customModelData != null)
            itemMeta.setCustomModelData(customModelData);

        // Apply glow effect
        if (glowEffect != null && glowEffect)
            itemMeta.addEnchant(Enchantment.LOYALTY, 1, true);;

        // Hide enchantments (used for glow effect)
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        // Apply ItemMeta
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
    
}
