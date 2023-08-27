package me.michal737.advancedmining.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.*;
import me.michal737.advancedmining.CustomBlock;
import me.michal737.advancedmining.CustomBlockManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

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
                                        .then(new IntegerArgument("strength")
                                                .then(new IntegerArgument("resistance")
                                                        .then(new ItemStackArgument("material")
                                                                .then(new MultiLiteralArgument("breakType", List.of("REPLACE_WITH_BEDROCK", "BREAK_PERMANENTLY", "BREAK_TEMPORARILY", "REPLACE"))
                                                                        .executes((sender, args) -> {

                                                                                    String name = (String) args.get("name");
                                                                                    int strength = (int) args.get("strength");
                                                                                    int resistance = (int) args.get("resistance");
                                                                                    ItemStack itemStack = (ItemStack) args.get("material");
                                                                                    Material material = itemStack.getType();
                                                                                    String breakTypeName = (String) args.get("breakType");
                                                                                    CustomBlock.BreakType breakType = CustomBlock.BreakType.valueOf(breakTypeName);

                                                                                    if (!material.isBlock()) {sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>This material is not a block!")); return;}

                                                                                    CustomBlock customBlock = new CustomBlock(name, strength, resistance, material, breakType, List.of(), 0, Material.AIR);

                                                                                    CustomBlock.storeInFile(customBlock);

                                                                        })))))))
                        .then(new LiteralArgument("delete").withPermission("advancedmining.admin.block.delete")
                                .then(new StringArgument("name").replaceSuggestions(ArgumentSuggestions.strings(CustomBlockManager.getCustomBlockNames()))
                                        .executes((sender, args) -> {

                                            CustomBlockManager.deleteCustomBlock((String) args.get("name"));

                                        }))))
                .register();

    }

}
