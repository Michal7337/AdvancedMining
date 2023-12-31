package me.michal737.advancedmining.MiningSystem;

import me.michal737.advancedmining.AdvancedMining;
import me.michal737.advancedmining.CustomBlock;
import me.michal737.advancedmining.CustomBlockManager;
import me.michal737.advancedmining.PlayerStats;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MiningListeners implements Listener {

    private MiningRunnable runnable;

    @EventHandler
    public void OnBlockDamage(@NotNull BlockDamageEvent event){

        if (CustomBlockManager.getBlockAtLocation(event.getBlock()) == null) {return;}
        if (new PlayerStats(event.getPlayer()).getBreakingPower() < Objects.requireNonNull(CustomBlockManager.getBlockAtLocation(event.getBlock())).getResistance()) return;

        runnable = new MiningRunnable(event);
        runnable.runTaskTimer(AdvancedMining.getInstance(), 0, 1);

    }

    @EventHandler
    public void OnBlockDamageAbort(@NotNull BlockDamageAbortEvent event){

        if (runnable != null) {runnable.cancel(); runnable.onCancel();}

        //event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, PotionEffect.INFINITE_DURATION, -1, false, false, false));

    }

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event){
        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, PotionEffect.INFINITE_DURATION, -1, false, false));
    }

    @EventHandler
    public void onBlockPlace(@NotNull BlockPlaceEvent event){

        Block block = event.getBlock();
        ItemStack placedBlockItem = event.getItemInHand();
        if (!placedBlockItem.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(AdvancedMining.getInstance(), "placeable_block"))) return;
        CustomBlock customBlock = CustomBlockManager.getBlock(placedBlockItem.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(AdvancedMining.getInstance(), "placeable_block"), PersistentDataType.STRING));
        if (customBlock == null) return;
        CustomBlockManager.setBlock(customBlock, block);

    }

}
