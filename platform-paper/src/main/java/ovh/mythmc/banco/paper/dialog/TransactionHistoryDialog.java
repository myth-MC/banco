package ovh.mythmc.banco.paper.dialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.Account;
import ovh.mythmc.banco.api.accounts.Transaction;
import ovh.mythmc.banco.common.util.MessageUtil;

public final class TransactionHistoryDialog {

    public void open(@NotNull Player player, @NotNull Account account) {
        final List<DialogBody> dialogBodyList = new ArrayList<>();
        final String description = player.getUniqueId().equals(account.getUuid()) 
            ? Banco.get().getSettings().get().getDialogs().getTransactionHistory().description() 
            : String.format(Banco.get().getSettings().get().getDialogs().getTransactionHistory().othersDescription(), account.getName());

        dialogBodyList.add(DialogBody.plainMessage(MiniMessage.miniMessage().deserialize(description)));

        final List<Transaction.ImmutableView> transactionHistory = Banco.get().getAccountManager().getTransactionHistory().get(account);
        final int limit = transactionHistory.size() > 255 ? 255 : transactionHistory.size();

        for (int i = 0; i < limit; i++) {
            final Transaction.ImmutableView transaction = transactionHistory.get(i);
            if (transaction.operation() == Transaction.Operation.SET)
                continue;

            final SimpleDateFormat dateFormat = new SimpleDateFormat(Banco.get().getSettings().get().getDialogs().getTransactionHistory().dateFormat());

            final Component operationComponent = switch (transaction.operation()) {
                case DEPOSIT -> Component.text("+", NamedTextColor.GREEN);
                case WITHDRAW -> Component.text("-", NamedTextColor.RED);
                default -> Component.empty();
            };

            dialogBodyList.add(
                DialogBody.plainMessage(
                    Component.text(dateFormat.format(Date.from(transaction.timestamp())), NamedTextColor.GRAY)
                        .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
                        .append(operationComponent)
                        .append(Component.text(MessageUtil.format(transaction.amount()) + Banco.get().getSettings().get().getCurrency().getSymbol(), NamedTextColor.WHITE))
                )
            );
        }

        if (dialogBodyList.size() < 2)
            dialogBodyList.add(DialogBody.plainMessage(Component.text("Nothing to show yet.", NamedTextColor.GRAY)
                .appendSpace()
                .append(Component.text("Maybe tomorrow?", NamedTextColor.GRAY).decorate(TextDecoration.ITALIC))));

        final Dialog dialog = Dialog.create(builder -> builder.empty()
            .base(DialogBase.builder(MiniMessage.miniMessage().deserialize(Banco.get().getSettings().get().getDialogs().getTransactionHistory().title()))
                .body(dialogBodyList)
                .build())
            .type(DialogType.notice())
        );

        player.showDialog(dialog);
    }
    
}
