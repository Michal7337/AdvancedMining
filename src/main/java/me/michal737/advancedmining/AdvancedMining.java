package me.michal737.advancedmining;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import me.michal737.advancedmining.commands.AdvancedMiningCommand;
import me.michal737.advancedmining.commands.testCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class AdvancedMining extends JavaPlugin {

    private static JavaPlugin plugin;
    private static File customBlocksFolder;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onEnable() {

        plugin = this;

        getDataFolder().mkdir();
        customBlocksFolder = new File(getDataFolder().getAbsolutePath() + "/blocks");
        customBlocksFolder.mkdir();

        CommandAPI.onEnable();
        BlockDataStorage.initialise(new File(getDataFolder().getAbsolutePath() + "/blockDatabase.json"));

        registerCommands();

        getLogger().info("ImprovedMining enabled!");

    }

    @Override
    public void onDisable() {

        CommandAPI.onDisable();

        getLogger().info("ImprovedMining disabled!");

    }

    @Override
    public void onLoad(){

        CommandAPI.onLoad(new CommandAPIBukkitConfig(this));

    }

    public static Plugin getInstance() {
        return plugin;
    }

    public static File getCustomBlocksFolder() {
        return customBlocksFolder;
    }

    @SuppressWarnings("DataFlowIssue")
    private void registerCommands(){

        getCommand("test").setExecutor(new testCommand());

        new AdvancedMiningCommand();

    }

}
