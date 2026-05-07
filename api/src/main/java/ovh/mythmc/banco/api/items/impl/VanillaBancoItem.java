package ovh.mythmc.banco.api.items.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.annotation.Nullable;

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
import org.jetbrains.annotations.NotNull;

import de.exlll.configlib.Configuration;
import de.exlll.configlib.Ignore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import ovh.mythmc.banco.api.items.BancoItem;
import ovh.mythmc.banco.api.items.BancoItemRegistry;

@Configuration
public final class VanillaBancoItem implements BancoItem {

    private Material material;

    private BigDecimal value;

    private BancoItemOptions customization;

    @Ignore
    private ItemStack itemStack;

    @Ignore
    private String identifier;

    VanillaBancoItem() {
    }

    public VanillaBancoItem(@NotNull Material material, @NotNull BigDecimal value, @Nullable BancoItemOptions customization) {
        this.material = material;
        this.value = value;
        this.customization = customization;
    }

    @Override
    public Component displayName() {
        if (this.customization != null && this.customization.displayName() != null)
            return MiniMessage.miniMessage().deserialize(this.customization.displayName());

        return Component.translatable(material.getTranslationKey());
    }

    @Override
    public BigDecimal value() {
        return this.value;
    }

    @Override
    public ItemStack asItemStack(int amount) {
        if (this.itemStack != null) {
            final var itemStack = this.itemStack.clone();
            itemStack.setAmount(amount);
            return itemStack;
        }

        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (customization != null) {
            // Apply custom display name
            if (customization.displayName() != null)
                itemMeta.setDisplayName(format(customization.displayName()));

            // Apply lore
            if (customization.lore() != null)
                itemMeta.setLore(customization.lore().stream().map(this::format).toList());

            // Apply custom model data
            if (customization.customModelData() != null)
                itemMeta.setCustomModelData(customization.customModelData());

            // Apply glow effect
            if (customization.glowEffect() != null && customization.glowEffect())
                itemMeta.addEnchant(Enchantment.LOYALTY, 1, true);

            // Apply head texture URL
            if (customization.headTextureUrl() != null && material.equals(Material.PLAYER_HEAD))
                ((SkullMeta) itemMeta).setOwnerProfile(getProfile(customization.headTextureUrl()));

            // Apply max stack size
            if (customization.maxStackSize() != null)
                itemMeta.setMaxStackSize(customization.maxStackSize());

            // Hide enchantments (used for glow effect)
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            // Apply attribute modifiers
            if (customization.attributes() != null) {
                customization.attributes().forEach(attribute -> itemMeta.addAttributeModifier(attribute.getAttribute(), attribute.getAttributeModifier()));
            }

            // Store item identifier in PDC
            itemMeta.getPersistentDataContainer().set(BancoItemRegistry.CUSTOM_ITEM_IDENTIFIER_KEY, PersistentDataType.STRING, getIdentifier());

            // Apply ItemMeta
            itemStack.setItemMeta(itemMeta);
        }

        this.itemStack = itemStack;
        return itemStack;
    }

    @Override
    public boolean match(@NotNull ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() != material) {
            return false;
        }

        if (!itemStack.hasItemMeta()) {
            return customization == null;
        }

        final ItemMeta meta = itemStack.getItemMeta();
        final String itemIdentifier = meta.getPersistentDataContainer().get(BancoItemRegistry.CUSTOM_ITEM_IDENTIFIER_KEY, PersistentDataType.STRING);

        if (itemIdentifier != null) {
            if (itemIdentifier.equals(getIdentifier())) {
                return true;
            }

            // Legacy comparison support:
            // Prior to v1.3.3, the identifier string was generated getting the string value of "customization",
            // which was way more restrictive
            String legacy = material + "-" + value + "-" + customization;
            return itemIdentifier.equals(legacy.replace(", restrictInteractions=null", ""));
        }

        // Fallback for legacy items or items without PDC
        if (customization == null) {
            // Check if it's similar to a plain ItemStack of this material
            return itemStack.isSimilar(new ItemStack(material));
        }

        return false;
    }

    public BancoItemOptions customization() {
        return this.customization;
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
        if (this.identifier != null) {
            return this.identifier;
        }

        if (customization == null) {
            return this.identifier = material.name() + ":" + value.toPlainString();
        }

        return this.identifier = material.name() + ":" + value.toPlainString() + ":" + customization.getSignature();
    }

    public static record BancoItemOptions(
        String displayName, 
        List<String> lore, 
        Integer customModelData, 
        Boolean glowEffect, 
        Integer maxStackSize, 
        String headTextureUrl, 
        List<AttributeField> attributes, 
        Boolean restrictInteractions
    ) implements Serializable {

        public String getSignature() {
            final Map<String, Object> components = new TreeMap<>();
            if (displayName != null) components.put("name", displayName);
            if (lore != null && !lore.isEmpty()) components.put("lore", lore);
            if (customModelData != null) components.put("model", customModelData);
            if (glowEffect != null && glowEffect) components.put("glow", true);
            if (maxStackSize != null) components.put("stack", maxStackSize);
            if (headTextureUrl != null) components.put("head", headTextureUrl);
            if (attributes != null && !attributes.isEmpty()) components.put("attr", attributes);
            if (restrictInteractions != null && restrictInteractions) components.put("restrict", true);
            
            return Integer.toHexString(components.hashCode());
        }

        public Boolean glowEffect() {
            if (glowEffect == null)
                return false;

            return glowEffect;
        }

        public record AttributeField(String key, double amount, AttributeModifier.Operation operation, String group) implements Serializable {
            
            public Attribute getAttribute() {
                return Registry.ATTRIBUTE.get(NamespacedKey.fromString(key));
            }

            public AttributeModifier getAttributeModifier() {
                return new AttributeModifier(new NamespacedKey("banco", UUID.nameUUIDFromBytes(key.getBytes()).toString()), amount, operation, EquipmentSlotGroup.getByName(group));
            }

        }

    }
    
}
