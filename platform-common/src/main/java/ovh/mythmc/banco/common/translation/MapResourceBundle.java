package ovh.mythmc.banco.common.translation;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.ResourceBundle;

public final class MapResourceBundle extends ResourceBundle {

    Map<String, Object> map;

    public MapResourceBundle(final Map<String, Object> map) {
        this.map = map;
    }

    @Override
    protected Object handleGetObject(final @NotNull String key) {
        return map.get(key);
    }

    @Override
    public @NotNull Enumeration<String> getKeys() {
        return Collections.enumeration(map.keySet());
    }

}