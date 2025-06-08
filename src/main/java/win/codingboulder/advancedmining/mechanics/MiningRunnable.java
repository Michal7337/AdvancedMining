package win.codingboulder.advancedmining.mechanics;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import win.codingboulder.advancedmining.BlockDataStorage;
import win.codingboulder.advancedmining.CustomBlock;
import win.codingboulder.advancedmining.api.CustomBlockBreakEvent;

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
    public BossBar progressbar;
    Component barName;

    public boolean isCanceled;

    public MiningRunnable(Block block, @NotNull CustomBlock customBlock, Player player, float miningSpeed, int breakingPower) {

        this.block = block;
        this.customBlock = customBlock;
        this.player = player;
        this.miningSpeed = miningSpeed;
        this.breakingPower = breakingPower;

        randomId = new Random().nextInt();
        miningProgress = customBlock.strength();
        decimalFormat = new DecimalFormat("#.#");
        decimalFormat.setRoundingMode(RoundingMode.CEILING);

        barName = customBlock.name().append(Component.text(" - ", NamedTextColor.GRAY));
        progressbar = BossBar.bossBar(barName, 0f, BossBar.Color.BLUE, BossBar.Overlay.NOTCHED_10);
        player.showBossBar(progressbar);

    }

    private float lastState;

    @Override
    public void run() {

        if (isCanceled) {this.cancel(); MiningEvents.miningRunnables.remove(player); return;}
        if (miningSpeed >= customBlock.strength()) {breakBlock(); this.cancel(); MiningEvents.miningRunnables.remove(player); return;} //check if instamine

        if (miningProgress <= 0) {
            breakBlock();
            this.cancel();
            MiningEvents.miningRunnables.remove(player);
            return;
        }

        float breakFraction = 1 - miningProgress / customBlock.strength();
        float breakStage = Float.parseFloat(decimalFormat.format(breakFraction));
        if (breakStage > lastState) {
            player.sendBlockDamage(block.getLocation(), breakStage, randomId);
            lastState = breakStage;
        }

        float barPercent = BigDecimal.valueOf(breakFraction).movePointRight(2).setScale(1, RoundingMode.HALF_UP).floatValue();
        progressbar.name(barName.append(Component.text(barPercent + "%", NamedTextColor.WHITE)));
        progressbar.progress(breakFraction);

        miningProgress -= miningSpeed;

    }

    public void breakBlock() {

        player.sendBlockDamage(block.getLocation(), 0f, randomId);
        player.hideBossBar(progressbar);

        BlockDrops blockDrops = BlockDrops.loadedDrops.get(customBlock.rawDropsFile());

        CustomBlockBreakEvent blockBreakEvent = new CustomBlockBreakEvent(player, block, customBlock, blockDrops, true, true);
        if (!blockBreakEvent.callEvent()) return;

        if (blockBreakEvent.playBreakEffect()) block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getBlockData());
        block.setType(Material.AIR);

        blockDrops = blockBreakEvent.blockDrops();
        if (blockDrops != null)
            for (ItemStack item : blockDrops.rollDrops()) block.getWorld().dropItemNaturally(block.getLocation().add(0.5, 0.5, 0.5), item);

        if (blockBreakEvent.removeBlockData()) BlockDataStorage.editContainer(block, pdc -> pdc.remove(CustomBlock.blockIdKey));

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
