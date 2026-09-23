package win.codingboulder.advancedmining;

import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NonNull;
import win.codingboulder.advancedmining.mechanics.DefaultTools;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static win.codingboulder.advancedmining.AdvancedMining.NAMESPACE;

public class PlayerStats {

    private static final LinkedHashMap<String, BiConsumer<Player, PlayerStats>> statModifiers = new LinkedHashMap<>();

    public static final NamespacedKey ITEM_TYPE_KEY = new NamespacedKey(NAMESPACE, "item_type");
    public static final NamespacedKey MINING_FORTUNE_KEY = new NamespacedKey(NAMESPACE, "mining_fortune");
    public static final NamespacedKey MIN_XP_DROP_BONUS_KEY = new NamespacedKey(NAMESPACE, "min_xp_drop_bonus");
    public static final NamespacedKey MAX_XP_DROP_BONUS_KEY = new NamespacedKey(NAMESPACE, "max_xp_drop_bonus");
    public static final NamespacedKey MINING_SPREAD_KEY = new NamespacedKey(NAMESPACE, "mining_spread");
    public static final NamespacedKey TELEKINESIS_KEY = new NamespacedKey(NAMESPACE, "telekinesis");
    public static final NamespacedKey SILK_TOUCH_KEY = new NamespacedKey(NAMESPACE, "silk_touch");
    public static final NamespacedKey FORTUNE_LEVEL_KEY = new NamespacedKey(NAMESPACE, "fortune_level");
    public static final NamespacedKey DROP_MIN_BONUS_KEY = new NamespacedKey(NAMESPACE, "drop_min_amount_bonus");
    public static final NamespacedKey DROP_MAX_BONUS_KEY = new NamespacedKey(NAMESPACE, "drop_max_amount_bonus");
    public static final NamespacedKey DROP_CHANCE_BONUS_KEY = new NamespacedKey(NAMESPACE, "drop_chance_bonus");
    public static final NamespacedKey DROP_ROLLS_BONUS_KEY = new NamespacedKey(NAMESPACE, "drop_rolls_bonus");

    private Player player;
    private boolean isCalculated;

    private float miningSpeed;
    private int breakingPower;
    private String toolType;

    private float miningFortune;
    private int minXpDropBonus;
    private int maxXpDropBonus;
    private float miningSpread;
    private boolean hasTelekinesis;
    private boolean hasSilkTouch;
    private int fortuneLevel;

    private int dropMinAmountBonus;
    private int dropMaxAmountBonus;
    private float dropChanceBonus;
    private int dropRollsBonus;


    private final HashMap<String, Object> otherStats = new HashMap<>();

    public PlayerStats(Player player) {
        this.player = player;
    }

    public void calculateStats() {

        statModifiers.forEach((id, consumer) -> consumer.accept(player, this));
        isCalculated = true;

    }

    public static LinkedHashMap<String, BiConsumer<Player, PlayerStats>> statModifiers() {
        return statModifiers;
    }

    public boolean isCalculated() {
        return isCalculated;
    }

    public Player player() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setCustomStat(String name, Object value) {
        otherStats.put(name, value);
    }

    public <T> T getCustomStat(String name, @NonNull Class<T> type) {
        Object val = otherStats.get(name);
        if (type.isInstance(val)) {
            return type.cast(val);
        }
        return null;
    }

    public PlayerStats withStats(@NonNull Consumer<PlayerStats> stats) {
        stats.accept(this);
        return this;
    }

    public PlayerStats addStatsFromItem(ItemStack item) {

        if (item == null) return this;

        miningFortune += item.getPersistentDataContainer().getOrDefault(AdvancedMining.MINING_SPEED_KEY, PersistentDataType.FLOAT, 0f);
        breakingPower += item.getPersistentDataContainer().getOrDefault(AdvancedMining.BREAKING_POWER_KEY, PersistentDataType.INTEGER, 0);
        miningFortune += item.getPersistentDataContainer().getOrDefault(MINING_FORTUNE_KEY, PersistentDataType.FLOAT, 0f);
        minXpDropBonus += item.getPersistentDataContainer().getOrDefault(MIN_XP_DROP_BONUS_KEY, PersistentDataType.INTEGER, 0);
        maxXpDropBonus += item.getPersistentDataContainer().getOrDefault(MAX_XP_DROP_BONUS_KEY, PersistentDataType.INTEGER, 0);
        miningSpread += item.getPersistentDataContainer().getOrDefault(MINING_SPREAD_KEY, PersistentDataType.INTEGER, 0);
        if (item.getPersistentDataContainer().getOrDefault(TELEKINESIS_KEY, PersistentDataType.BOOLEAN, false)) hasTelekinesis = true;
        if (item.getPersistentDataContainer().getOrDefault(SILK_TOUCH_KEY, PersistentDataType.BOOLEAN, false)) hasSilkTouch = true;
        fortuneLevel += item.getPersistentDataContainer().getOrDefault(FORTUNE_LEVEL_KEY, PersistentDataType.INTEGER, 0);
        dropMinAmountBonus += item.getPersistentDataContainer().getOrDefault(DROP_MIN_BONUS_KEY, PersistentDataType.INTEGER, 0);
        dropMaxAmountBonus += item.getPersistentDataContainer().getOrDefault(DROP_MAX_BONUS_KEY, PersistentDataType.INTEGER, 0);
        dropChanceBonus += item.getPersistentDataContainer().getOrDefault(DROP_CHANCE_BONUS_KEY, PersistentDataType.FLOAT, 0f);
        dropRollsBonus += item.getPersistentDataContainer().getOrDefault(DROP_ROLLS_BONUS_KEY, PersistentDataType.INTEGER, 0);

        return this;

    }

    public static void registerDefaultStatModifiers() {

        PlayerStats.statModifiers().putFirst("default_tool_modifier", (player, playerStats) -> {

            // Get player stats. If a stat is not defined check default tools
            ItemStack item = player.getInventory().getItemInMainHand();
            PersistentDataContainerView pdc = item.getPersistentDataContainer();

            // Check if the item is a tool. If it doesn't have the tag assume it's a legacy item from older versions which didn't have the tag
            if (pdc.has(ITEM_TYPE_KEY) && !pdc.getOrDefault(ITEM_TYPE_KEY, PersistentDataType.STRING, "").equalsIgnoreCase("tool")) return;

            DefaultTools.Tool defaultTool = DefaultTools.getDefaultMapping(item.getType()); // Get the default tool
            float miningSpeed = pdc.getOrDefault(AdvancedMining.MINING_SPEED_KEY, PersistentDataType.FLOAT, defaultTool == null ? 0f : defaultTool.miningSpeed());
            int breakingPower = pdc.getOrDefault(AdvancedMining.BREAKING_POWER_KEY, PersistentDataType.INTEGER, defaultTool == null ? 0 : defaultTool.breakingPower());
            String toolType = item.isEmpty() ? "hand" : pdc.getOrDefault(AdvancedMining.TOOL_TYPE_KEY, PersistentDataType.STRING, defaultTool == null ? "" : defaultTool.toolType());

            // Efficiency enchantment
            if (AdvancedMining.Config.efficiencyEnable) {
                if (AdvancedMining.Config.efficiencyEffectType.equals("percent")) {
                    miningSpeed += miningSpeed * item.getEnchantmentLevel(Enchantment.EFFICIENCY) * AdvancedMining.Config.efficiencyAmount;
                } else {
                    miningSpeed += item.getEnchantmentLevel(Enchantment.EFFICIENCY) * AdvancedMining.Config.efficiencyAmount;
                }
            }

            if (!pdc.has(FORTUNE_LEVEL_KEY)) playerStats.fortuneLevel += item.getEnchantmentLevel(Enchantment.FORTUNE); // If the item doesn't have a set fortune level stat, read its actual fortune enchantment level

            // Add all the stats then replace some with the above calculated values which take into account default tools
            playerStats.addStatsFromItem(item);

            playerStats.setMiningSpeed(miningSpeed);
            playerStats.setBreakingPower(breakingPower);
            playerStats.setToolType(toolType);

        });

        PlayerStats.statModifiers().put("default_armor_modifier", (player, playerStats) -> {

            for (ItemStack item : player.getInventory().getArmorContents())
                if (item != null && item.getPersistentDataContainer().getOrDefault(ITEM_TYPE_KEY, PersistentDataType.STRING, "").equalsIgnoreCase("armor")) {
                    playerStats.addStatsFromItem(item);
                }

        });

        PlayerStats.statModifiers().put("default_offhand_modifier", (player, playerStats) -> {

            ItemStack item = player.getInventory().getItemInOffHand();
            if (item.getPersistentDataContainer().getOrDefault(ITEM_TYPE_KEY, PersistentDataType.STRING, "").equalsIgnoreCase("offhand")) playerStats.addStatsFromItem(item);

        });

        PlayerStats.statModifiers().put("default_accessory_modifier", (player, playerStats) -> {

            ItemStack[] items = player.getInventory().getContents();
            for (ItemStack item : items) if (item.getPersistentDataContainer().getOrDefault(ITEM_TYPE_KEY, PersistentDataType.STRING, "").equalsIgnoreCase("accessory")) playerStats.addStatsFromItem(item);

        });

        PlayerStats.statModifiers().put("default_potion_modifier", (player, playerStats) -> {

            float miningSpeed = playerStats.miningSpeed();

            // Haste effect
            if (AdvancedMining.Config.hasteEnable && player.hasPotionEffect(PotionEffectType.HASTE)) {
                if (AdvancedMining.Config.hasteEffectType.equals("percent")) {
                    miningSpeed += miningSpeed * (Objects.requireNonNull(player.getPotionEffect(PotionEffectType.HASTE)).getAmplifier() + 1) * AdvancedMining.Config.hasteAmount;
                } else {
                    miningSpeed += (Objects.requireNonNull(player.getPotionEffect(PotionEffectType.HASTE)).getAmplifier() + 1) * AdvancedMining.Config.hasteAmount;
                }
            }

            // Mining Fatigue effect
            if (AdvancedMining.Config.miningFatigueEnable && player.hasPotionEffect(PotionEffectType.MINING_FATIGUE)) {
                if (AdvancedMining.Config.miningFatigueEffectType.equals("percent")) {
                    miningSpeed -= miningSpeed * (Objects.requireNonNull(player.getPotionEffect(PotionEffectType.MINING_FATIGUE)).getAmplifier() + 1) * AdvancedMining.Config.miningFatigueAmount;
                } else {
                    miningSpeed -= (Objects.requireNonNull(player.getPotionEffect(PotionEffectType.MINING_FATIGUE)).getAmplifier() + 1) * AdvancedMining.Config.miningFatigueAmount;
                }
            }

            playerStats.setMiningSpeed(miningSpeed);

        });

        PlayerStats.statModifiers().put("default_fortune_stats_modifier", (player, stats) -> {

            if (!AdvancedMining.Config.fortuneEnable || !AdvancedMining.Config.fortuneEffectType.equalsIgnoreCase("custom")) return;

            int fortuneLevel = stats.fortuneLevel();
            stats.dropMinAmountBonus += fortuneLevel * AdvancedMining.Config.fortuneMinAmount;
            stats.dropMaxAmountBonus += fortuneLevel * AdvancedMining.Config.fortuneMaxAmount;
            stats.dropChanceBonus += fortuneLevel * AdvancedMining.Config.fortuneDropChance;
            stats.dropRollsBonus += fortuneLevel * AdvancedMining.Config.fortuneDropRolls;

        });

    }

    public float miningSpeed() {
        return miningSpeed;
    }

    public void setMiningSpeed(float miningSpeed) {
        this.miningSpeed = miningSpeed;
    }

    public void addMiningSpeed(float amount) {
        this.miningSpeed += amount;
    }

    public int breakingPower() {
        return breakingPower;
    }

    public void setBreakingPower(int breakingPower) {
        this.breakingPower = breakingPower;
    }

    public String toolType() {
        return toolType;
    }

    public void setToolType(String toolType) {
        this.toolType = toolType;
    }

    public boolean hasTelekinesis() {
        return hasTelekinesis;
    }

    public void setHasTelekinesis(boolean hasTelekinesis) {
        this.hasTelekinesis = hasTelekinesis;
    }

    public float miningFortune() {
        return miningFortune;
    }

    public void setMiningFortune(float miningFortune) {
        this.miningFortune = miningFortune;
    }

    public int minXpDropBonus() {
        return minXpDropBonus;
    }

    public void setMinXpDropBonus(int minXpDropBonus) {
        this.minXpDropBonus = minXpDropBonus;
    }

    public int maxXpDropBonus() {
        return maxXpDropBonus;
    }

    public void setMaxXpDropBonus(int maxXpDropBonus) {
        this.maxXpDropBonus = maxXpDropBonus;
    }

    public float miningSpread() {
        return miningSpread;
    }

    public void setMiningSpread(float miningSpread) {
        this.miningSpread = miningSpread;
    }

    public int dropMinAmountBonus() {
        return dropMinAmountBonus;
    }

    public void setDropMinAmountBonus(int dropMinAmountBonus) {
        this.dropMinAmountBonus = dropMinAmountBonus;
    }

    public int dropMaxAmountBonus() {
        return dropMaxAmountBonus;
    }

    public void setDropMaxAmountBonus(int dropMaxAmountBonus) {
        this.dropMaxAmountBonus = dropMaxAmountBonus;
    }

    public float dropChanceBonus() {
        return dropChanceBonus;
    }

    public void setDropChanceBonus(float dropChanceBonus) {
        this.dropChanceBonus = dropChanceBonus;
    }

    public int dropRollsBonus() {
        return dropRollsBonus;
    }

    public void setDropRollsBonus(int dropRollsBonus) {
        this.dropRollsBonus = dropRollsBonus;
    }

    public boolean hasSilkTouch() {
        return hasSilkTouch;
    }

    public void setHasSilkTouch(boolean hasSilkTouch) {
        this.hasSilkTouch = hasSilkTouch;
    }

    public int fortuneLevel() {
        return fortuneLevel;
    }

    public void setFortuneLevel(int fortuneLevel) {
        this.fortuneLevel = fortuneLevel;
    }

}
