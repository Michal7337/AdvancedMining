package win.codingboulder.advancedmining.mechanics;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import win.codingboulder.advancedmining.AdvancedMining;

import java.io.*;
import java.util.*;

public class BlockDrops implements Serializable {

    public static final HashMap<String, BlockDrops> loadedDrops = new HashMap<>();

    private String id;
    private final ArrayList<Entry> entries = new ArrayList<>();

    public BlockDrops(String id) {
        this.id = id;
    }

    public ItemStack[] rollDrops() {

        ArrayList<ItemStack> droppedItems = new ArrayList<>();

        for (Entry entry : entries)
            if (new Random().nextDouble() <= entry.chance)
                droppedItems.addAll(List.of(getCostItemsArray(entry.itemStack, entry.amount)));

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
            throw new RuntimeException();
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

    public static class Entry {

        private transient ItemStack itemStack;
        private int amount;
        private float chance;
        private byte[] item;

        public Entry(@NotNull ItemStack itemStack, int amount, float chance) {
            this.itemStack = itemStack;
            this.amount = amount;
            this.chance = chance;
            this.item = itemStack.serializeAsBytes();
        }

        public ItemStack item() {
            return itemStack;
        }

        public void setItem(ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        public int amount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
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
