package win.codingboulder.advancedmining.mechanics;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import win.codingboulder.advancedmining.AdvancedMining;
import win.codingboulder.advancedmining.CustomBlock;

import java.util.HashMap;

public class Events implements Listener {

    public static HashMap<Player, MiningRunnable> miningRunnables = new HashMap<>();

    @EventHandler
    public void onBlockBreakStart(@NotNull BlockDamageEvent event) {

        Player player = event.getPlayer();
        Block block = event.getBlock();

        CustomBlock customBlock = CustomBlock.getCustomBlock(block);
        if (customBlock == null) return;

        AttributeInstance attrib =  player.getAttribute(Attribute.BLOCK_BREAK_SPEED);
        assert attrib != null;
        attrib.setBaseValue(0d);
        attrib.getModifiers().forEach(attrib::removeModifier);

        //checks for instamine, tool and hardness

        MiningRunnable runnable = new MiningRunnable(block, customBlock, player, 10, 5);
        runnable.runTaskTimer(AdvancedMining.getInstance(), 0, 1);
        miningRunnables.put(player, runnable);

    }

    @EventHandler
    public void onBlockBreakAbort(@NotNull BlockDamageAbortEvent event) {

        Player player = event.getPlayer();

        MiningRunnable runnable = miningRunnables.get(player);
        if (runnable == null) return;

        runnable.isCanceled = true;
        player.sendBlockDamage(event.getBlock().getLocation(), 0f, runnable.randomId);
        player.hideBossBar(runnable.progressbar);


    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        //event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, PotionEffect.INFINITE_DURATION, -1, false, false));

    }

}
