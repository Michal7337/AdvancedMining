package win.codingboulder.advancedmining;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver;
import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import static io.papermc.paper.command.brigadier.Commands.literal;
import static io.papermc.paper.command.brigadier.Commands.argument;

@SuppressWarnings("UnstableApiUsage")
public class AdvancedMiningCommand {

    public AdvancedMiningCommand(@NotNull JavaPlugin plugin) {

        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> event.registrar().register(

            literal("advmining")

                .then(literal("block")

                    .then(literal("create")
                        .then(argument("id", StringArgumentType.word())
                            .then(argument("name", StringArgumentType.string())
                                .then(argument("material", ArgumentTypes.blockState())
                                    .then(argument("strength", FloatArgumentType.floatArg(0))
                                        .then(argument("hardness", IntegerArgumentType.integer(0))
                                            .executes(context -> {

                                                Material mat = context.getArgument("material", BlockState.class).getType();
                                                if (mat.isAir()) {
                                                    context.getSource().getSender().sendRichMessage("<red>Block material cannot be air!");
                                                    return 1;
                                                }

                                                CustomBlockInfo blockInfo = new CustomBlockInfo(
                                                    StringArgumentType.getString(context, "id"),
                                                    StringArgumentType.getString(context, "name"),
                                                    mat,
                                                    FloatArgumentType.getFloat(context, "strength"),
                                                    IntegerArgumentType.getInteger(context, "hardness"),
                                                    "",
                                                    "",
                                                    "",
                                                    "",
                                                    mat,
                                                    ""
                                                );

                                                blockInfo.saveToFile();
                                                CustomBlockInfo.loadedBlocks.add(blockInfo);
                                                CustomBlock.loadedBlocks.put(blockInfo.id(), CustomBlock.constructFromInfo(blockInfo));

                                                return 1;

                                            })

                                        )))))
                    )

                    .then(literal("place")
                        .then(argument("block", StringArgumentType.word())
                            .suggests((context, builder) -> {
                                CustomBlock.loadedBlocks.keySet().forEach(builder::suggest);
                                return builder.buildFuture();
                            })
                            .then(argument("position", ArgumentTypes.blockPosition())
                                .executes(context -> {

                                    if (!(context.getSource().getSender() instanceof Player player)) {
                                        context.getSource().getSender().sendRichMessage("<red>You must specify a world!");
                                        return 1;
                                    }

                                    BlockPosition pos = context.getArgument("position", BlockPositionResolver.class).resolve(context.getSource());
                                    String blockId = StringArgumentType.getString(context, "block");
                                    Block block = pos.toLocation(player.getWorld()).getBlock();

                                    CustomBlock.setCustomBlock(block, blockId);

                                    return 1;

                                })))
                    )

                )

                .build()

        ));

    }

}
