package ovh.mythmc.banco.common.command.exception;

import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.exception.parsing.ParserException;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.banco.common.command.BancoCaptionKeys;
import ovh.mythmc.banco.common.command.parser.AccountParser;

public final class UnknownAccountException extends ParserException {
    
    private final String input;

    public UnknownAccountException(@NotNull String input, @NotNull AccountParser parser, @NotNull CommandContext<?> context) {
        super(
            parser.getClass(),
            context,
            BancoCaptionKeys.ARGUMENT_PARSE_FAILURE_ACCOUNT,
            CaptionVariable.of("input", input)
        );
        this.input = input;
    }

    public String input() {
        return this.input;
    }

}
