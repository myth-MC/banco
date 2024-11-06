package ovh.mythmc.banco.common.hooks;

import org.bukkit.Sound;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.accounts.Account;
import ovh.mythmc.banco.common.util.MessageUtil;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.api.reactions.Reaction;
import ovh.mythmc.social.api.text.keywords.SocialKeyword;

import java.util.List;

public final class BancoSocialHook {

    public void registerReaction() {
        Reaction reaction = new Reaction("banco",
                "http://textures.minecraft.net/texture/198df42f477f213ff5e9d7fa5a4cc4a69f20d9cef2b90c4ae4f29bd17287b5",
                Sound.ENTITY_PIG_AMBIENT,
                List.of("oink")
        );

        Social.get().getReactionManager().registerReaction("hidden", reaction);
    }

    public void registerKeyword() {
        Social.get().getTextProcessor().registerParser(new SocialKeyword() {
            @Override
            public String keyword() {
                return "balance";
            }

            @Override
            public String process(SocialPlayer socialPlayer) {
                Account account = Banco.get().getAccountManager().get(socialPlayer.getUuid());
                if (account == null)
                    return null;

                String playerName = socialPlayer.getPlayer().getName();
                String formattedAmount = MessageUtil.format(account.amount());
                String currencySymbol = Banco.get().getSettings().get().getCurrency().getSymbol();

                return "<light_purple><click:run_command:/balance " + playerName + ">" + formattedAmount + currencySymbol + "</click></light_purple>";
            }
        });
    }

}
