package win.codingboulder.advancedmining;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import win.codingboulder.advancedmining.mechanics.BlockDrops;
import win.codingboulder.advancedmining.mechanics.DefaultBlocks;
import win.codingboulder.advancedmining.mechanics.DefaultTools;

import java.util.List;

import static io.papermc.paper.command.brigadier.Commands.literal;
import static io.papermc.paper.command.brigadier.Commands.argument;

@SuppressWarnings("UnstableApiUsage")
public class AdvancedMiningCommand {

    public AdvancedMiningCommand(@NotNull JavaPlugin plugin) {

        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> event.registrar().register(

            literal("advmining")
                .requires(source -> source.getSender().hasPermission("advancedmining.command.admin"))

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

                                                CustomBlock customBlock = new CustomBlock(
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

                                                customBlock.saveToFile();
                                                CustomBlock.loadedBlocks().put(customBlock.id(), customBlock);

                                                return 1;

                                            })

                                            .then(argument("best-tool", StringArgumentType.word())

                                                .executes(context -> {

                                                    Material mat = context.getArgument("material", BlockState.class).getType();
                                                    if (mat.isAir()) {
                                                        context.getSource().getSender().sendRichMessage("<red>Block material cannot be air!");
                                                        return 1;
                                                    }

                                                    CustomBlock customBlock = new CustomBlock(
                                                        StringArgumentType.getString(context, "id"),
                                                        StringArgumentType.getString(context, "name"),
                                                        mat,
                                                        FloatArgumentType.getFloat(context, "strength"),
                                                        IntegerArgumentType.getInteger(context, "hardness"),
                                                        StringArgumentType.getString(context, "best-tool"),
                                                        "",
                                                        "",
                                                        "",
                                                        mat,
                                                        ""
                                                    );

                                                    customBlock.saveToFile();
                                                    CustomBlock.loadedBlocks().put(customBlock.id(), customBlock);

                                                    return 1;

                                                })

                                                .then(argument("texture", StringArgumentType.string())
                                                    .then(argument("break-sound", ArgumentTypes.resourceKey(RegistryKey.SOUND_EVENT))
                                                        .then(argument("place-sound", ArgumentTypes.resourceKey(RegistryKey.SOUND_EVENT))
                                                            .then(argument("icon-material", ArgumentTypes.blockState())
                                                                .then(argument("drops-file", StringArgumentType.word())
                                                                    .executes(context -> {

                                                                        Material mat = context.getArgument("material", BlockState.class).getType();
                                                                        if (mat.isAir()) {
                                                                            context.getSource().getSender().sendRichMessage("<red>Block material cannot be air!");
                                                                            return 1;
                                                                        }

                                                                        CustomBlock customBlock = new CustomBlock(
                                                                            StringArgumentType.getString(context, "id"),
                                                                            StringArgumentType.getString(context, "name"),
                                                                            mat,
                                                                            FloatArgumentType.getFloat(context, "strength"),
                                                                            IntegerArgumentType.getInteger(context, "hardness"),
                                                                            StringArgumentType.getString(context, "best-tool"),
                                                                            StringArgumentType.getString(context, "texture"),
                                                                            context.getArgument("break-sound", TypedKey.class).asString(),
                                                                            context.getArgument("place-sound", TypedKey.class).asString(),
                                                                            context.getArgument("icon-material", BlockState.class).getType(),
                                                                            StringArgumentType.getString(context, "drops-file")
                                                                        );

                                                                        customBlock.saveToFile();
                                                                        CustomBlock.loadedBlocks().put(customBlock.id(), customBlock);

                                                                        return 1;

                                                                    }))))))
                                            )

                                        )))))
                    )

                    .then(literal("edit")
                        .then(argument("block", CustomBlockArgument.blockArgument())

                            .then(literal("drops-file")
                                .then(argument("file", StringArgumentType.word())
                                    .suggests((context, builder) -> {
                                        BlockDrops.loadedDrops().keySet().forEach(builder::suggest);
                                        return builder.buildFuture();
                                    })
                                    .executes(context -> {

                                        CustomBlock block = context.getArgument("block", CustomBlock.class);
                                        block.editAndSave(b -> b.setDropsFile(context.getArgument("file", String.class)));

                                        return 1;

                                    }))
                            )

                            .then(literal("best-tool")
                                .then(argument("tool", StringArgumentType.word())
                                    .executes(context -> {

                                        CustomBlock block = context.getArgument("block", CustomBlock.class);
                                        block.editAndSave(b -> b.setBestTool(context.getArgument("tool", String.class)));

                                        return 1;

                                    }))
                            )

                            .then(literal("texture")
                                .then(argument("texture", ArgumentTypes.key())
                                    .executes(context -> {

                                        CustomBlock block = context.getArgument("block", CustomBlock.class);
                                        block.editAndSave(b -> b.setTexture(context.getArgument("texture", Key.class)));

                                        return 1;

                                    }))
                            )

                            .then(literal("icon-material")
                                .then(argument("material", ArgumentTypes.blockState())
                                    .executes(context -> {

                                        CustomBlock block = context.getArgument("block", CustomBlock.class);
                                        block.editAndSave(b -> b.setIconMaterial(context.getArgument("material", BlockState.class).getType()));

                                        return 1;

                                    }))
                            )

                            .then(literal("break-sound")
                                .then(argument("sound", ArgumentTypes.resourceKey(RegistryKey.SOUND_EVENT))
                                    .executes(context -> {

                                        CustomBlock block = context.getArgument("block", CustomBlock.class);
                                        block.editAndSave(b -> b.setBreakSound(context.getArgument("sound", TypedKey.class)));

                                        return 1;

                                    }))
                            )

                            .then(literal("place-sound")
                                .then(argument("sound", ArgumentTypes.resourceKey(RegistryKey.SOUND_EVENT))
                                    .executes(context -> {

                                        CustomBlock block = context.getArgument("block", CustomBlock.class);
                                        block.editAndSave(b -> b.setPlaceSound(context.getArgument("sound", TypedKey.class)));

                                        return 1;

                                    }))
                            )

                            .then(literal("add-drop-itself")
                                .executes(context -> {

                                    CustomBlock customBlock = context.getArgument("block", CustomBlock.class);

                                    ItemStack item = ItemStack.of(customBlock.iconMaterial());
                                    item.setData(DataComponentTypes.ITEM_NAME, customBlock.name());
                                    item.editPersistentDataContainer(pdc -> pdc.set(AdvancedMining.PLACED_BLOCK_KEY, PersistentDataType.STRING, customBlock.id()));

                                    String dropsFile = customBlock.rawDropsFile();
                                    if (dropsFile == null || dropsFile.isEmpty()) {

                                        BlockDrops blockDrops = new BlockDrops(customBlock.id());
                                        blockDrops.entries().add(new BlockDrops.Entry(item, 1, 1, 1));
                                        BlockDrops.loadedDrops().put(customBlock.id(), blockDrops);
                                        blockDrops.saveToFile();
                                        customBlock.setDropsFile(customBlock.id());
                                        customBlock.saveToFile();

                                    } else {

                                        BlockDrops blockDrops = BlockDrops.loadedDrops().get(dropsFile);
                                        if (blockDrops == null) {

                                            BlockDrops newDrops = new BlockDrops(customBlock.rawDropsFile());
                                            newDrops.entries().add(new BlockDrops.Entry(item, 1, 1, 1));
                                            BlockDrops.loadedDrops().put(customBlock.id(), newDrops);
                                            newDrops.saveToFile();

                                        } else {

                                            blockDrops.entries().add(new BlockDrops.Entry(item, 1, 1, 1));
                                            blockDrops.saveToFile();

                                        }

                                    }

                                    context.getSource().getSender().sendRichMessage("<green>Drop added!");

                                    return 1;

                                }))

                        )
                    )

                    .then(literal("place")
                        .then(argument("block", StringArgumentType.word())
                            .suggests((context, builder) -> {
                                CustomBlock.loadedBlocks().keySet().forEach(builder::suggest);
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

                    .then(literal("give")
                        .then(argument("block", StringArgumentType.word())
                            .suggests((context, builder) -> {
                                CustomBlock.loadedBlocks().keySet().forEach(builder::suggest);
                                return builder.buildFuture();
                            })
                            .executes(context -> {

                                String blockId = StringArgumentType.getString(context, "block");
                                if (!(context.getSource().getSender() instanceof Player player)) return 1;

                                CustomBlock customBlock = CustomBlock.loadedBlocks().get(blockId);
                                Material material = customBlock == null ? Material.STONE : customBlock.iconMaterial();
                                Component name = customBlock == null ? Component.text(blockId) : customBlock.name();

                                ItemStack item = ItemStack.of(material);
                                item.setData(DataComponentTypes.ITEM_NAME, name);
                                item.editPersistentDataContainer(pdc -> pdc.set(AdvancedMining.PLACED_BLOCK_KEY, PersistentDataType.STRING, blockId));

                                player.give(item);

                                return 1;

                            }))
                    )

                    .then(literal("set-default")
                        .then(argument("material", ArgumentTypes.blockState())
                            .then(argument("block", CustomBlockArgument.blockArgument())
                                .executes(context -> {

                                    DefaultBlocks.defaultBlocks().put(
                                        context.getArgument("material", BlockState.class).getType(),
                                        context.getArgument("block", CustomBlock.class).id()
                                    );
                                    DefaultBlocks.saveToFile();

                                    return 1;

                                }))))

                )

                .then(literal("tool")
                    .then(literal("hand")
                        .requires(source -> source.getSender() instanceof Player)
                        .then(argument("mining-speed", FloatArgumentType.floatArg(0))
                            .then(argument("breaking-power", IntegerArgumentType.integer(0))
                                .then(argument("tool-type", StringArgumentType.word())
                                    .executes(context -> {

                                        Player player = (Player) context.getSource().getSender();

                                        ItemStack item = player.getInventory().getItemInMainHand();
                                        item.editPersistentDataContainer(pdc -> {
                                            pdc.set(AdvancedMining.MINING_SPEED_KEY, PersistentDataType.FLOAT, FloatArgumentType.getFloat(context, "mining-speed"));
                                            pdc.set(AdvancedMining.BREAKING_POWER_KEY, PersistentDataType.INTEGER, IntegerArgumentType.getInteger(context, "breaking-power"));
                                            pdc.set(AdvancedMining.TOOL_TYPE_KEY, PersistentDataType.STRING, StringArgumentType.getString(context, "tool-type"));
                                        });

                                        player.sendRichMessage("<green>Tool set!");

                                        return 1;

                                    }))))
                    )
                    .then(literal("give")
                        .then(argument("player", ArgumentTypes.players())
                            .then(argument("item", ArgumentTypes.itemStack())
                                .then(argument("mining-speed", FloatArgumentType.floatArg(0))
                                    .then(argument("mining-power", IntegerArgumentType.integer(0))
                                        .then(argument("tool-type", StringArgumentType.word())
                                            .executes(context -> {

                                                List<Player> players = context.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(context.getSource());

                                                ItemStack item = context.getArgument("item", ItemStack.class);
                                                item.editPersistentDataContainer(pdc -> {
                                                    pdc.set(AdvancedMining.MINING_SPEED_KEY, PersistentDataType.FLOAT, FloatArgumentType.getFloat(context, "mining-speed"));
                                                    pdc.set(AdvancedMining.BREAKING_POWER_KEY, PersistentDataType.INTEGER, IntegerArgumentType.getInteger(context, "breaking-power"));
                                                    pdc.set(AdvancedMining.TOOL_TYPE_KEY, PersistentDataType.STRING, StringArgumentType.getString(context, "tool-wype"));
                                                });

                                                players.forEach(player -> player.give(item));

                                                return 1;

                                            }))))))
                    )

                    .then(literal("set-default")
                        .then(argument("item", ArgumentTypes.itemStack())
                            .then(argument("mining-speed", FloatArgumentType.floatArg(0))
                                .then(argument("breaking-power", IntegerArgumentType.integer(0))

                                    .executes(context -> {

                                        DefaultTools.addDefaultTool(
                                            context.getArgument("item", ItemStack.class).getType(),
                                            FloatArgumentType.getFloat(context, "mining-speed"),
                                            IntegerArgumentType.getInteger(context, "breaking-power"),
                                            ""
                                        );
                                        DefaultTools.saveToFile();

                                        return 1;

                                    })

                                    .then(argument("tool-type", StringArgumentType.word())
                                        .executes(context -> {

                                            DefaultTools.addDefaultTool(
                                                context.getArgument("item", ItemStack.class).getType(),
                                                FloatArgumentType.getFloat(context, "mining-speed"),
                                                IntegerArgumentType.getInteger(context, "breaking-power"),
                                                StringArgumentType.getString(context, "tool-type")
                                            );
                                            DefaultTools.saveToFile();

                                            return 1;

                                        })))))

                    )

                )

                .then(literal("drops")
                    .then(literal("create")
                        .then(argument("name", StringArgumentType.word())
                            .executes(context -> {

                                BlockDrops blockDrops = new BlockDrops(StringArgumentType.getString(context, "name"));
                                BlockDrops.loadedDrops().put(blockDrops.id(), blockDrops);
                                blockDrops.saveToFile();

                                return 1;

                            }))
                    )
                    .then(literal("edit")
                        .then(argument("name", StringArgumentType.word())
                            .suggests((context, builder) -> {
                                BlockDrops.loadedDrops().keySet().forEach(builder::suggest);
                                return builder.buildFuture();
                            })

                            .then(literal("add-entry")
                                .then(argument("chance", FloatArgumentType.floatArg(0f, 1f))
                                    .then(argument("item", ArgumentTypes.itemStack())
                                        .then(argument("min-amount", IntegerArgumentType.integer(1))
                                            .then(argument("max-amount", IntegerArgumentType.integer(1))
                                                .executes(context -> {

                                                    BlockDrops blockDrops = BlockDrops.loadedDrops().get(StringArgumentType.getString(context, "name"));
                                                    if (blockDrops == null) {
                                                        context.getSource().getSender().sendRichMessage("<red>That block drops config doesn't exist");
                                                        return 1;
                                                    }

                                                    blockDrops.entries().add(new BlockDrops.Entry(
                                                        context.getArgument("item", ItemStack.class),
                                                        IntegerArgumentType.getInteger(context, "min-amount"),
                                                        IntegerArgumentType.getInteger(context, "max-amount"),
                                                        FloatArgumentType.getFloat(context, "chance")
                                                    ));

                                                    blockDrops.saveToFile();

                                                    return 1;

                                                }))))
                                    .then(literal("hand")
                                        .requires(source -> source.getSender() instanceof Player)
                                        .then(argument("min-amount", IntegerArgumentType.integer(1))
                                            .then(argument("max-amount", IntegerArgumentType.integer(1))
                                                .executes(context -> {

                                                    BlockDrops blockDrops = BlockDrops.loadedDrops().get(StringArgumentType.getString(context, "name"));
                                                    if (blockDrops == null) {
                                                        context.getSource().getSender().sendRichMessage("<red>That block drops config doesn't exist");
                                                        return 1;
                                                    }

                                                    Player player = (Player) context.getSource().getSender();
                                                    ItemStack itemStack = player.getInventory().getItemInMainHand();
                                                    if (itemStack.isEmpty()) return 1;

                                                    blockDrops.entries().add(new BlockDrops.Entry(
                                                        itemStack,
                                                        IntegerArgumentType.getInteger(context, "min-amount"),
                                                        IntegerArgumentType.getInteger(context, "max-amount"),
                                                        FloatArgumentType.getFloat(context, "chance")
                                                    ));

                                                    blockDrops.saveToFile();

                                                    return 1;

                                                })))))
                            )))
                )

                .then(literal("reload")
                    .executes(context -> {

                        AdvancedMining.getInstance().loadConfig();
                        context.getSource().getSender().sendRichMessage("<green>Config reloaded!");
                        return 1;

                    }))

                .build()

        ));

    }

}
