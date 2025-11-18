package ovh.mythmc.banco.paper.dialog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.Account;
import ovh.mythmc.banco.api.accounts.Transaction;
import ovh.mythmc.banco.api.accounts.Transaction.Operation;
import ovh.mythmc.banco.api.items.BancoItem;
import ovh.mythmc.banco.api.util.ItemUtil;

public final class BalanceConvertDialog {

    public void open(@NotNull Player player) {
        player.showDialog(getMainMenu(player));
    }

    private static Dialog getMainMenu(@NotNull Player player) {
        final List<DialogBody> dialogBodyList = new ArrayList<>();
        final String description = Banco.get().getSettings().get().getMenus().getBalanceConvert().description();
        final String itemButton = Banco.get().getSettings().get().getMenus().getBalanceConvert().itemButton();
        final String compactButton = Banco.get().getSettings().get().getMenus().getBalanceConvert().compactButton();

        dialogBodyList.add(DialogBody.plainMessage(MiniMessage.miniMessage().deserialize(description)));

        final List<ActionButton> actionButtonList = new ArrayList<>();

        actionButtonList.add(ActionButton.builder(MiniMessage.miniMessage().deserialize(compactButton))
            .action(DialogAction.staticAction(ClickEvent.showDialog(getCompactConfirmation(player))))
            .build()
        );

        for (BancoItem bancoItem : Banco.get().getItemRegistry().get()) {
            if (!showConverter(player, bancoItem))
                continue;

            actionButtonList.add(ActionButton.builder(MiniMessage.miniMessage().deserialize(itemButton, Placeholder.component("item", bancoItem.displayName())))
                .action(DialogAction.staticAction(ClickEvent.showDialog(getSpecificConverter(player, bancoItem))))
                .build());
        }

        final Dialog dialog = Dialog.create(builder -> builder.empty()
            .base(DialogBase.builder(MiniMessage.miniMessage().deserialize(Banco.get().getSettings().get().getMenus().getTransactionHistory().title()))
                .body(dialogBodyList)
                .build())
            .type(DialogType.multiAction(actionButtonList)
                .columns(1)
                .build())
        );

        return dialog;
    }

    private static boolean showConverter(@NotNull Player player, @NotNull BancoItem item) {
        final Account account = Banco.get().getAccountManager().getByUuid(player.getUniqueId());
        final int maxAmount = ItemUtil.getMaxUnitsFromValue(item, account.amount());

        return maxAmount > 0;
    }

    private static Dialog getSpecificConverter(@NotNull Player player, @NotNull BancoItem item) {
        final Account account = Banco.get().getAccountManager().getByUuid(player.getUniqueId());
        final int maxAmount = Math.min(ItemUtil.getMaxUnitsFromValue(item, account.amount()), Banco.get().getSettings().get().getCommands().getBalanceConvert().maxConvertibleAmount());

        final String description = Banco.get().getSettings().get().getMenus().getBalanceConvert().itemDescription();

        return Dialog.create(builder -> builder.empty()
            .base(DialogBase.builder(Component.text("Convert to ").append(item.displayName()))
                .body(List.of(
                    DialogBody.item(item.asItemStack()).build(),
                    DialogBody.plainMessage(
                        MiniMessage.miniMessage().deserialize(description, Placeholder.component("item", item.displayName()))
                    )
                ))
                .inputs(List.of(
                    DialogInput.numberRange("amount", Component.text("Amount"), 1f, (float) maxAmount)
                        .step(1f)
                        .initial(Float.valueOf(1))
                        .build()
                ))
                .build())
            .type(DialogType.confirmation(
                ActionButton.builder(Component.translatable("gui.proceed"))
                    .action(DialogAction.customClick(
                        (view, audience) -> {
                            final int amount = view.getFloat("amount").intValue();
                            final BigDecimal value = item.value(amount);

                            final List<ItemStack> itemList = new ArrayList<>();
                            for (int i = 0; i < amount; i++)
                                itemList.add(item.asItemStack());

                            final var transaction = Transaction.builder()
                                .account(account)
                                .amount(value)
                                .operation(Operation.WITHDRAW)
                                .executeAfterTransaction(List.of(
                                    () -> {
                                        final HashMap<Integer, ItemStack> dropItems = player.getInventory().addItem(itemList.toArray(new ItemStack[itemList.size()]));
                                        dropItems.entrySet().stream().map(Map.Entry::getValue).forEach(player::dropItem);
                                    }
                                ))
                                .build();

                            transaction.queue();

                            player.playSound(Sound.sound(Key.key("item.armor.equip_chain"), Sound.Source.UI, 0.5f, 1.25f));
                        }, 
                        ClickCallback.Options.builder()
                            .uses(1)
                            .lifetime(ClickCallback.DEFAULT_LIFETIME)
                            .build()
                        ))
                    .build(),
                ActionButton.builder(Component.translatable("gui.back"))
                    .action(DialogAction.staticAction(ClickEvent.callback(audience -> player.showDialog(getMainMenu(player)))))
                    .build()))
        );
    }

    private static Dialog getCompactConfirmation(@NotNull Player player) {
        final String compactTitle = Banco.get().getSettings().get().getMenus().getBalanceConvert().compactTitle();

        return Dialog.create(builder -> builder.empty()
            .base(DialogBase.builder(MiniMessage.miniMessage().deserialize(compactTitle))
                .body(getCompactResult(player))
                .build()
            )
            .type(DialogType.confirmation(
                ActionButton.builder(Component.translatable("gui.proceed"))
                    .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                        final Account account = Banco.get().getAccountManager().getByUuid(player.getUniqueId());
                        final BigDecimal amount = account.amount();

                        Banco.get().getAccountManager().withdraw(account, amount);
                        Banco.get().getAccountManager().deposit(account, amount);
                    })))
                    .build(), 
                    ActionButton.builder(Component.translatable("gui.back"))
                        .action(DialogAction.staticAction(ClickEvent.callback(audience -> player.showDialog(getMainMenu(player)))))
                        .build())));
    }

    private static List<DialogBody> getCompactResult(@NotNull Player player) {
        final String compactWarning = Banco.get().getSettings().get().getMenus().getBalanceConvert().compactWarning();

        final Account account = Banco.get().getAccountManager().getByUuid(player.getUniqueId());
        final List<ItemStack> itemStackList = ItemUtil.convertAmountToItems(account.amount());
        final List<DialogBody> dialogBodyList = new ArrayList<>(List.of(
            DialogBody.plainMessage(MiniMessage.miniMessage().deserialize(compactWarning))
        ));

        itemStackList.forEach(itemStack -> dialogBodyList.add(DialogBody.item(itemStack).build()));
        return dialogBodyList;
    }

}
