package win.codingboulder.advancedmining.mechanics;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import win.codingboulder.advancedmining.CustomBlock;

public class Events implements Listener {

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



    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        //event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, PotionEffect.INFINITE_DURATION, -1, false, false));

    }

}
