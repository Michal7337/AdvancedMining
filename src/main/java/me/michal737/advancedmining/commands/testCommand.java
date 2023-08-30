package me.michal737.advancedmining.commands;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import me.michal737.advancedmining.CustomBlockManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class testCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        @NotNull ArgumentSuggestions<CommandSender> a = CustomBlockManager.getAllBlocksArgumentSuggestions();
        sender.sendMessage(a.toString());

        return true;

    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        return null;

    }

}
