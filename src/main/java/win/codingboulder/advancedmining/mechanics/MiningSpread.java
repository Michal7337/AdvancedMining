package win.codingboulder.advancedmining.mechanics;

import org.bukkit.block.Block;
import win.codingboulder.advancedmining.AdvancedMining;

import java.util.Random;

public class MiningSpread {

    public static void runMiningSpread(MiningRunnable runnable) {

        if (!AdvancedMining.Config.miningSpreadEnable) return;

        float miningSpreadFraction = runnable.playerStats().miningSpread() / AdvancedMining.Config.miningSpreadDivisor;
        int miningSpreadBlocks = (int) miningSpreadFraction;
        if (new Random().nextDouble() <= miningSpreadFraction - miningSpreadBlocks) miningSpreadBlocks++;

        if (miningSpreadBlocks <= 0) return;

        Block block = runnable.block();



    }



}
