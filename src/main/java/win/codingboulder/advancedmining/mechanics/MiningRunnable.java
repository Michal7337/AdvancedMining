package win.codingboulder.advancedmining.mechanics;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import win.codingboulder.advancedmining.CustomBlock;

public class MiningRunnable extends BukkitRunnable {

    private final Block block;
    private final CustomBlock customBlock;
    private final Player player;
    private final float miningSpeed;
    private final int breakingPower;

    private float miningProgress;

    public boolean isCanceled;

    public MiningRunnable(Block block, CustomBlock customBlock, Player player, float miningSpeed, int breakingPower) {

        this.block = block;
        this.customBlock = customBlock;
        this.player = player;
        this.miningSpeed = miningSpeed;
        this.breakingPower = breakingPower;

        miningProgress = customBlock.strength();

    }

    private float lastState;

    @Override
    public void run() {

        if (isCanceled) {this.cancel(); return;}

    }

    public Block block() {
        return block;
    }

    public CustomBlock customBlock() {
        return customBlock;
    }

    public Player player() {
        return player;
    }

    public float miningSpeed() {
        return miningSpeed;
    }

    public int breakingPower() {
        return breakingPower;
    }

    public float miningProgress() {
        return miningProgress;
    }
}
