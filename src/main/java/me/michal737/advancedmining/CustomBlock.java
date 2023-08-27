package me.michal737.advancedmining;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class CustomBlock {

    private String name;
    private int strength, resistance, time;
    private Material material;
    private BreakType breakType;
    private List<blockDrop> drops;
    private Material replacementMaterial;

    /**
     * @param name The name of the block
     * @param strength Defines the time it take to mine the block
     * @param resistance Defines how resistant the block is (how much breaking power is needed to break it)
     * @param material The material of the block
     * @param breakType What happens when the block is broken
     * @param drops The items the block drops
     */
    public CustomBlock(String name, int strength, int resistance, Material material, BreakType breakType, List<blockDrop> drops, int time, Material replacementMaterial) {

        this.name = name;
        this.strength = strength;
        this.resistance = resistance;
        this.material = material;
        this.drops = drops;
        this.breakType = breakType;
        this.time = time;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getResistance() {
        return resistance;
    }

    public void setResistance(int resistance) {
        this.resistance = resistance;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public List<blockDrop> getDrops() {
        return drops;
    }

    public void setDrops(List<blockDrop> drops) {
        this.drops = drops;
    }

    public BreakType getBreakType() {
        return breakType;
    }

    public void setBreakType(BreakType breakType) {
        this.breakType = breakType;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomBlock that = (CustomBlock) o;
        return strength == that.strength && resistance == that.resistance && Objects.equals(name, that.name) && material == that.material && Objects.equals(drops, that.drops) && breakType == that.breakType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, strength, resistance, material, drops, breakType);
    }

    public enum BreakType{

        BREAK(),
        REPLACE(),
        BREAK_TEMPORARILY(),
        REPLACE_TEMPORARILY()

    }

    public static class blockDrop{

        private byte[] item;
        private int minAmount, maxAmount, chance;

        public blockDrop(byte[] item, int minAmount, int maxAmount, int chance) {
            this.item = item;
            this.minAmount = minAmount;
            this.maxAmount = maxAmount;
            this.chance = chance;
        }

        public ItemStack getItem() {
            return ItemStack.deserializeBytes(item);
        }

        public void setItem(@NotNull ItemStack item) {
            this.item = item.serializeAsBytes();
        }

        public byte[] getItemBytes(){
            return item;
        }

        public void setItemBytes(byte[] item){
            this.item = item;
        }

        public int getMinAmount() {
            return minAmount;
        }

        public void setMinAmount(int minAmount) {
            this.minAmount = minAmount;
        }

        public int getMaxAmount() {
            return maxAmount;
        }

        public void setMaxAmount(int maxAmount) {
            this.maxAmount = maxAmount;
        }

        public int getChance() {
            return chance;
        }

        public void setChance(int chance) {
            this.chance = chance;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            blockDrop blockDrop = (blockDrop) o;
            return minAmount == blockDrop.minAmount && maxAmount == blockDrop.maxAmount && chance == blockDrop.chance && Arrays.equals(item, blockDrop.item);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(minAmount, maxAmount, chance);
            result = 31 * result + Arrays.hashCode(item);
            return result;
        }

    }

}
