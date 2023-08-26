package me.michal737.advancedmining.commands;

import me.michal737.advancedmining.CustomBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class testCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        String blocks = CustomBlock.getAllCustomBlocks().get(0) + CustomBlock.getAllCustomBlocks().get(1) + CustomBlock.getAllCustomBlocks().get(2);

        sender.sendMessage(blocks);

        return true;

    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        return null;

    }

}
