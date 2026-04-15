package win.codingboulder.advancedmining.mechanics;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import win.codingboulder.advancedmining.AdvancedMining;
import win.codingboulder.advancedmining.BlockDataStorage;
import win.codingboulder.advancedmining.CustomBlock;
import win.codingboulder.advancedmining.api.CustomBlockBreakEvent;
import win.codingboulder.advancedmining.api.CustomBlockBreakProgressEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

/**
 * This runnable is responsible for the block mining process. Instantiating it does not start the mining process, to do that you need to schedule it.
 */
public class MiningRunnable extends BukkitRunnable {

    private final Block block;
    private final CustomBlock customBlock;
    private final Player player;
    private float miningSpeed;
    private final int breakingPower;

    public int randomId;
    private float miningProgress;
    public BossBar progressbar;
    private final Component barName;
    private ItemStack tool;
    public int tick;
    public int pauseTicks;

    public boolean isCanceled;
    public boolean instaMine;
    public boolean isPaused;

    public MiningRunnable(Block block, @NotNull CustomBlock customBlock, Player player, float miningSpeed, int breakingPower) {

        this.block = block;
        this.customBlock = customBlock;
        this.player = player;
        this.miningSpeed = miningSpeed;
        this.breakingPower = breakingPower;

        randomId = new Random().nextInt();
        miningProgress = customBlock.strength();
        instaMine = miningSpeed >= customBlock.strength();

        barName = customBlock.name().append(Component.text(" - ", NamedTextColor.GRAY));
        progressbar = BossBar.bossBar(barName.append(Component.text("0.0%", NamedTextColor.WHITE)), 0f, AdvancedMining.Config.progressBarColor, BossBar.Overlay.NOTCHED_10);
        if (!instaMine && AdvancedMining.Config.showProgressBar && player != null) player.showBossBar(progressbar);

    }

    private float lastState;

    @Override
    public void run() {

        if (isCanceled) {this.cancel(); return;}
        if (isPaused) {
            if (pauseTicks >= AdvancedMining.Config.miningProgressResetTime) {stopMining(); MiningEvents.miningRunnables.get(player).remove(block);}
            pauseTicks++;
            return;
        }
        if (instaMine) {breakBlock(); this.cancel(); MiningEvents.miningRunnables.get(player).remove(block); return;}

        if (miningProgress <= 0) {
            breakBlock();
            this.cancel();
            MiningEvents.miningRunnables.get(player).remove(block);
            return;
        }

        float breakFraction = 1 - miningProgress / customBlock.strength();
        float breakStage = (float) (Math.ceil(breakFraction * 10) / 10);

        CustomBlockBreakProgressEvent event = new CustomBlockBreakProgressEvent(player, block, customBlock, miningProgress, miningSpeed, breakStage, tick);
        if (!event.callEvent()) {tick++; return;}

        miningProgress = event.progress();
        breakStage = event.breakStage();

        if (breakStage != lastState) {
            int range = AdvancedMining.Config.crackingAnimationRange;
            if (player != null) {
                player.sendBlockDamage(block.getLocation(), breakStage, randomId);
                for (Entity entity : player.getNearbyEntities(range, range, range)) if (entity instanceof Player pl) pl.sendBlockDamage(block.getLocation(), breakStage, randomId);
            }
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
            int range = AdvancedMining.Config.crackingAnimationRange;
            for (Entity entity : player.getNearbyEntities(range, range, range)) if (entity instanceof Player pl) pl.sendBlockDamage(block.getLocation(), 0, randomId);
            player.hideBossBar(progressbar);
        }

        BlockDrops blockDrops = BlockDrops.loadedDrops().get(customBlock.rawDropsFile());

        CustomBlockBreakEvent blockBreakEvent = new CustomBlockBreakEvent(player, block, customBlock, blockDrops, true, true);
        if (!blockBreakEvent.callEvent()) return;

        ItemDisplay display = CustomBlock.getDisplayEntity(block);
        BlockData blockData = display == null ? block.getBlockData() : customBlock.iconMaterial().createBlockData();
        if (display != null) display.remove();

        if (blockBreakEvent.playBreakEffect()) {
            block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, blockData);
            if (customBlock.breakSound() != null) block.getWorld().playSound(Sound.sound(customBlock.breakSound(), Sound.Source.BLOCK, 16, 1), block.getX(), block.getY(), block.getZ());
        }
        block.setType(Material.AIR);

        blockDrops = blockBreakEvent.blockDrops();
        if (blockDrops != null)
            for (ItemStack item : blockDrops.rollDrops(tool)) block.getWorld().dropItemNaturally(block.getLocation().add(0.5, 0.5, 0.5), item);

        if (blockBreakEvent.removeBlockData()) BlockDataStorage.editDataContainer(block, pdc -> pdc.remove(CustomBlock.blockIdKey));

        BlockRegenSystem.registerBlock(block, customBlock);

    }

    public void stopMining() {
        this.isCanceled = true;
        player.hideBossBar(progressbar);
        player.sendBlockDamage(block.getLocation(), 0f, randomId);
        int range = AdvancedMining.Config.crackingAnimationRange;
        for (Entity entity : player.getNearbyEntities(range, range, range)) if (entity instanceof Player pl) pl.sendBlockDamage(block.getLocation(), 0f, randomId);
    }

    public void pauseMining() {
        this.isPaused = true;
        player.hideBossBar(progressbar);
    }

    public void unpauseMining() {
        this.isPaused = false;
        pauseTicks = 0;
        player.showBossBar(progressbar);
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

    public void setMiningSpeed(float miningSpeed) {
        this.miningSpeed = miningSpeed;
    }

    public int breakingPower() {
        return breakingPower;
    }

    public float miningProgress() {
        return miningProgress;
    }

    public ItemStack tool() {
        return tool;
    }

    public void setTool(@NonNull ItemStack tool) {
        this.tool = tool.clone();
    }

}
