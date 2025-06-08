package win.codingboulder.advancedmining.api;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import win.codingboulder.advancedmining.CustomBlock;

public class CustomBlockBreakStartEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private boolean cancelled;

    private final Player player;
    private final Block block;
    private CustomBlock customBlock;
    private float miningSpeed;
    private int breakingPower;
    private String toolType;

    public CustomBlockBreakStartEvent(Player player, Block block, CustomBlock customBlock, float miningSpeed, int breakingPower, String toolType) {

        this.player = player;
        this.block = block;
        this.customBlock = customBlock;
        this.miningSpeed = miningSpeed;
        this.breakingPower = breakingPower;
        this.toolType = toolType;

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

    public void setCustomBlock(CustomBlock customBlock) {
        this.customBlock = customBlock;
    }

    public float getMiningSpeed() {
        return miningSpeed;
    }

    public void setMiningSpeed(float miningSpeed) {
        this.miningSpeed = miningSpeed;
    }

    public int getBreakingPower() {
        return breakingPower;
    }

    public void setBreakingPower(int breakingPower) {
        this.breakingPower = breakingPower;
    }

    public String getToolType() {
        return toolType;
    }

    public void setToolType(String toolType) {
        this.toolType = toolType;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
