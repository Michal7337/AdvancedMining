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
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import win.codingboulder.advancedmining.AdvancedMining;
import win.codingboulder.advancedmining.CustomBlock;
import win.codingboulder.advancedmining.api.CustomBlockBreakStartEvent;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class MiningEvents implements Listener {

    public static HashMap<Player, LinkedHashMap<Block, MiningRunnable>> miningRunnables = new HashMap<>();

    @EventHandler
    public void onBlockBreakStart(@NotNull BlockDamageEvent event) {

        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!player.getGameMode().equals(GameMode.SURVIVAL)) return; // Return if the player is not in survival to not break stuff

        // Check if there is a default mapping for this material
        // If there is no default mapping or if a block is placed there normally set it normally
        CustomBlock customBlock = DefaultBlocks.getDefaultMapping(block.getType());
        if (customBlock == null || CustomBlock.getCustomBlock(block) != null) customBlock = CustomBlock.getCustomBlock(block);

        if (customBlock == null) return; // If the block isn't a custom block return

        event.setCancelled(true); // prevent breaking of the block if something were to go wrong with mining prevention

        // Set the Block Break Speed attribute to 0 to remove the client-side block breaking mechanics
        AttributeInstance attrib =  player.getAttribute(Attribute.BLOCK_BREAK_SPEED);
        assert attrib != null;
        attrib.setBaseValue(0d);
        attrib.getModifiers().forEach(attrib::removeModifier);

        // Get player stats. If a stat is not defined check default tools
        ItemStack item = player.getInventory().getItemInMainHand();
        PersistentDataContainerView pdc = item.getPersistentDataContainer();
        DefaultTools.Tool defaultTool = DefaultTools.getDefaultMapping(item.getType()); // Get the default tool
        float miningSpeed = pdc.getOrDefault(AdvancedMining.MINING_SPEED_KEY, PersistentDataType.FLOAT, defaultTool == null ? 0f : defaultTool.miningSpeed());
        int breakingPower = pdc.getOrDefault(AdvancedMining.BREAKING_POWER_KEY, PersistentDataType.INTEGER, defaultTool == null ? 0 : defaultTool.breakingPower());
        String toolType = item.isEmpty() ? "hand" : pdc.getOrDefault(AdvancedMining.TOOL_TYPE_KEY, PersistentDataType.STRING, defaultTool == null ? "" : defaultTool.toolType());

        // Create the event and return if it was canceled
        CustomBlockBreakStartEvent breakStartEvent = new CustomBlockBreakStartEvent(player, block, customBlock, miningSpeed, breakingPower, toolType);
        if (!breakStartEvent.callEvent()) return;

        customBlock = breakStartEvent.getCustomBlock();
        if (customBlock == null) return;
        miningSpeed = breakStartEvent.getMiningSpeed();
        breakingPower = breakStartEvent.getBreakingPower();
        toolType = breakStartEvent.getToolType();

        //checks for tool and hardness
        if (!customBlock.bestTool().isEmpty() && !toolType.equals(customBlock.bestTool())) return;

        if (customBlock.hardness() > breakingPower) {
            if (!item.isEmpty()) player.sendRichMessage("<red>You need at least Breaking Power " + customBlock.hardness() + " to mine this!");
            return;
        }

        if (miningRunnables.containsKey(player)) { // If the player was mining something

            LinkedHashMap<Block, MiningRunnable> playerRunnables = miningRunnables.get(player); //Get the blocks the player is mining

            if (playerRunnables.containsKey(block)) playerRunnables.get(block).unpauseMining(); //If the block was being mined, unpause it
            else { //Else create a new runnable

                MiningRunnable runnable = new MiningRunnable(block, customBlock, player, miningSpeed, breakingPower);
                runnable.setTool(item);
                runnable.runTaskTimer(AdvancedMining.getInstance(), 0, 1);
                playerRunnables.putLast(block, runnable);

                //If the amount of broken blocks is above the limit, remove the oldest one
                if (playerRunnables.size() > AdvancedMining.Config.simultaneousBrokenBlocksLimit) {
                    MiningRunnable miningRunnable = playerRunnables.firstEntry().getValue();
                    miningRunnable.stopMining();
                    playerRunnables.remove(playerRunnables.firstEntry().getKey());
                }

            }

        } else { // If the player wasn't mining anything, create a new list and a new runnable

            miningRunnables.put(player, new LinkedHashMap<>());

            MiningRunnable runnable = new MiningRunnable(block, customBlock, player, miningSpeed, breakingPower);
            runnable.setTool(item);
            runnable.runTaskTimer(AdvancedMining.getInstance(), 0, 1);
            miningRunnables.get(player).putLast(block, runnable);

        }

    }

    @EventHandler
    public void onBlockBreakAbort(@NotNull BlockDamageAbortEvent event) {

        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (AdvancedMining.Config.breakVanillaBlocks) {
            AttributeInstance attrib =  player.getAttribute(Attribute.BLOCK_BREAK_SPEED);
            assert attrib != null;
            attrib.setBaseValue(1d);
        }

        LinkedHashMap<Block, MiningRunnable> playerRunnables = miningRunnables.get(player);
        if (playerRunnables == null) return;
        MiningRunnable runnable = playerRunnables.get(block);
        if (runnable == null) return;

        if (AdvancedMining.Config.allowBreakingMultipleBlocks) {
            runnable.pauseMining();
        } else {
            runnable.stopMining();
            playerRunnables.remove(block);
        }

    }

    @EventHandler
    public void onBlockPlace(@NotNull BlockPlaceEvent event) {

        ItemStack itemStack = event.getItemInHand();
        String placedBlock = itemStack.getPersistentDataContainer().get(AdvancedMining.PLACED_BLOCK_KEY, PersistentDataType.STRING);
        if (placedBlock == null) return;
        CustomBlock.setCustomBlock(event.getBlock(), placedBlock);

    }

    @EventHandler
    public void onPlayerLeave(@NotNull PlayerQuitEvent event) {

        Player player = event.getPlayer();

        if (miningRunnables.containsKey(player)) miningRunnables.get(player).forEach((block, miningRunnable) -> miningRunnable.stopMining());
        miningRunnables.remove(player);

    }

}
