package me.michal737.advancedmining.MiningSystem;

import me.michal737.advancedmining.CustomBlock;
import me.michal737.advancedmining.CustomBlockManager;
import me.michal737.advancedmining.PlayerStats;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class MiningRunnable extends BukkitRunnable {

    private final Block block;
    private final CustomBlock customBlock;
    private final Player player;
    private float destroyStage;
    private int blockDamage;
    private final int randomID;
    private float previousDestroyStage;

    public MiningRunnable(@NotNull BlockDamageEvent event){

        this.block = event.getBlock();
        this.customBlock = CustomBlockManager.getBlockAtLocation(event.getBlock());
        this.player = event.getPlayer();
        this.destroyStage = 0f;
        assert customBlock != null;
        this.blockDamage = customBlock.getStrength();
        this.previousDestroyStage = 0f;
        this.randomID = new Random().nextInt();

    }

    @Override
    public void run() {

        // If player has enough mining speed break the block and skip all the checks
        if (PlayerStats.getMiningSpeed(player) >= customBlock.getStrength()) {breakBlock(); this.cancel(); return;}

        //Check if block is broken
        if (blockDamage <= 0) {breakBlock(); this.cancel(); return;}

        //Get the destroy stage
        destroyStage = 1 - roundValue(((float) blockDamage / customBlock.getStrength()));

        //Check if the destroy stage is different from the previous one and send a block damage
        if (destroyStage != previousDestroyStage) {player.sendBlockDamage(block.getLocation(), destroyStage, randomID); previousDestroyStage = destroyStage;}

        //Damage the block
        blockDamage -= PlayerStats.getMiningSpeed(player);

        player.sendMessage(String.valueOf(blockDamage));

        //Repeat

    }

    private void breakBlock(){

        sendBlockDamage(0f);

    }

    public void sendBlockDamage(float stage){
        player.sendBlockDamage(block.getLocation(), stage, randomID);
    }

    private float roundValue(float value){

        return (float) (Math.floor(value * 10) / 10);

    }

}
