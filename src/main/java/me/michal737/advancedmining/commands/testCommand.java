package me.michal737.advancedmining.commands;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import me.michal737.advancedmining.CustomBlockManager;
import me.michal737.advancedmining.PlayerStats;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class testCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        int value = Integer.valueOf(args[1]);
        Player player = (Player) sender;

        if (args[0].equals("speed")) {
            PlayerStats.setMiningSpeed(player, value);
        }else if (args[0].equals("power")) PlayerStats.setBreakingPower(player, value);

        return true;

    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        return null;

    }

}
