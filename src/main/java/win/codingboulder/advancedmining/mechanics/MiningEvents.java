package win.codingboulder.advancedmining.mechanics;

import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import win.codingboulder.advancedmining.AdvancedMining;
import win.codingboulder.advancedmining.CustomBlock;

import java.util.HashMap;

public class MiningEvents implements Listener {

    public static HashMap<Player, MiningRunnable> miningRunnables = new HashMap<>();

    @EventHandler
    public void onBlockBreakStart(@NotNull BlockDamageEvent event) {

        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!player.getGameMode().equals(GameMode.SURVIVAL)) return; // Return if the player is not in survival to not break stuff
        if (miningRunnables.containsKey(player)) return; // Return if the player is already mining a block

        CustomBlock customBlock = CustomBlock.getCustomBlock(block);
        if (customBlock == null) return;

        event.setCancelled(true); // prevent breaking of the block if something were to go wrong with mining prevention

        AttributeInstance attrib =  player.getAttribute(Attribute.BLOCK_BREAK_SPEED);
        assert attrib != null;
        attrib.setBaseValue(0d);
        attrib.getModifiers().forEach(attrib::removeModifier);

        ItemStack item = player.getInventory().getItemInMainHand();
        PersistentDataContainerView pdc = item.getPersistentDataContainer();
        float miningSpeed = pdc.getOrDefault(AdvancedMining.MINING_SPEED_KEY, PersistentDataType.FLOAT, 0f);
        int breakingPower = pdc.getOrDefault(AdvancedMining.BREAKING_POWER_KEY, PersistentDataType.INTEGER, 0);
        String toolType = item.isEmpty() ? "hand" : pdc.getOrDefault(AdvancedMining.TOOL_TYPE_KEY, PersistentDataType.STRING, "");

        //checks for tool and hardness
        if (!customBlock.bestTool().isEmpty() && !toolType.equals(customBlock.bestTool())) return;

        if (customBlock.hardness() > breakingPower) {
            if (!item.isEmpty()) player.sendRichMessage("<red>You need a better tool to mine this!");
            return;
        }

        MiningRunnable runnable = new MiningRunnable(block, customBlock, player, miningSpeed, breakingPower);
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
