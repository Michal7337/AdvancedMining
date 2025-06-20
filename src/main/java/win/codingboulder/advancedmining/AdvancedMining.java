package win.codingboulder.advancedmining;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import win.codingboulder.advancedmining.mechanics.BlockDrops;
import win.codingboulder.advancedmining.mechanics.DefaultBlocks;
import win.codingboulder.advancedmining.mechanics.DefaultTools;
import win.codingboulder.advancedmining.mechanics.MiningEvents;

import java.io.File;

public final class AdvancedMining extends JavaPlugin {

    private static AdvancedMining instance;

    public static File blocksFolder = null;
    public static File blockDropsFolder = null;

    public static final String NAMESPACE = "advancedmining";
    public static final NamespacedKey MINING_SPEED_KEY = new NamespacedKey(NAMESPACE, "mining_speed");
    public static final NamespacedKey BREAKING_POWER_KEY = new NamespacedKey(NAMESPACE, "breaking_power");
    public static final NamespacedKey TOOL_TYPE_KEY = new NamespacedKey(NAMESPACE, "tool_type");
    public static final NamespacedKey PLACED_BLOCK_KEY = new NamespacedKey(NAMESPACE, "placed_block");

    @Override
    public void onEnable() {

        instance = this;

        blocksFolder = new File(getDataFolder(), "Blocks");
        blockDropsFolder = new File(getDataFolder(), "BlockDrops");

        loadConfig();

        getServer().getPluginManager().registerEvents(new MiningEvents(), this);
        getServer().getPluginManager().addPermission(new Permission("advancedmining.command.admin", "Enables access to the /advmining command", PermissionDefault.OP));

        new AdvancedMiningCommand(this);

        getLogger().info("AdvancedMining enabled!");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static AdvancedMining getInstance() {
        return instance;
    }

    public static boolean showProgressBar;
    public static boolean breakVanillaBlocks;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void loadConfig() {

        getDataFolder().mkdir();
        blocksFolder.mkdir();
        blockDropsFolder.mkdir();

        saveDefaultConfig();
        reloadConfig();
        FileConfiguration config = getConfig();
        showProgressBar = config.getBoolean("show-progress-bar", true);
        breakVanillaBlocks = config.getBoolean("break-vanilla-blocks", false);

        CustomBlock.loadAll();
        BlockDrops.loadAll();
        DefaultBlocks.loadFromFile();
        DefaultTools.loadFromFile();

        getLogger().info("Config loaded!");

    }

}
