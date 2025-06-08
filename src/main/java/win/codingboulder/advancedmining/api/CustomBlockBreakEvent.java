package win.codingboulder.advancedmining.api;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import win.codingboulder.advancedmining.CustomBlock;
import win.codingboulder.advancedmining.mechanics.BlockDrops;

public class CustomBlockBreakEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private boolean cancelled;

    private final Player player;
    private final Block block;
    private final CustomBlock customBlock;
    private BlockDrops blockDrops;
    private boolean playBreakEffect;
    private boolean removeBlockData;

    public CustomBlockBreakEvent(Player player, Block block, CustomBlock customBlock, BlockDrops blockDrops, boolean playBreakEffect, boolean removeBlockData) {

        this.player = player;
        this.block = block;
        this.customBlock = customBlock;
        this.blockDrops = blockDrops;
        this.playBreakEffect = playBreakEffect;
        this.removeBlockData = removeBlockData;

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

    public BlockDrops blockDrops() {
        return blockDrops;
    }

    public void setBlockDrops(BlockDrops blockDrops) {
        this.blockDrops = blockDrops;
    }

    public boolean playBreakEffect() {
        return playBreakEffect;
    }

    public void setPlayBreakEffect(boolean playBreakEffect) {
        this.playBreakEffect = playBreakEffect;
    }

    public boolean removeBlockData() {
        return removeBlockData;
    }

    public void setRemoveBlockData(boolean removeBlockData) {
        this.removeBlockData = removeBlockData;
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
