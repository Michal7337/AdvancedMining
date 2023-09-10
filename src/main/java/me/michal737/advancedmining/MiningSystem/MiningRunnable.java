package me.michal737.advancedmining.MiningSystem;

import me.michal737.advancedmining.CustomBlock;
import me.michal737.advancedmining.CustomBlockManager;
import me.michal737.advancedmining.PlayerStats;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class MiningRunnable extends BukkitRunnable {

    private final Block block;
    private final CustomBlock customBlock;
    private final Player player;
    private float previousDestroyStage = 0f;
    private double destroyPercentage = 0;
    private float rawDestroyStage = 0f;
    private int blockDamage;
    private final int randomID;
    private final BossBar progressbar;

    public MiningRunnable(@NotNull BlockDamageEvent event){

        this.block = event.getBlock();
        this.customBlock = CustomBlockManager.getBlockAtLocation(event.getBlock());
        this.player = event.getPlayer();
        assert customBlock != null;
        this.blockDamage = customBlock.getStrength();
        this.randomID = new Random().nextInt();

        this.progressbar = BossBar.bossBar(MiniMessage.miniMessage().deserialize(customBlock.getDisplayName() + "<reset><gray> - " + destroyPercentage + "%"), rawDestroyStage, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);
        player.showBossBar(progressbar);

    }

    @Override
    public void run() {

        PlayerStats playerStats = new PlayerStats(player);

        // If player has enough mining speed break the block and skip all the checks
        if (playerStats.getMiningSpeed() >= customBlock.getStrength()) {breakBlock(); this.cancel(); return;}

        //Check if block is broken
        if (blockDamage <= 0) {breakBlock(); this.cancel(); return;}

        //Set destroy stage variables
        float destroyStage = 1 - roundValue(((float) blockDamage / customBlock.getStrength()));
        rawDestroyStage = 1 - ((float) blockDamage / customBlock.getStrength());
        //destroyPercentage = 100 - (roundValue(((float) blockDamage / customBlock.getStrength()) * 100));
        destroyPercentage = new BigDecimal("100").subtract(new BigDecimal(String.valueOf((double) blockDamage / customBlock.getStrength() * 100)).setScale(1, RoundingMode.FLOOR)).doubleValue();


        //Check if the destroy stage is different from the previous one and send a block damage
        if (destroyStage != previousDestroyStage) {player.sendBlockDamage(block.getLocation(), destroyStage, randomID); previousDestroyStage = destroyStage;}

        //Update progress bar
        progressbar.name(MiniMessage.miniMessage().deserialize(customBlock.getDisplayName() + "<reset><gray> - " + destroyPercentage + "%"));
        progressbar.progress(rawDestroyStage);

        //Damage the block
        blockDamage -= playerStats.getMiningSpeed();

        //Repeat

    }

    private void breakBlock(){

        sendBlockDamage(0f);
        player.hideBossBar(progressbar);

        switch (customBlock.getBreakType()){

            case BREAK -> {block.breakNaturally(player.getActiveItem()); CustomBlockManager.removeBlock(block);}
            case BREAK_TEMPORARILY -> {}
            case REPLACE -> {}
            case REPLACE_TEMPORARILY -> {}
            case REPLACE_VANILLA -> {}
            case REPLACE_TEMPORARILY_VANILLA -> {}

        }

    }

    public void sendBlockDamage(float stage){
        player.sendBlockDamage(block.getLocation(), stage, randomID);
    }

    private float roundValue(float value){

        return (float) (Math.floor(value * 10) / 10);

    }

    public void onCancel(){
        sendBlockDamage(0f);
        player.hideBossBar(progressbar);
    }

}
