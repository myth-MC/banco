package ovh.mythmc.banco.utils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class PlayerUtils {

    public static UUID getUuid(String name) {
        name = "OfflinePlayer:" + name;
        return UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
    }

}
