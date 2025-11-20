package ovh.mythmc.banco.paper.dialog;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.scheduler.BancoScheduler;
import ovh.mythmc.banco.common.update.UpdateChecker;

public final class InfoDialog {

    public void open(@NotNull Player player) {
        final List<DialogBody> dialogBodyList = new ArrayList<>();
        dialogBodyList.add(DialogBody.plainMessage(MiniMessage.miniMessage().deserialize(Banco.get().getSettings().get().getDialogs().getInfo().description())));

        dialogBodyList.addAll(List.of(
            DialogBody.plainMessage(Component.text("Running ")
                .append(Component.text("banco ", NamedTextColor.LIGHT_PURPLE))
                .append(Component.text("v" + Banco.get().version()))
            ),
            DialogBody.plainMessage(Component.text("Server version: ")
                .append(Component.text(Bukkit.getBukkitVersion(), NamedTextColor.GRAY))
            ),
            DialogBody.plainMessage(Component.text("Online mode: ")
                .append(Component.text(Bukkit.getBukkitVersion(), NamedTextColor.GRAY))
            ),
            DialogBody.plainMessage(Component.text("Items: ")
                .append(Component.text(Banco.get().getItemRegistry().get().size() + "", NamedTextColor.GRAY))
            ),
            DialogBody.plainMessage(Component.text("Storages: ")
                .append(Component.text(Banco.get().getStorageRegistry().get().size() + "", NamedTextColor.GRAY))
            ),
            DialogBody.plainMessage(Component.text("Accounts: ")
                .append(Component.text(Banco.get().getAccountManager().get().size() + " (" + Banco.get().getAccountManager().getDatabase().getCachedAccounts().size() + " cached)", NamedTextColor.GRAY))
            ),
            DialogBody.plainMessage(Component.text("Transactions in Queue: ")
                .append(Component.text(BancoScheduler.get().getQueuedTransactions().size() + "", NamedTextColor.GRAY))
            )
        ));

        if (!UpdateChecker.getLatest().equals(Banco.get().version()))
            dialogBodyList.add(
                DialogBody.plainMessage(Component.text("New version available: " + UpdateChecker.getLatest(), NamedTextColor.YELLOW))  
            );


        final Dialog dialog = Dialog.create(builder -> builder.empty()
            .base(DialogBase.builder(MiniMessage.miniMessage().deserialize(Banco.get().getSettings().get().getDialogs().getInfo().title()))
                .body(dialogBodyList)
                .build())
            .type(DialogType.notice())
        );

        player.showDialog(dialog);
        
    }
    
}
