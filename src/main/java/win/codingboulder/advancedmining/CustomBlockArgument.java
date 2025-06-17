package win.codingboulder.advancedmining;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;


@SuppressWarnings("UnstableApiUsage")
public class CustomBlockArgument implements CustomArgumentType.Converted<CustomBlock, String> {

    @Override
    public @NotNull CustomBlock convert(@NotNull String nativeType) throws CommandSyntaxException {

        CustomBlock block = CustomBlock.loadedBlocks.get(nativeType);
        if (block == null) throw new DynamicCommandExceptionType(name -> new LiteralMessage("The custom block '" + name + "' doesn't exist!")).create(nativeType);
        return block;

    }

    @Override
    public <S> @NotNull CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {

        CustomBlock.loadedBlocks.keySet().forEach(builder::suggest);
        return builder.buildFuture();

    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    public static CustomBlockArgument blockArgument() {
        return new CustomBlockArgument();
    }

}
