package ovh.mythmc.banco.common.util;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class PlayerUtil {

    public static UUID getUuid(String name) {
        name = "OfflinePlayer:" + name;
        return UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
    }

}
