package win.codingboulder.advancedmining.mechanics;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import win.codingboulder.advancedmining.AdvancedMining;
import win.codingboulder.advancedmining.BlockDataStorage;
import win.codingboulder.advancedmining.CustomBlock;
import win.codingboulder.advancedmining.api.CustomBlockBreakEvent;
import win.codingboulder.advancedmining.api.CustomBlockBreakProgressEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Random;

/**
 * This runnable is responsible for the block mining process. Instantiating it does not start the mining process, to do that you need to schedule it.
 */
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
    private final Component barName;
    public int tick;

    public boolean isCanceled;
    public boolean instaMine;

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
        instaMine = miningSpeed >= customBlock.strength();

        barName = customBlock.name().append(Component.text(" - ", NamedTextColor.GRAY));
        progressbar = BossBar.bossBar(barName.append(Component.text("0.0%", NamedTextColor.WHITE)), 0f, BossBar.Color.BLUE, BossBar.Overlay.NOTCHED_10);
        if (!instaMine && AdvancedMining.showProgressBar && player != null) player.showBossBar(progressbar);

    }

    private float lastState;

    @Override
    public void run() {

        if (isCanceled) {this.cancel(); return;}
        if (instaMine) {breakBlock(); this.cancel(); MiningEvents.miningRunnables.remove(player); return;}

        if (miningProgress <= 0) {
            breakBlock();
            this.cancel();
            MiningEvents.miningRunnables.remove(player);
            return;
        }

        float breakFraction = 1 - miningProgress / customBlock.strength();
        float breakStage = Float.parseFloat(decimalFormat.format(breakFraction));

        CustomBlockBreakProgressEvent event = new CustomBlockBreakProgressEvent(player, block, customBlock, miningProgress, miningSpeed, breakStage, tick);
        if (!event.callEvent()) {tick++; return;}

        miningProgress = event.progress();
        breakStage = event.breakStage();

        if (breakStage != lastState) {
            if (player != null) player.sendBlockDamage(block.getLocation(), breakStage, randomId);
            lastState = breakStage;
        }

        float barPercent = BigDecimal.valueOf(breakFraction).movePointRight(2).setScale(1, RoundingMode.HALF_UP).floatValue();
        progressbar.name(barName.append(Component.text(barPercent + "%", NamedTextColor.WHITE)));
        progressbar.progress(breakFraction);

        miningProgress -= event.tickProgress();
        tick ++;

    }

    public void breakBlock() {

        if (player != null) {
            player.sendBlockDamage(block.getLocation(), 0f, randomId);
            player.hideBossBar(progressbar);
        }

        BlockDrops blockDrops = BlockDrops.loadedDrops().get(customBlock.rawDropsFile());

        CustomBlockBreakEvent blockBreakEvent = new CustomBlockBreakEvent(player, block, customBlock, blockDrops, true, true);
        if (!blockBreakEvent.callEvent()) return;

        ItemDisplay display = CustomBlock.getDisplayEntity(block);
        BlockData blockData = display == null ? block.getBlockData() : customBlock.iconMaterial().createBlockData();
        if (display != null) display.remove();

        if (blockBreakEvent.playBreakEffect()) block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, blockData);
        block.setType(Material.AIR);

        blockDrops = blockBreakEvent.blockDrops();
        if (blockDrops != null)
            for (ItemStack item : blockDrops.rollDrops()) block.getWorld().dropItemNaturally(block.getLocation().add(0.5, 0.5, 0.5), item);

        if (blockBreakEvent.removeBlockData()) BlockDataStorage.editDataContainer(block, pdc -> pdc.remove(CustomBlock.blockIdKey));

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
