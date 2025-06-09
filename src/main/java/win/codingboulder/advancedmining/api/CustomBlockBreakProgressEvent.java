package win.codingboulder.advancedmining.api;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import win.codingboulder.advancedmining.CustomBlock;

public class CustomBlockBreakProgressEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private boolean cancelled;

    private final Player player;
    private final Block block;
    private final CustomBlock customBlock;
    private final int tick;
    private float progress;
    private float tickProgress;
    private float breakStage;

    public CustomBlockBreakProgressEvent(Player player, Block block, CustomBlock customBlock, float progress, float tickProgress, float breakStage, int tick) {

        this.player = player;
        this.block = block;
        this.customBlock = customBlock;
        this.progress = progress;
        this.tickProgress = tickProgress;
        this.breakStage = breakStage;
        this.tick = tick;

    }

    public Player getPlayer() {
        return player;
    }

    public Block getBlock() {
        return block;
    }

    public CustomBlock getCustomBlock() {
        return customBlock;
    }

    public float progress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public float tickProgress() {
        return tickProgress;
    }

    public void setTickProgress(float tickProgress) {
        this.tickProgress = tickProgress;
    }

    public float breakStage() {
        return breakStage;
    }

    public void setBreakStage(float breakStage) {
        this.breakStage = breakStage;
    }

    public int tick() {
        return tick;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

}
