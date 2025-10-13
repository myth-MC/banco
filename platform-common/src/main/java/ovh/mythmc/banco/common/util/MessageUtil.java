package ovh.mythmc.banco.common.util;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import lombok.experimental.UtilityClass;
import ovh.mythmc.banco.api.Banco;

import java.math.BigDecimal;
import java.text.DecimalFormat;

@UtilityClass
public class MessageUtil {

    public void info(CommandSender commandSender, String message) {
        if (commandSender instanceof Audience audience) // Paper
            info(audience, message);

        
    }

    public void info(Audience audience, String message) {
        info(audience, Component.translatable(message));
    }

    public void info(Audience audience, Component message) {
        audience.sendMessage(getInfoPrefix()
            .append(message.color(NamedTextColor.WHITE))
        );
    }

    public void warn(Audience audience, String message) {
        warn(audience, Component.translatable(message));
    }

    public void warn(Audience audience, Component message) {
        audience.sendMessage(getWarnPrefix()
            .append(message.color(NamedTextColor.WHITE))
        );
    }

    public void success(Audience audience, String message) {
        success(audience, Component.translatable(message));
    }

    public void success(Audience audience, Component message) {
        audience.sendMessage(getSuccessPrefix()
            .append(message.color(NamedTextColor.WHITE))
        );
    }

    public void error(Audience audience, String message) {
        error(audience, Component.translatable(message));
    }

    public void error(Audience audience, Component message) {
        audience.sendMessage(getErrorPrefix()
            .append(message.color(NamedTextColor.WHITE))
        );
    }

    public void debug(Audience audience, String message) { 
        debug(audience, Component.translatable(message)); 
    }

    public void debug(Audience audience, Component message) {
        audience.sendMessage(getDebugPrefix()
            .append(message.color(NamedTextColor.WHITE))
        );
    }

    public String format(final @NotNull BigDecimal value) {
        DecimalFormat format = new DecimalFormat(Banco.get().getSettings().get().getCurrency().getFormat());
        return format.format(value);
    }

    private Component getPrefix(String configPrefix) {
        Component prefix = Component.empty();
        if (configPrefix != null && !configPrefix.isEmpty())
            prefix = MiniMessage.miniMessage().deserialize(configPrefix + " ");

        return prefix;
    }

    private Component getInfoPrefix() {
        return getPrefix(Banco.get().getSettings().get().getCommands().getInfoPrefix());
    }

    private Component getWarnPrefix() {
        return getPrefix(Banco.get().getSettings().get().getCommands().getWarnPrefix());
    }

    private Component getSuccessPrefix() {
        return getPrefix(Banco.get().getSettings().get().getCommands().getSuccessPrefix());
    }

    private Component getErrorPrefix() {
        return getPrefix(Banco.get().getSettings().get().getCommands().getErrorPrefix());
    }

    private Component getDebugPrefix() {
        return getPrefix("<#2cc83c>\uD83E\uDEB2</#2cc83c>");
    }

}
