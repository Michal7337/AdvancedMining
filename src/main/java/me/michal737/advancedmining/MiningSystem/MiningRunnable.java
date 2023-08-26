package me.michal737.advancedmining.MiningSystem;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class MiningRunnable extends BukkitRunnable {

    private BlockDamageEvent event;
    private Block block;

    public MiningRunnable(BlockDamageEvent event){

        block = event.getBlock();

    }

    @Override
    public void run() {

    }

}
