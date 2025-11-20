package ovh.mythmc.banco.paper.dialog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.service.OfflinePlayerReference;
import ovh.mythmc.banco.common.util.MessageUtil;

public final class BalanceTopDialog {

    public void open(@NotNull Player player) {
        Banco.get().getAccountManager().getTopAsync(1024000).thenAccept(map -> {
            final List<DialogBody> dialogBodyList = new ArrayList<>();
            dialogBodyList.add(DialogBody.plainMessage(Component.text(Banco.get().getSettings().get().getDialogs().getBalanceTop().description())));

            int index = 0;
    
            for (Map.Entry<UUID, BigDecimal> entry : map.entrySet()) {
                if (index >= 9)
                    break;
    
                final Optional<OfflinePlayerReference> optPlayerReference = Banco.get().getAccountManager().getUuidResolver().resolveOfflinePlayer(entry.getKey());
                if (optPlayerReference.isEmpty() || !optPlayerReference.get().toOfflinePlayer().hasPlayedBefore())
                    continue;
    
                final String balance = MessageUtil.format(entry.getValue()) + Banco.get().getSettings().get().getCurrency().getSymbol();
                final String formattedText = String.format(Banco.get().getSettings().get().getDialogs().getBalanceTop().format(),
                        index+1,
                        optPlayerReference.get().toOfflinePlayer().getName(),
                        balance
                );
    
                dialogBodyList.add(DialogBody.plainMessage(
                    MiniMessage.miniMessage().deserialize(formattedText)
                ));
    
                index++;
            }
    
            final Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(MiniMessage.miniMessage().deserialize(Banco.get().getSettings().get().getDialogs().getBalanceTop().title()))
                    .body(dialogBodyList)
                    .build())
                .type(DialogType.notice())
            );
    
            player.showDialog(dialog);
        });
    }
    
}
