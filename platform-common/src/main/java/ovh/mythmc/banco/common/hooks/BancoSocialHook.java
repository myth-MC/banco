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
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.reactions.Reaction;
import ovh.mythmc.social.api.text.parsers.SocialContextualKeyword;

import java.util.List;

public final class BancoSocialHook {

    public void registerReaction() {
        Reaction reaction = new Reaction(
                "banco",
                "http://textures.minecraft.net/texture/198df42f477f213ff5e9d7fa5a4cc4a69f20d9cef2b90c4ae4f29bd17287b5",
                Sound.sound(Key.key("entity.pig.ambient"), Sound.Source.PLAYER, 0.75f, 1.5f),
                List.of("oink")
        );

        Social.get().getReactionManager().registerReaction("hidden", reaction);
    }

    public void registerKeyword() {
        Social.get().getTextProcessor().registerContextualParser(new BancoKeyword());
    }

    private static class BancoKeyword extends SocialContextualKeyword {

        @Override
        public String keyword() {
            return "balance";
        }

        @Override
        public Component process(SocialParserContext context) {
            Account account = Banco.get().getAccountManager().getByUuid(context.user().getUuid());
            if (account == null)
                return null;

            String playerName = context.user().player().get().getName();
            String formattedAmount = MessageUtil.format(account.amount());
            String currencySymbol = Banco.get().getSettings().get().getCurrency().getSymbol();

            return Component.text(formattedAmount + currencySymbol, NamedTextColor.LIGHT_PURPLE)
                .clickEvent(ClickEvent.runCommand("/balance " + playerName));
        }

    }

}
