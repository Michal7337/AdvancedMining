package win.codingboulder.advancedmining.mechanics;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.jspecify.annotations.NonNull;
import win.codingboulder.advancedmining.AdvancedMining;
import win.codingboulder.advancedmining.CustomBlock;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.function.Consumer;

public class MiningSpread {

    public static void runMiningSpread(@NonNull MiningRunnable runnable) {

        float miningSpread = runnable.playerStats().miningSpread();
        if (miningSpread > AdvancedMining.Config.miningSpreadCap) miningSpread = AdvancedMining.Config.miningSpreadCap;

        if (miningSpread <= 0) return;

        CustomBlock brokenCustomBlock = runnable.customBlock();
        float miningSpreadResistance = Math.max(brokenCustomBlock.miningSpreadResistance(), 1);
        miningSpread /= miningSpreadResistance;

        float miningSpreadFraction = miningSpread / AdvancedMining.Config.miningSpreadDivisor;
        int miningSpreadBlocks = (int) miningSpreadFraction;
        if (new Random().nextDouble() <= miningSpreadFraction - miningSpreadBlocks) miningSpreadBlocks++;

        if (miningSpreadBlocks <= 0) return;

        Block brokenBlock = runnable.block();

        ArrayList<Block> validBlocks = new ArrayList<>();

        int distance = 1;
        while (true) {

            ArrayList<Block> blocksInLayer = new ArrayList<>();
            iterateBlockLayer(brokenBlock, distance, block -> { // Get every matching Custom Block in the current layer

                if (block.isEmpty()) return;

                CustomBlock customBlock = CustomBlock.getCustomBlock(block);
                if (customBlock == null) return;
                if (customBlock.id().equals(brokenCustomBlock.id()) || brokenCustomBlock.miningSpreadCompatibleBlocks().contains(customBlock.id())) blocksInLayer.add(block);

            });

            if (blocksInLayer.isEmpty()) break; // If the layer contains no matching blocks, end the chain

            if (validBlocks.size() + blocksInLayer.size() == miningSpreadBlocks) { // If the layer has exactly enough, add them

                validBlocks.addAll(blocksInLayer);
                break;

            } else if (validBlocks.size() + blocksInLayer.size() > miningSpreadBlocks) { // If the layer has more than enough, remove random ones until it's good

                int overAmount = (validBlocks.size() + blocksInLayer.size()) - miningSpreadBlocks;
                while (overAmount > 0) {
                    blocksInLayer.remove(new Random().nextInt(0, blocksInLayer.size()));
                    overAmount--;
                }
                validBlocks.addAll(blocksInLayer);
                break;

            } else { // If the layer has too little blocks, check the next layer
                validBlocks.addAll(blocksInLayer);
                distance++;
            }

        }

        validBlocks.forEach(block -> {

            MiningRunnable miningRunnable = new MiningRunnable(block, CustomBlock.getCustomBlock(block), runnable.player(), runnable.playerStats());
            miningRunnable.breakBlock();

            LinkedHashMap<Block, MiningRunnable> playerRunnables = MiningEvents.miningRunnables.get(runnable.player());
            if (playerRunnables == null) return;
            MiningRunnable playerRunnable = playerRunnables.get(block);
            if (playerRunnable == null) return;
            playerRunnable.stopMining();
            playerRunnables.remove(block);

        });

    }

    public static void iterateBlockLayer(@NonNull Block center, int distance, Consumer<Block> action) {

        World world = center.getWorld();
        Block corner1 = center.getRelative(-distance, -distance, -distance);
        Block corner2 = center.getRelative(distance, distance, distance);

        int minX = Math.min(corner1.getX(), corner2.getX()), maxX = Math.max(corner1.getX(), corner2.getX());
        int minY = Math.min(corner1.getY(), corner2.getY()), maxY = Math.max(corner1.getY(), corner2.getY());
        int minZ = Math.min(corner1.getZ(), corner2.getZ()), maxZ = Math.max(corner1.getZ(), corner2.getZ());

        // Y Sides
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                action.accept(world.getBlockAt(x, minY, z));
                action.accept(world.getBlockAt(x, maxY, z));
            }
        }

        // Z sides excluding previous
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY + 1; y <= maxY - 1; y++) {
                action.accept(world.getBlockAt(x, y, minZ));
                action.accept(world.getBlockAt(x, y, maxZ));
            }
        }

        // X sides excluding previous
        for (int z = minZ + 1; z <= maxZ - 1; z++) {
            for (int y = minY + 1; y <= maxY - 1; y++) {
                action.accept(world.getBlockAt(minX, y, z));
                action.accept(world.getBlockAt(maxX, y, z));
            }
        }
    }

}
