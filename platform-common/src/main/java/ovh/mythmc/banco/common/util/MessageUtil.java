package ovh.mythmc.banco.common.util;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;

import lombok.experimental.UtilityClass;
import ovh.mythmc.banco.api.Banco;

@UtilityClass
public class MessageUtil {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    public void info(Audience audience, String message, Component... placeholders) {
        info(audience, Component.translatable(message, placeholders));
    }

    public void info(Audience audience, String message) {
        info(audience, Component.translatable(message));
    }

    public void info(Audience audience, Component message) {
        audience.sendMessage(getInfoPrefix()
            .append(message.color(NamedTextColor.WHITE))
        );
    }

    public void warn(Audience audience, String message, Component... placeholders) {
        warn(audience, Component.translatable(message, placeholders));
    }

    public void warn(Audience audience, String message) {
        warn(audience, Component.translatable(message));
    }

    public void warn(Audience audience, Component message) {
        audience.sendMessage(getWarnPrefix()
            .append(message.color(NamedTextColor.WHITE))
        );
    }

    public void success(Audience audience, String message, Component... placeholders) {
        success(audience, Component.translatable(message, placeholders));
    }

    public void success(Audience audience, String message) {
        success(audience, Component.translatable(message));
    }

    public void success(Audience audience, Component message) {
        audience.sendMessage(getSuccessPrefix()
            .append(message.color(NamedTextColor.WHITE))
        );
    }

    public void error(Audience audience, String message, Component... placeholders) {
        error(audience, Component.translatable(message, placeholders));
    }

    public void error(Audience audience, String message) {
        error(audience, Component.translatable(message));
    }

    public void error(Audience audience, Component message) {
        audience.sendMessage(getErrorPrefix()
            .append(message.color(NamedTextColor.WHITE))
        );
    }

    public void debug(Audience audience, String message, Component... placeholders) { 
        debug(audience, Component.translatable(message, placeholders)); 
    }

    public void debug(Audience audience, String message) { 
        debug(audience, Component.translatable(message)); 
    }

    public void debug(Audience audience, Component message) {
        audience.sendMessage(getDebugPrefix()
            .append(message.color(NamedTextColor.WHITE))
        );
    }

    private static Component getPrefix(String configPrefix) {
        Component prefix = Component.empty();
        if (configPrefix != null && !configPrefix.isEmpty())
            prefix = MINI_MESSAGE.deserialize(configPrefix + " ");

        return prefix;
    }

    public static Component getInfoPrefix() {
        return getPrefix(Banco.get().getSettings().get().getCommands().getInfoPrefix());
    }

    public static Component getWarnPrefix() {
        return getPrefix(Banco.get().getSettings().get().getCommands().getWarnPrefix());
    }

    public static Component getSuccessPrefix() {
        return getPrefix(Banco.get().getSettings().get().getCommands().getSuccessPrefix());
    }

    public static Component getErrorPrefix() {
        return getPrefix(Banco.get().getSettings().get().getCommands().getErrorPrefix());
    }

    public static Component getDebugPrefix() {
        return getPrefix("<#2cc83c>\uD83E\uDEB2</#2cc83c>");
    }

}
