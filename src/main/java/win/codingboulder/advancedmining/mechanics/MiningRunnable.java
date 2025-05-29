package win.codingboulder.advancedmining.mechanics;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import win.codingboulder.advancedmining.BlockDataStorage;
import win.codingboulder.advancedmining.CustomBlock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Random;

public class MiningRunnable extends BukkitRunnable {

    private final Block block;
    private final CustomBlock customBlock;
    private final Player player;
    private final float miningSpeed;
    private final int breakingPower;

    public int randomId;
    private final DecimalFormat decimalFormat;
    private float miningProgress;

    public boolean isCanceled;

    public MiningRunnable(Block block, CustomBlock customBlock, Player player, float miningSpeed, int breakingPower) {

        this.block = block;
        this.customBlock = customBlock;
        this.player = player;
        this.miningSpeed = miningSpeed;
        this.breakingPower = breakingPower;

        randomId = new Random().nextInt();
        miningProgress = customBlock.strength();
        decimalFormat = new DecimalFormat("#.#");
        decimalFormat.setRoundingMode(RoundingMode.CEILING);

    }

    private float lastState;

    @Override
    public void run() {

        if (isCanceled) {this.cancel(); Events.miningRunnables.remove(player); return;}

        if (miningProgress <= 0) {
            breakBlock();
            this.cancel();
            Events.miningRunnables.remove(player);
            return;
        }

        float breakFraction = 1 - miningProgress / customBlock.strength();
        float breakStage = Float.parseFloat(decimalFormat.format(breakFraction));
        if (breakStage > lastState) {
            player.sendBlockDamage(block.getLocation(), breakStage, randomId);
            lastState = breakStage;
        }

        //player.sendRichMessage("progress: " + miningProgress + " fraction: " + breakFraction + " stage: " + breakStage);

        miningProgress -= miningSpeed;

    }

    public void breakBlock() {

        block.breakNaturally(true, false);
        player.sendBlockDamage(block.getLocation(), 0f, randomId);

        BlockDataStorage.editContainer(block, pdc -> pdc.remove(CustomBlock.blockIdKey));

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
