package win.codingboulder.advancedmining.mechanics;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import win.codingboulder.advancedmining.AdvancedMining;
import win.codingboulder.advancedmining.BlockDataStorage;
import win.codingboulder.advancedmining.CustomBlock;
import win.codingboulder.advancedmining.PlayerStats;
import win.codingboulder.advancedmining.api.CustomBlockBreakEvent;
import win.codingboulder.advancedmining.api.CustomBlockBreakProgressEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
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

    private PlayerStats playerStats;
    private int randomId;
    private float miningProgress;
    private final BossBar progressbar;
    private final Component barName;
    private ItemStack tool;
    private int tick;
    private int pauseTicks;

    private boolean isCanceled;
    private boolean instaMine;
    private boolean isPaused;
    private float lastState;

    public MiningRunnable(Block block, @NotNull CustomBlock customBlock, Player player, float miningSpeed, int breakingPower) {

        this.block = block;
        this.customBlock = customBlock;
        this.player = player;
        this.miningSpeed = miningSpeed;
        this.breakingPower = breakingPower;

        randomId = new Random().nextInt();
        miningProgress = customBlock.strength();
        instaMine = miningSpeed >= customBlock.strength();

        barName = Component.empty().append(customBlock.name()).append(Component.text(" - ", NamedTextColor.GRAY));
        progressbar = BossBar.bossBar(barName.append(Component.text("0.0%", NamedTextColor.WHITE)), 0f, AdvancedMining.Config.progressBarColor, BossBar.Overlay.NOTCHED_10);
        if (!instaMine && AdvancedMining.Config.showProgressBar && player != null) player.showBossBar(progressbar);

    }

    public MiningRunnable(Block block, CustomBlock customBlock, Player player, @NonNull PlayerStats playerStats) {

        this(block, customBlock, player, playerStats.miningSpeed(), playerStats.breakingPower());
        this.playerStats = playerStats;

    }

    @Override
    public void run() {

        if (isCanceled) {this.cancel(); return;}
        if (isPaused) {
            if (pauseTicks >= AdvancedMining.Config.miningProgressResetTime) {stopMining(); MiningEvents.miningRunnables.get(player).remove(block);}
            pauseTicks++;
            return;
        }

        if (miningProgress <= 0 || instaMine) {
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
        if (playerStats == null) playerStats = new PlayerStats(player);

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
        if (blockDrops != null) {
            ItemStack[] drops = blockDrops.rollDrops(playerStats);
            if (playerStats.hasTelekinesis() && player != null) player.getInventory().addItem(drops)
                .forEach((integer, item) -> block.getWorld().dropItemNaturally(block.getLocation().add(0.5, 0.5, 0.5), item)); // If not enough space, drop items on the ground
            else for (ItemStack item : drops) block.getWorld().dropItemNaturally(block.getLocation().add(0.5, 0.5, 0.5), item);
        }

        if (customBlock.minXpDrop() > 0) {
            ExperienceOrb orb = block.getWorld().createEntity(block.getLocation(), ExperienceOrb.class);
            orb.setExperience(new Random().nextInt(customBlock().minXpDrop() + playerStats.minXpDropBonus(), customBlock.maxXpDrop()+1 + playerStats.maxXpDropBonus()));
            orb.spawnAt(block.getLocation().toCenterLocation());
        }

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

    public int randomId() {
        return randomId;
    }

    public void setRandomId(int randomId) {
        this.randomId = randomId;
    }

    public void setMiningProgress(float miningProgress) {
        this.miningProgress = miningProgress;
    }

    public BossBar progressbar() {
        return progressbar;
    }

    public int tick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    public int pauseTicks() {
        return pauseTicks;
    }

    public void setPauseTicks(int pauseTicks) {
        this.pauseTicks = pauseTicks;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public boolean instaMine() {
        return instaMine;
    }

    public void setInstaMine(boolean instaMine) {
        this.instaMine = instaMine;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public float lastState() {
        return lastState;
    }

    public PlayerStats playerStats() {
        return playerStats;
    }

    public void setPlayerStats(PlayerStats playerStats) {
        this.playerStats = playerStats;
        this.miningSpeed = playerStats().miningSpeed();
    }
}
