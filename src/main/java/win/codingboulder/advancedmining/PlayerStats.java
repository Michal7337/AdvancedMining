package win.codingboulder.advancedmining;

import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NonNull;
import win.codingboulder.advancedmining.mechanics.DefaultTools;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.function.BiConsumer;

public class PlayerStats {

    private static final LinkedHashMap<String, BiConsumer<Player, PlayerStats>> statModifiers = new LinkedHashMap<>();

    private Player player;
    private boolean isCalculated;

    private float miningSpeed;
    private int breakingPower;
    private String toolType;

    private int miningFortune;
    private int minXpDropBonus;
    private int maxXpDropBonus;
    private float miningSpread;
    private boolean hasTelekinesis;

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

    public int miningFortune() {
        return miningFortune;
    }

    public void setMiningFortune(int miningFortune) {
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

    public static void registerDefaultStatModifiers() {

        PlayerStats.statModifiers().putFirst("default_tool_modifier", (player, playerStats) -> {

            // Get player stats. If a stat is not defined check default tools
            ItemStack item = player.getInventory().getItemInMainHand();
            PersistentDataContainerView pdc = item.getPersistentDataContainer();
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

            playerStats.setMiningSpeed(miningSpeed);
            playerStats.setBreakingPower(breakingPower);
            playerStats.setToolType(toolType);

        });

        PlayerStats.statModifiers().put("default_armor_modifier", (player, playerStats) -> {

            for (ItemStack item : player.getInventory().getArmorContents())
                if (item != null) playerStats.addMiningSpeed(item.getPersistentDataContainer().getOrDefault(AdvancedMining.MINING_SPEED_KEY, PersistentDataType.FLOAT, 0f));

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

    }

}
