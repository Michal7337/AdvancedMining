package me.michal737.advancedmining;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"unused", "DataFlowIssue"})
public class PlayerStats {

    private PlayerStats(){}

    public static int getMiningSpeed(@NotNull Player player){

        if (!player.getPersistentDataContainer().has(new NamespacedKey(AdvancedMining.getInstance(), "mining_speed"))) return 0;
        return player.getPersistentDataContainer().get(new NamespacedKey(AdvancedMining.getInstance(), "mining_speed"), PersistentDataType.INTEGER);

    }

    public static void setMiningSpeed(@NotNull Player player, Integer speed){

        player.getPersistentDataContainer().set(new NamespacedKey(AdvancedMining.getInstance(), "mining_speed"), PersistentDataType.INTEGER, speed);

    }

    public static int getBreakingPower(@NotNull Player player){

        if (!player.getPersistentDataContainer().has(new NamespacedKey(AdvancedMining.getInstance(), "breaking_power"))) return 0;
        return player.getPersistentDataContainer().get(new NamespacedKey(AdvancedMining.getInstance(), "breaking_power"), PersistentDataType.INTEGER);

    }

    public static void setBreakingPower(@NotNull Player player, int power){

        player.getPersistentDataContainer().set(new NamespacedKey(AdvancedMining.getInstance(), "breaking_power"), PersistentDataType.INTEGER, power);

    }

}
