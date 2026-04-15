package win.codingboulder.advancedmining;

import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import win.codingboulder.advancedmining.mechanics.*;

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

        BlockRegenSystem.loadSystem(this);

        getLogger().info("AdvancedMining enabled!");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static AdvancedMining getInstance() {
        return instance;
    }

    /**
     * Reloads the plugin's configuration. This includes Blocks, Drops and such.
     */
    public void loadConfig() {

        Config.loadConfig();

    }

    public static class Config {

        public static boolean showProgressBar;
        public static BossBar.Color progressBarColor;
        public static boolean breakVanillaBlocks;
        public static int crackingAnimationRange;
        public static boolean allowBreakingMultipleBlocks;
        public static int miningProgressResetTime;
        public static int simultaneousBrokenBlocksLimit;
        public static boolean allowToolSwapping;

        public static boolean efficiencyEnable;
        public static String efficiencyEffectType;
        public static float efficiencyAmount;

        public static boolean hasteEnable;
        public static String hasteEffectType;
        public static float hasteAmount;

        public static boolean miningFatigueEnable;
        public static String miningFatigueEffectType;
        public static float miningFatigueAmount;

        public static boolean fortuneEnable;
        public static String fortuneEffectType;
        public static int fortuneMinAmount;
        public static int fortuneMaxAmount;
        public static float fortuneDropChance;
        public static int fortuneDropRolls;
        public static String fortuneVanillaBehavior;
        public static boolean fortuneVanillaIgnoreSilkTouch;

        @SuppressWarnings("ResultOfMethodCallIgnored")
        public static void loadConfig() {

            getInstance().getDataFolder().mkdir();
            blocksFolder.mkdir();
            blockDropsFolder.mkdir();

            getInstance().saveDefaultConfig();
            getInstance().reloadConfig();
            FileConfiguration config = getInstance().getConfig();
            showProgressBar = config.getBoolean("show-progress-bar", true);
            breakVanillaBlocks = config.getBoolean("break-vanilla-blocks", false);
            crackingAnimationRange = config.getInt("cracking-animation-range", 50);
            allowBreakingMultipleBlocks = config.getBoolean("allow-breaking-multiple-blocks", false);
            miningProgressResetTime = config.getInt("mining-progress-reset-timer", 1200);
            simultaneousBrokenBlocksLimit = config.getInt("simultaneous-broken-blocks-limit", 8);
            progressBarColor = BossBar.Color.NAMES.valueOr(config.getString("progress-bar-color", "blue"), BossBar.Color.BLUE);
            allowToolSwapping = config.getBoolean("allow-tool-swapping", false);

            efficiencyEnable = config.getBoolean("enchantments.efficiency.enable", false);
            efficiencyEffectType = config.getString("enchantments.efficiency.effect-type", "constant");
            efficiencyAmount = (float) config.getDouble("enchantments.efficiency.amount", 100.0d);

            fortuneEnable = config.getBoolean("enchantments.fortune.enable", false);
            fortuneEffectType = config.getString("enchantments.fortune.effect-type", "vanilla");
            fortuneMinAmount = config.getInt("enchantments.fortune.increase-min-amount", 1);
            fortuneMaxAmount = config.getInt("enchantments.fortune.increase-max-amount", 1);
            fortuneDropChance = (float) config.getDouble("enchantments.fortune.increase-drop-chance", 0.1d);
            fortuneDropRolls = config.getInt("enchantments.fortune.increase-drop-rolls", 0);
            fortuneVanillaBehavior = config.getString("enchantments.fortune.vanilla-behaviour", "additional-rolls");
            fortuneVanillaIgnoreSilkTouch = config.getBoolean("enchantments.fortune.vanilla-ignore-silk-touch", false);

            hasteEnable = config.getBoolean("effects.haste.enable", false);
            hasteEffectType = config.getString("effects.haste.effect-type", "constant");
            hasteAmount = (float) config.getDouble("effects.haste.amount", 100.0d);

            miningFatigueEnable = config.getBoolean("effects.mining-fatigue.enable", false);
            miningFatigueEffectType = config.getString("effects.mining-fatigue.effect-type", "constant");
            miningFatigueAmount = (float) config.getDouble("effects.mining-fatigue.amount", 100.0d);

            CustomBlock.loadAll();
            BlockDrops.loadAll();
            DefaultBlocks.loadFromFile();
            DefaultTools.loadFromFile();

            getInstance().getLogger().info("Config loaded!");

        }

    }

}
