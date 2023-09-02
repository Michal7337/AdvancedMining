package me.michal737.advancedmining;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import me.michal737.advancedmining.MiningSystem.MiningListeners;
import me.michal737.advancedmining.commands.AdvancedMiningCommand;
import me.michal737.advancedmining.commands.testCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class AdvancedMining extends JavaPlugin {

    private static JavaPlugin plugin;
    //private static CommandManager<CommandSender> commandManager;
    private static File customBlocksFolder;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onEnable() {

        plugin = this;

        //try {commandManager = new PaperCommandManager<>(this, CommandExecutionCoordinator.simpleCoordinator(), Function.identity(), Function.identity());} catch (Exception e) {throw new RuntimeException(e);}

        getDataFolder().mkdir();
        customBlocksFolder = new File(getDataFolder().getAbsolutePath() + "/blocks");
        customBlocksFolder.mkdir();

        CustomBlockManager.updateBlockList();
        CommandAPI.onEnable();
        BlockDataStorage.initialiseBlockDatabase(new File(getDataFolder().getAbsolutePath() + "/blockDatabase.json"));

        registerCommands();
        getServer().getPluginManager().registerEvents(new MiningListeners(), this);

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

    //public static CommandManager<CommandSender> getCommandManager(){
        //return commandManager;
    //}

    @SuppressWarnings("DataFlowIssue")
    private void registerCommands(){

        getCommand("test").setExecutor(new testCommand());
        //new AdvancedMiningCommand().registerCommand();

        new AdvancedMiningCommand();

    }

}
