package win.codingboulder.advancedmining.util;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import win.codingboulder.advancedmining.mechanics.BlockDrops;

import java.util.concurrent.CompletableFuture;

public class BlockDropsArgument implements CustomArgumentType.Converted<BlockDrops, String> {

    @Override
    public BlockDrops convert(String nativeType) throws CommandSyntaxException {

        BlockDrops blockDrops = BlockDrops.loadedDrops().get(nativeType);
        if (blockDrops == null) throw new DynamicCommandExceptionType(name -> new LiteralMessage("The Block Drops configuration '" + name + "' doesn't exist!")).create(nativeType);
        return blockDrops;

    }

    @Override
    public <S> @NonNull CompletableFuture<Suggestions> listSuggestions(@NonNull CommandContext<S> context, @NonNull SuggestionsBuilder builder) {

        BlockDrops.loadedDrops().keySet().forEach(builder::suggest);
        return builder.buildFuture();

    }

    @Override
    public @NonNull ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    public static @NonNull BlockDropsArgument dropsArgument() {
        return new BlockDropsArgument();
    }

    public static BlockDrops getBlockDrops(@NonNull CommandContext<?> context, String name) {

        return context.getArgument(name, BlockDrops.class);

    }

}
