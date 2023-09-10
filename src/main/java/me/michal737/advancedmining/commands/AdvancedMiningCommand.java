package me.michal737.advancedmining.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.*;
import me.michal737.advancedmining.AdvancedMining;
import me.michal737.advancedmining.CustomBlock;
import me.michal737.advancedmining.CustomBlockManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

@SuppressWarnings({"DataFlowIssue", "unused"})
public class AdvancedMiningCommand {

    public AdvancedMiningCommand(){

        new CommandTree("advancedmining")
                .withAliases("am")
                .withPermission("advancedmining.admin")
                .withHelp("The main ImprovedMining command", "The main command in the plugin. It's used for doing most things in the plugin.")

                .then(new LiteralArgument("block").withPermission("advancedmining.admin.block")
                        .then(new LiteralArgument("create").withPermission("advancedmining.admin.block.create")
                                .then(new StringArgument("name")
                                        .then(new TextArgument("display_name")
                                                .then(new IntegerArgument("strength")
                                                        .then(new IntegerArgument("resistance")
                                                                .then(new ItemStackArgument("material")
                                                                        .executes((sender, args) -> {

                                                                    String name = (String) args.get("name");
                                                                    String displayName = (String) args.get("display_name");
                                                                    int strength = (int) args.get("strength");
                                                                    int resistance = (int) args.get("resistance");
                                                                    ItemStack itemStack = (ItemStack) args.get("material");
                                                                    Material material = itemStack.getType();

                                                                    if (!material.isBlock()) {sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>This material is not a block!")); return;}

                                                                    CustomBlock customBlock = new CustomBlock(name, displayName, strength, resistance, material, CustomBlock.BreakType.BREAK, List.of(), 0, null);

                                                                    CustomBlockManager.storeBlock(customBlock);

                                                                    })))))))
                        .then(new LiteralArgument("delete").withPermission("advancedmining.admin.block.delete")
                                .then(new StringArgument("name").replaceSuggestions(CustomBlockManager.getAllBlocksArgumentSuggestions()))
                                        .executes((sender, args) -> {

                                            CustomBlockManager.deleteCustomBlock((String) args.get("name"));

                                        }))
                        .then(new LiteralArgument("edit").withPermission("advancedmining.admin.block.edit")
                                .then(new StringArgument("block").replaceSuggestions(CustomBlockManager.getAllBlocksArgumentSuggestions())
                                        .then(new LiteralArgument("name")
                                                .then(new StringArgument("new_name")
                                                        .executes((sender, args) -> {

                                                            String block = (String) args.get("block");
                                                            String newName = (String) args.get("new_name");

                                                            CustomBlock oldBlock = CustomBlockManager.getBlock(block);
                                                            oldBlock.setName(newName);
                                                            CustomBlockManager.storeBlock(oldBlock);
                                                            CustomBlockManager.deleteCustomBlock(block);

                                                            sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Block \"" + block + "\" was successfully renamed to \"" + newName + "\"!"));

                                                        })))
                                        .then(new LiteralArgument("strength")
                                                .then(new IntegerArgument("new_value")
                                                        .executes((sender, args) -> {

                                                            String blockName = (String) args.get("block");
                                                            int new_value = (int) args.get("new_value");

                                                            CustomBlock block = CustomBlockManager.getBlock(blockName);
                                                            block.setStrength(new_value);
                                                            CustomBlockManager.storeBlock(block);

                                                        })))
                                        .then(new LiteralArgument("resistance")
                                                .then(new IntegerArgument("new_value")
                                                        .executes((sender, args) -> {

                                                            String blockName = (String) args.get("block");
                                                            int new_value = (int) args.get("new_value");

                                                            CustomBlock block = CustomBlockManager.getBlock(blockName);
                                                            block.setResistance(new_value);
                                                            CustomBlockManager.storeBlock(block);

                                                        })))
                                        .then(new LiteralArgument("material")
                                                .then(new BlockStateArgument("new_value")
                                                        .executes((sender, args) -> {

                                                            String blockName = (String) args.get("block");
                                                            BlockData blockData = (BlockData) args.get("new_value");
                                                            Material new_value = blockData.getMaterial();

                                                            CustomBlock block = CustomBlockManager.getBlock(blockName);
                                                            block.setMaterial(new_value);
                                                            CustomBlockManager.storeBlock(block);

                                                        })))
                                        .then(new LiteralArgument("breakType")
                                                .then(new LiteralArgument("break")
                                                        .executes((sender, args) -> {

                                                            String blockName = (String) args.get("block");
                                                            CustomBlock block = CustomBlockManager.getBlock(blockName);
                                                            block.setBreakType(CustomBlock.BreakType.BREAK);
                                                            CustomBlockManager.storeBlock(block);

                                                        }))
                                                .then(new LiteralArgument("break_temporarily")
                                                        .then(new TimeArgument("time")
                                                                .executes((sender, args) -> {

                                                                    String blockName = (String) args.get("block");
                                                                    int time = (int) args.get("time");
                                                                    CustomBlock block = CustomBlockManager.getBlock(blockName);
                                                                    block.setBreakType(CustomBlock.BreakType.BREAK_TEMPORARILY);
                                                                    block.setTime(time);
                                                                    CustomBlockManager.storeBlock(block);

                                                                })))
                                                .then(new LiteralArgument("replace")
                                                        .then(new StringArgument("replacement").replaceSuggestions(ArgumentSuggestions.strings(CustomBlockManager.getCustomBlockNames()))
                                                                .executes((sender, args) -> {

                                                                    CustomBlock block = CustomBlockManager.getBlock((String) args.get("block"));
                                                                    block.setBreakType(CustomBlock.BreakType.REPLACE);
                                                                    block.setReplacement((String) args.get("replacement"));
                                                                    CustomBlockManager.storeBlock(block);

                                                                })))
                                                .then(new LiteralArgument("replace_vanilla")
                                                        .then(new BlockStateArgument("replacement")
                                                                .executes((sender, args) -> {

                                                                    CustomBlock block = CustomBlockManager.getBlock((String) args.get("block"));
                                                                    BlockData blockData = (BlockData) args.get("replacement");
                                                                    block.setReplacement(blockData.getMaterial().getKey().asString());
                                                                    block.setBreakType(CustomBlock.BreakType.REPLACE_VANILLA);
                                                                    CustomBlockManager.storeBlock(block);

                                                                })))
                                                .then(new LiteralArgument("replace_temporarily")
                                                        .then(new StringArgument("replacement").replaceSuggestions(ArgumentSuggestions.strings(CustomBlockManager.getCustomBlockNames()))
                                                                .then(new TimeArgument("time")
                                                                        .executes((sender, args) -> {

                                                                            CustomBlock block = CustomBlockManager.getBlock((String) args.get("block"));
                                                                            int time = (int) args.get("time");
                                                                            block.setReplacement((String) args.get("replacement"));
                                                                            block.setTime(time);
                                                                            block.setBreakType(CustomBlock.BreakType.REPLACE_TEMPORARILY);
                                                                            CustomBlockManager.storeBlock(block);

                                                                        }))))
                                                .then(new LiteralArgument("replace_temporarily_vanilla")
                                                        .then(new BlockStateArgument("replacement")
                                                                .then(new TimeArgument("time")
                                                                        .executes((sender, args) -> {

                                                                            CustomBlock block = CustomBlockManager.getBlock((String) args.get("block"));
                                                                            int time = (int) args.get("time");
                                                                            BlockData blockData = (BlockData) args.get("replacement");
                                                                            block.setReplacement(blockData.getMaterial().getKey().asString());
                                                                            block.setTime(time);
                                                                            block.setBreakType(CustomBlock.BreakType.REPLACE_TEMPORARILY_VANILLA);
                                                                            CustomBlockManager.storeBlock(block);

                                                                        })))))
                                ))
                        .then(new LiteralArgument("set")
                                .then(new StringArgument("block")
                                        .then(new LocationArgument("location")
                                                .executes((sender, args) -> {

                                                    Location location = (Location) args.get("location");
                                                    String blockName = (String) args.get("block");
                                                    CustomBlockManager.setBlock(blockName, location.getBlock());

                                                }))))
                        .then(new LiteralArgument("give")
                            .then(new StringArgument("block")
                                            .executesPlayer((sender, args) -> {

                                                CustomBlock customBlock = CustomBlockManager.getBlock((String) args.get("block"));
                                                if (customBlock == null) return;
                                                ItemStack item = new ItemStack(customBlock.getMaterial());
                                                ItemMeta itemMeta = item.getItemMeta();
                                                itemMeta.getPersistentDataContainer().set(new NamespacedKey(AdvancedMining.getInstance(), "placeable_block"), PersistentDataType.STRING, (String) args.get("block"));
                                                itemMeta.displayName(MiniMessage.miniMessage().deserialize(customBlock.getDisplayName()));
                                                item.setItemMeta(itemMeta);
                                                sender.getInventory().addItem(item);
                                            }))))
                .then(new LiteralArgument("tool")
                        .then(new IntegerArgument("mining_speed")
                                .then(new IntegerArgument("breaking_power")
                                        .executesPlayer((sender, args) -> {

                                            ItemStack item = sender.getInventory().getItemInMainHand();
                                            ItemMeta itemMeta = item.getItemMeta();
                                            itemMeta.getPersistentDataContainer().set(new NamespacedKey(AdvancedMining.getInstance(), "stat_mining_speed"), PersistentDataType.INTEGER, (int) args.get("mining_speed"));
                                            itemMeta.getPersistentDataContainer().set(new NamespacedKey(AdvancedMining.getInstance(), "stat_breaking_power"), PersistentDataType.INTEGER, (int) args.get("breaking_power"));
                                            item.setItemMeta(itemMeta);

                                        }))))
                .register();

    }

}
