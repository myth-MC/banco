package ovh.mythmc.banco.common.hooks;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.Account;
import ovh.mythmc.banco.common.util.MessageUtil;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.reaction.Reaction;
import ovh.mythmc.social.api.util.registry.NamespacedRegistryKey;
import ovh.mythmc.social.api.util.registry.RegistryKey;

public final class BancoSocialHook {

    public void registerReaction() {
        String name = "banco";
        String textureUrl = "http://textures.minecraft.net/texture/198df42f477f213ff5e9d7fa5a4cc4a69f20d9cef2b90c4ae4f29bd17287b5";

        NamespacedRegistryKey key = RegistryKey.namespaced("banco", name);
        Reaction reaction = Reaction.builder(name, textureUrl)
            .sound(Sound.sound(Key.key("entity.pig.ambient"), Sound.Source.PLAYER, 0.75f, 1.5f))
            .particle(textureUrl)
            .triggerWords("oink")
            .build();

        Social.registries().reactions().register(key, reaction);
    }

    public void registerKeyword() {
        Social.get().getTextProcessor().registerContextualKeyword("balance", context -> {
            Account account = Banco.get().getAccountManager().getByUuid(context.user().uuid());
            if (account == null)
                return Component.empty();

            String playerName = context.user().username();
            String formattedAmount = MessageUtil.format(account.balance());
            String currencySymbol = Banco.get().getSettings().get().getCurrency().getSymbol();

            return Component.text(formattedAmount + currencySymbol, NamedTextColor.LIGHT_PURPLE)
                .clickEvent(ClickEvent.runCommand("/banco:balance " + playerName));
        });
    }

}
