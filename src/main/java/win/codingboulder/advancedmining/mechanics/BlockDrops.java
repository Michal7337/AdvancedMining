package win.codingboulder.advancedmining.mechanics;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import win.codingboulder.advancedmining.AdvancedMining;

import java.io.*;
import java.util.*;

public class BlockDrops implements Serializable {

    @Serial private static final long serialVersionUID = 4598191079417030808L;

    public static final HashMap<String, BlockDrops> loadedDrops = new HashMap<>();

    private String id;
    private final ArrayList<Entry> entries = new ArrayList<>();

    public BlockDrops(String id) {
        this.id = id;
    }

    public static @NotNull BlockDrops singleDrop(String id, ItemStack item, int minAmount, int maxAmount, float chance) {
        BlockDrops blockDrops = new BlockDrops(id);
        blockDrops.entries.add(new Entry(item, minAmount, maxAmount, chance));
        return blockDrops;
    }

    public ItemStack[] rollDrops() {

        ArrayList<ItemStack> droppedItems = new ArrayList<>();

        for (Entry entry : entries)
            if (new Random().nextDouble() <= entry.chance)
                droppedItems.addAll(List.of(getCostItemsArray(entry.itemStack, new Random().nextInt(entry.minAmount, entry.maxAmount + 1))));

        return droppedItems.toArray(new ItemStack[]{});

    }

    public static ItemStack @NotNull [] getCostItemsArray(ItemStack item, int amount) {

        if (amount == 0) return new ItemStack[0];
        int stackSize = item.getMaxStackSize();
        ItemStack priceItem = item.asQuantity(stackSize);

        int fullStacks = amount / stackSize;
        int remainingItems = amount % stackSize;

        int stacks = remainingItems != 0 ? fullStacks + 1 : fullStacks;

        ItemStack[] items = new ItemStack[stacks];

        Arrays.fill(items, 0, fullStacks, priceItem);
        if (remainingItems > 0) items[fullStacks] = item.asQuantity(remainingItems);

        return items;

    }

    public void saveToFile() {

        File file = new File(AdvancedMining.blockDropsFolder, id + ".drops");

        try {
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(this);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static BlockDrops loadFromFile(File file) {

        try {

            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            return (BlockDrops) ois.readObject();

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public static void loadAll() {

        if (AdvancedMining.blockDropsFolder.listFiles() == null) return;
        for (File file : Objects.requireNonNull(AdvancedMining.blockDropsFolder.listFiles())) {
            BlockDrops blockDrops = BlockDrops.loadFromFile(file);
            if (blockDrops != null) loadedDrops.put(blockDrops.id, blockDrops);
        }

    }

    public String id() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Entry> entries() {
        return entries;
    }

    public static class Entry implements Serializable {

        private transient ItemStack itemStack;
        private int minAmount;
        private int maxAmount;
        private float chance;
        private byte[] item;

        public Entry(@NotNull ItemStack itemStack, int minAmount, int maxAmount, float chance) {
            this.itemStack = itemStack;
            this.minAmount = minAmount;
            this.maxAmount = maxAmount;
            this.chance = chance;
            this.item = itemStack.serializeAsBytes();
        }

        @Serial
        private void readObject(@NotNull ObjectInputStream stream) throws IOException, ClassNotFoundException {
            stream.defaultReadObject();
            itemStack = ItemStack.deserializeBytes(item);
        }

        public ItemStack item() {
            return itemStack;
        }

        public void setItem(ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        public int minAmount() {
            return minAmount;
        }

        public void setMinAmount(int minAmount) {
            this.minAmount = minAmount;
        }

        public int maxAmount() {
            return maxAmount;
        }

        public void setMaxAmount(int maxAmount) {
            this.maxAmount = maxAmount;
        }

        public float chance() {
            return chance;
        }

        public void setChance(float chance) {
            this.chance = chance;
        }

        public byte[] itemArray() {
            return item;
        }

        public void setItemArray(byte[] item) {
            this.item = item;
        }

    }

}
