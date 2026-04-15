package win.codingboulder.advancedmining.mechanics;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NonNull;
import win.codingboulder.advancedmining.AdvancedMining;
import win.codingboulder.advancedmining.BlockDataStorage;
import win.codingboulder.advancedmining.CustomBlock;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BlockRegenSystem extends BukkitRunnable {

    private static BlockRegenSystem blockRegenSystem;

    public static final NamespacedKey REGENERATING_BLOCKS_LIST_KEY = new NamespacedKey(AdvancedMining.NAMESPACE, "regenerating_blocks");
    public static final NamespacedKey BLOCK = new NamespacedKey(AdvancedMining.NAMESPACE, "block");
    public static final NamespacedKey IS_ALTERNATE = new NamespacedKey(AdvancedMining.NAMESPACE, "is_alternate");
    public static final NamespacedKey REGEN_TIME = new NamespacedKey(AdvancedMining.NAMESPACE, "regen_time");

    private static ConcurrentHashMap<Block, Integer> activeBlocks = new ConcurrentHashMap<>();
    private static HashMap<Block, Boolean> alternateRolls = new HashMap<>();
    private static HashMap<Block, CustomBlock> customBlockMap = new HashMap<>();

    public static void loadSystem(@NonNull JavaPlugin plugin) {

        if (blockRegenSystem == null) {
            blockRegenSystem = new BlockRegenSystem();
            blockRegenSystem.runTaskTimer(plugin, 0, 1);
        }

        activeBlocks = new ConcurrentHashMap<>();
        alternateRolls = new HashMap<>();
        customBlockMap = new HashMap<>();

        plugin.getServer().getWorlds().forEach(world -> {

            PersistentDataContainer pdc = world.getPersistentDataContainer();
            if (!pdc.has(REGENERATING_BLOCKS_LIST_KEY)) pdc.set(REGENERATING_BLOCKS_LIST_KEY, PersistentDataType.TAG_CONTAINER, pdc.getAdapterContext().newPersistentDataContainer());
            PersistentDataContainer placedPdc = pdc.get(REGENERATING_BLOCKS_LIST_KEY, PersistentDataType.TAG_CONTAINER);
            if (placedPdc == null) return;

            Set<NamespacedKey> blockKeys = placedPdc.getKeys();
            for (NamespacedKey key : blockKeys) { // For every regenerating block, read attributes and put into maps

                Block block = BlockDataStorage.getBlockFromKey(key, world);
                if (block == null) continue;

                PersistentDataContainer container = placedPdc.get(key, PersistentDataType.TAG_CONTAINER);
                if (container == null) continue;

                boolean isAlternate = container.getOrDefault(IS_ALTERNATE, PersistentDataType.BOOLEAN, false);
                int regenTime = container.getOrDefault(REGEN_TIME, PersistentDataType.INTEGER, 1);
                String blockId = container.get(BLOCK, PersistentDataType.STRING);

                activeBlocks.put(block, regenTime);
                alternateRolls.put(block, isAlternate);
                customBlockMap.put(block, CustomBlock.loadedBlocks().get(blockId));

            }

        });

    }

    public static void registerBlock(Block block, CustomBlock customBlock) {

        if (customBlock == null || block == null) return;
        String regenType = customBlock.blockRegenType();
        String altRegenType = customBlock.regenAlternativeType();

        if (!"vanilla".equals(regenType) && !"custom".equals(regenType)) return; // If no regen return

        boolean isAlternate = false;
        if ("vanilla".equalsIgnoreCase(altRegenType) || "custom".equalsIgnoreCase(altRegenType))
            isAlternate = new Random().nextDouble() <= customBlock.regenAlternativeChance();
        alternateRolls.put(block, isAlternate);
        customBlockMap.put(block, customBlock);

        int regenTime = isAlternate ? customBlock.regenAlternativeTime() : customBlock.regenTime();

        int delay = isAlternate ? customBlock.regenAlternativeDelay() : customBlock.regenDelay();
        if (delay > 0) { // Start with delay

            AdvancedMining.getInstance().getServer().getScheduler().runTaskLater(AdvancedMining.getInstance(),
                task -> startRegen(block, customBlock, regenTime),
                delay
            );

        } else startRegen(block, customBlock, regenTime); // Start with no delay

        PersistentDataContainer pdc = block.getWorld().getPersistentDataContainer();
        if (!pdc.has(REGENERATING_BLOCKS_LIST_KEY)) pdc.set(REGENERATING_BLOCKS_LIST_KEY, PersistentDataType.TAG_CONTAINER, pdc.getAdapterContext().newPersistentDataContainer());
        PersistentDataContainer blocksPdc = pdc.get(REGENERATING_BLOCKS_LIST_KEY, PersistentDataType.TAG_CONTAINER);
        if (blocksPdc == null) return;

        PersistentDataContainer newPdc = blocksPdc.getAdapterContext().newPersistentDataContainer();
        newPdc.set(REGEN_TIME, PersistentDataType.INTEGER, regenTime);
        newPdc.set(IS_ALTERNATE, PersistentDataType.BOOLEAN, isAlternate);
        newPdc.set(BLOCK, PersistentDataType.STRING, customBlock.id());
        blocksPdc.set(BlockDataStorage.getBockKey(block), PersistentDataType.TAG_CONTAINER, newPdc);

    }

    public static void startRegen(Block block, @NonNull CustomBlock customBlock, int regenTime) {

        if (regenTime == 0) {
            regenBlock(block);
            return;
        }

        // Set temporary blocks
        if ("vanilla".equals(customBlock.regenTempBlockType())) block.setType(customBlock.regenTempVanillaMaterial());
        else CustomBlock.setCustomBlock(block, customBlock.regenTempCustomBlock());

        if (regenTime < 0) return; // If specified, don't set blocks back

        activeBlocks.put(block, regenTime);

    }

    public static void regenBlock(@NonNull Block block) {

        CustomBlock customBlock = customBlockMap.get(block);
        if (customBlock == null) return;
        boolean isAlternate = alternateRolls.getOrDefault(block, false);

        String regenType = customBlock.blockRegenType();
        String altRegenType = customBlock.regenAlternativeType();

        if (isAlternate) {

            if ("vanilla".equalsIgnoreCase(altRegenType)) block.setType(customBlock.regenAltVanillaMaterial());
            else CustomBlock.setCustomBlock(block, customBlock.regenAltCustomBlock());

        } else {

            if ("vanilla".equalsIgnoreCase(regenType)) block.setType(customBlock.regenVanillaMaterial());
            else CustomBlock.setCustomBlock(block, customBlock.regenCustomBlock());

        }

        customBlockMap.remove(block);
        alternateRolls.remove(block);

        PersistentDataContainer pdc = block.getWorld().getPersistentDataContainer();
        if (!pdc.has(REGENERATING_BLOCKS_LIST_KEY)) pdc.set(REGENERATING_BLOCKS_LIST_KEY, PersistentDataType.TAG_CONTAINER, pdc.getAdapterContext().newPersistentDataContainer());
        PersistentDataContainer blocksPdc = pdc.get(REGENERATING_BLOCKS_LIST_KEY, PersistentDataType.TAG_CONTAINER);
        if (blocksPdc == null) return;
        blocksPdc.remove(BlockDataStorage.getBockKey(block));

    }

    @Override
    public void run() {

        activeBlocks.forEach(((block, time) -> {

            if (time <= 0) {
                regenBlock(block);
                activeBlocks.remove(block);
            }
            activeBlocks.replace(block, time - 1);

        }));

    }

}
