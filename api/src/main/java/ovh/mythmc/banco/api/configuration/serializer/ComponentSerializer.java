package ovh.mythmc.banco.api.configuration.serializer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.exlll.configlib.Serializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

/**
 * Serializer for Adventure Component objects in YAML configuration files.
 * <p>
 * This serializer converts between Adventure Component objects and MiniMessage strings,
 * allowing components to be stored in the configuration file as text.
 * </p>
 *
 * @since 1.0.0
 */
public final class ComponentSerializer implements Serializer<Component, String> {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    @Override
    @NotNull
    public Component deserialize(@Nullable String string) {
        if (string == null || string.trim().isEmpty()) {
            return Component.empty();
        }

        try {
            return MINI_MESSAGE.deserialize(string);
        } catch (Exception e) {
            // If deserialization fails, return empty component
            return Component.empty();
        }
    }

    @Override
    @NotNull
    public String serialize(@Nullable Component component) {
        if (component == null) {
            return "";
        }

        try {
            return MINI_MESSAGE.serialize(component);
        } catch (Exception e) {
            // If serialization fails, return empty string
            return "";
        }
    }
}
