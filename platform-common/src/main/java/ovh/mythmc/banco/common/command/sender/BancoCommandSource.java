package ovh.mythmc.banco.common.command.sender;

import net.kyori.adventure.audience.ForwardingAudience;

public interface BancoCommandSource extends ForwardingAudience.Single {

    Object source();

    boolean isPlayer();

    String name();
    
}
