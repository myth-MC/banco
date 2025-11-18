package ovh.mythmc.banco.api.configuration.serializer;

import de.exlll.configlib.Serializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class ComponentSerializer implements Serializer<Component, String> {

    @Override
    public Component deserialize(String string) {
        return string == null 
            ? Component.empty() 
            : MiniMessage.miniMessage().deserialize(string);
    }

    @Override
    public String serialize(Component component) {
        return component == null
            ? ""
            : MiniMessage.miniMessage().serialize(component);
    }
    
}
