package win.codingboulder.advancedmining;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import win.codingboulder.advancedmining.mechanics.Events;

import java.io.File;

public final class AdvancedMining extends JavaPlugin {

    private static AdvancedMining instance;

    public static File blocksFolder = null;
    public static File blockDropsFolder = null;

    public static final NamespacedKey MINING_SPEED_KEY = new NamespacedKey("advancedmining", "mining_speed");
    public static final NamespacedKey BREAKING_POWER_KEY = new NamespacedKey("advancedmining", "breaking_power");
    public static final NamespacedKey TOOL_TYPE_KEY = new NamespacedKey("advancedmining", "tool_type");

    @Override
    public void onEnable() {

        instance = this;

        blocksFolder = new File(getDataFolder(), "Blocks");
        blockDropsFolder = new File(getDataFolder(), "BlockDrops");

        loadConfig();

        getServer().getPluginManager().registerEvents(new Events(), this);

        new AdvancedMiningCommand(this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static AdvancedMining getInstance() {
        return instance;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void loadConfig() {

        getDataFolder().mkdir();
        blocksFolder.mkdir();
        blockDropsFolder.mkdir();

        CustomBlock.loadBlocks();

    }

}
