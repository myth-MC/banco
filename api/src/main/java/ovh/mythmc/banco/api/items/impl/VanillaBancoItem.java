package ovh.mythmc.banco.api.items.impl;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.items.BancoItem;

public record VanillaBancoItem(Material material, BigDecimal value, BancoItemOptions customization) implements BancoItem {

    @Override
    public boolean match(ItemStack itemStack) {
        // Match by basic ItemStack
        if (itemStack.isSimilar(asItemStack()))
            return true;

        // Match by identifier
        NamespacedKey key = Banco.get().getItemRegistry().CUSTOM_ITEM_IDENTIFIER_KEY;
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().getPersistentDataContainer().has(key)) {
            String itemStackIdentifier = itemStack.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
            if (getIdentifier().equals(itemStackIdentifier))
                return true;
        }

        return false;
    }

    @Override
    public ItemStack asItemStack(int amount) {
        ItemStack itemStack = new ItemStack(material, amount);

        if (customization() != null) {
            ItemMeta itemMeta = itemStack.getItemMeta();

            // Apply custom display name
            if (customization().displayName() != null)
                itemMeta.setDisplayName(format(customization().displayName()));

            // Apply lore
            if (customization().lore() != null)
                itemMeta.setLore(customization().lore().stream().map(this::format).toList());

            // Apply custom model data
            if (customization().customModelData() != null)
                itemMeta.setCustomModelData(customization().customModelData());

            // Apply glow effect
            if (customization().glowEffect() != null && customization().glowEffect())
                itemMeta.addEnchant(Enchantment.LOYALTY, 1, true);

            // Apply head texture URL
            if (customization().headTextureUrl() != null && material().equals(Material.PLAYER_HEAD))
                ((SkullMeta) itemMeta).setOwnerProfile(getProfile(customization().headTextureUrl()));

            // Apply max stack size
            if (customization().maxStackSize() != null)
                itemMeta.setMaxStackSize(customization().maxStackSize());

            // Hide enchantments (used for glow effect)
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            // Store item identifier in PDC
            itemMeta.getPersistentDataContainer().set(Banco.get().getItemRegistry().CUSTOM_ITEM_IDENTIFIER_KEY, PersistentDataType.STRING, getIdentifier());

            // Apply attribute modifiers
            if (customization().attributes() != null) {
                customization().attributes().forEach(attribute -> itemMeta.addAttributeModifier(attribute.getAttribute(), attribute.getAttributeModifier()));
            }

            // Apply ItemMeta
            itemStack.setItemMeta(itemMeta);
        }

        return itemStack;
    }

    private PlayerProfile getProfile(String textureUrl) {
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.nameUUIDFromBytes(textureUrl.getBytes())); // Get a new player profile
        PlayerTextures textures = profile.getTextures();
        URL urlObject;
        try {
            urlObject = URI.create(textureUrl).toURL(); // The URL to the skin, for example: https://textures.minecraft.net/texture/18813764b2abc94ec3c3bc67b9147c21be850cdf996679703157f4555997ea63a
        } catch (MalformedURLException exception) {
            throw new RuntimeException("Invalid URL", exception);
        }
        textures.setSkin(urlObject); // Set the skin of the player profile to the URL
        profile.setTextures(textures); // Set the textures back to the profile
        return profile;
    }

    private String format(String input) {
        Component component = MiniMessage.miniMessage().deserialize(input);
        return LegacyComponentSerializer.legacySection().serialize(component);
    } 

    private String getIdentifier() {
        return material + "-" + value + "-" + customization;
    }

    public record BancoItemOptions(String displayName, List<String> lore, Integer customModelData, Boolean glowEffect, Integer maxStackSize, String headTextureUrl, List<AttributeField> attributes) {
        
        public Boolean glowEffect() {
            if (glowEffect == null)
                return false;

            return glowEffect;
        }

        public record AttributeField(String key, double amount, AttributeModifier.Operation operation, String group) {
            
            public Attribute getAttribute() {
                return Registry.ATTRIBUTE.get(NamespacedKey.fromString(key));
            }

            public AttributeModifier getAttributeModifier() {
                return new AttributeModifier(new NamespacedKey("banco", UUID.nameUUIDFromBytes(key.getBytes()).toString()), amount, operation, EquipmentSlotGroup.getByName(group));
            }

        }
    }

}
