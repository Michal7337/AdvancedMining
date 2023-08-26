package me.michal737.advancedmining.MiningSystem;

import me.michal737.advancedmining.AdvancedMining;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class MiningListeners implements Listener {

    private BukkitRunnable runnable;

    @EventHandler
    public void OnBlockDamage(BlockDamageEvent event){

        runnable = new MiningRunnable(event);
        runnable.runTaskTimerAsynchronously(AdvancedMining.getInstance(), 0, 1);

    }

    @EventHandler
    public void OnBlockDamageAbort(BlockDamageAbortEvent event){

        runnable.cancel();

    }

}
