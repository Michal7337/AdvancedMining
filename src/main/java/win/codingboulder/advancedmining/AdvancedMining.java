package win.codingboulder.advancedmining;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class AdvancedMining extends JavaPlugin {

    private static AdvancedMining instance;

    public static File blocksFolder = null;
    public static File blockDropsFolder = null;

    @Override
    public void onEnable() {

        instance = this;

        blocksFolder = new File(getDataFolder(), "Blocks");
        blockDropsFolder = new File(getDataFolder(), "BlockDrops");

        new AdvancedMiningCommand(this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static AdvancedMining getInstance() {
        return instance;
    }

}
