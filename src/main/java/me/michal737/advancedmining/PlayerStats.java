package me.michal737.advancedmining;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"unused", "DataFlowIssue"})
public class PlayerStats {

    private int miningSpeed, breakingPower;
    private final Player player;

    public PlayerStats(Player player){
        this.player = player;
    }

    public int getMiningSpeed(){

        //if (!player.getPersistentDataContainer().has(new NamespacedKey(AdvancedMining.getInstance(), "mining_speed"))) return 0;
        //return player.getPersistentDataContainer().get(new NamespacedKey(AdvancedMining.getInstance(), "mining_speed"), PersistentDataType.INTEGER);

        updateStats();
        return this.miningSpeed;

    }

    public void setMiningSpeed(int speed){

        //player.getPersistentDataContainer().set(new NamespacedKey(AdvancedMining.getInstance(), "mining_speed"), PersistentDataType.INTEGER, speed);

        this.miningSpeed = speed;

    }

    public int getBreakingPower(){

        //if (!player.getPersistentDataContainer().has(new NamespacedKey(AdvancedMining.getInstance(), "breaking_power"))) return 0;
        //return player.getPersistentDataContainer().get(new NamespacedKey(AdvancedMining.getInstance(), "breaking_power"), PersistentDataType.INTEGER);

        updateStats();
        return this.breakingPower;

    }

    public void setBreakingPower(int power){

        //player.getPersistentDataContainer().set(new NamespacedKey(AdvancedMining.getInstance(), "breaking_power"), PersistentDataType.INTEGER, power);

        this.breakingPower = power;

    }

    public void updateStats(){

        int toolMiningSpeed = 0, toolBreakingPower = 0;

        if (player.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(AdvancedMining.getInstance(), "stat_mining_speed")))
            toolMiningSpeed = player.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(AdvancedMining.getInstance(), "stat_mining_speed"), PersistentDataType.INTEGER);

        if (player.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(AdvancedMining.getInstance(), "stat_breaking_power")))
            toolBreakingPower = player.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(AdvancedMining.getInstance(), "stat_breaking_power"), PersistentDataType.INTEGER);

        miningSpeed = toolMiningSpeed;
        breakingPower = toolBreakingPower;

    }

}
