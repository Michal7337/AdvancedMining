package win.codingboulder.advancedmining.mechanics;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import win.codingboulder.advancedmining.AdvancedMining;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;

/**
 * Represents a list of items that can be dropped from a block.
 */
public class BlockDrops implements Serializable {

    @Serial private static final long serialVersionUID = 4598191079417030808L;

    private static final HashMap<String, BlockDrops> loadedDrops = new HashMap<>();

    private String id;
    private final ArrayList<Entry> entries = new ArrayList<>();
    private transient HashMap<String, Entry> entryMap = new HashMap<>();

    public BlockDrops(String id) {
        this.id = id;
    }

    public static @NotNull BlockDrops singleDrop(String id, ItemStack item, int minAmount, int maxAmount, float chance) {
        BlockDrops blockDrops = new BlockDrops(id);
        blockDrops.entries.add(new Entry("main", item, minAmount, maxAmount, chance));
        return blockDrops;
    }

    public static @NotNull BlockDrops singleDrop(String id, ItemStack itemStack, int minAmount, int maxAmount, float chance, boolean affectedByFortune, boolean silkTouchOnly, boolean noRollByDefault, ArrayList<String> extraDrops) {
        BlockDrops blockDrops = new BlockDrops(id);
        blockDrops.entries.add(new Entry("main", itemStack, minAmount, maxAmount, chance, affectedByFortune, silkTouchOnly, noRollByDefault, extraDrops));
        return blockDrops;
    }


    /**
     * A utility method for changing an entry's Id. It automatically updates the entryMap
     * @param targetId Id of the entry to change
     * @param newId New Id of the entry
     */
    public void changeEntryId(String targetId, String newId) {

        Entry entry = entryMap.get(targetId);
        if (entry == null) return;
        entry.setId(newId);
        entryMap.remove(targetId);
        entryMap.put(newId, entry);
        saveToFile();

    }

    /**
     * Iterates through all the entries and randomly rolls if they should be dropped and how many items should be dropped
     * @return The randomly rolled items
     */
    public ItemStack[] rollDrops() {

        ArrayList<ItemStack> droppedItems = new ArrayList<>();

        for (Entry entry : entries) { // for each entry

            if (!entry.noRollByDefault) { // if it can be rolled

                boolean extraDropSuccess = false;
                for (String extraDropId : entry.extraDrops) { // for each extra drop

                    Entry extraDrop = entryMap.get(extraDropId);
                    if (extraDrop == null) continue;
                    ArrayList<ItemStack> rolledDrops = extraDrop.roll(); // if it exists roll it
                    droppedItems.addAll(rolledDrops);
                    if (!rolledDrops.isEmpty()) {extraDropSuccess = true; break;} // if it succeeds end the loop

                }

                if (!extraDropSuccess) droppedItems.addAll(entry.roll()); // if the extra drops don't roll, roll the base drop

            }

        }

        return droppedItems.toArray(new ItemStack[0]);

    }

    /**
     * Iterates through all the entries and randomly rolls if they should be dropped and how many items should be dropped.
     * Takes into account the enchantments of the tool used
     * @return The randomly rolled items
     */
    public ItemStack[] rollDrops(ItemStack tool) {

        if (tool == null) return rollDrops();

        ArrayList<ItemStack> droppedItems = new ArrayList<>();

        if (AdvancedMining.Config.fortuneEnable) {

            int fortuneLevel = tool.getEnchantmentLevel(Enchantment.FORTUNE);

            if (AdvancedMining.Config.fortuneEffectType.equals("vanilla")) {

                if (AdvancedMining.Config.fortuneVanillaBehavior.equals("additional-rolls")) {

                    float normalDropChance = (float) 2 / (fortuneLevel+2);
                    boolean noBonus = new Random().nextDouble() <= normalDropChance;

                    int dropMultiplier = noBonus ? 1 : new Random().nextInt(2, fortuneLevel + 2); // If there is no bonus, roll once

                    droppedItems.addAll(rollDropsWithExtras(tool)); // Roll once normally
                    dropMultiplier--;

                    while (dropMultiplier > 0) {

                        droppedItems.addAll(rollDropsWithExtras(tool, true)); // Roll and skip entries that ignore fortune
                        dropMultiplier--;

                    }

                } else droppedItems.addAll(rollDropsWithExtras(tool)); //

            } else {

                int rolls = 1 + AdvancedMining.Config.fortuneDropRolls * fortuneLevel;
                while (rolls > 0) {

                    droppedItems.addAll(rollDropsWithExtras(tool));
                    rolls--;

                }

            }

        } else droppedItems.addAll(rollDropsWithExtras(tool));

        return droppedItems.toArray(new ItemStack[0]);

    }

    public ArrayList<ItemStack> rollDropsWithExtras(ItemStack tool) {
        return rollDropsWithExtras(tool, false);
    }

    public ArrayList<ItemStack> rollDropsWithExtras(ItemStack tool, boolean skipNoFortune) {

        ArrayList<ItemStack> droppedItems = new ArrayList<>();

        for (Entry entry : entries) { // for each entry

            if (!entry.noRollByDefault) { // if it can be rolled

                // If the entry ignores fortune, don't roll. Used for extra roll fortune behavior
                if (skipNoFortune && !entry.affectedByFortune) continue;

                boolean extraDropSuccess = false;
                for (String extraDropId : entry.extraDrops) { // for each extra drop

                    Entry extraDrop = entryMap.get(extraDropId);
                    if (extraDrop == null) continue;
                    ArrayList<ItemStack> rolledDrops = extraDrop.roll(tool); // if it exists roll it
                    droppedItems.addAll(rolledDrops);
                    if (!rolledDrops.isEmpty()) {extraDropSuccess = true; break;} // if it succeeds end the loop

                }

                if (!extraDropSuccess) droppedItems.addAll(entry.roll(tool)); // if the extra drops don't roll, roll the base drop

            }

        }

        return droppedItems;

    }

    /**
     * Makes an array of ItemStacks in which the amount of items equals the specified amount.<br>
     * E.g. If the ItemStack is a Gold Ingot and the amount is 130, the array will contain two stacks of 64 Ingots and one stack of 2 Ingots.
     * @param item The item to make stacks of
     * @param amount The amount of items
     * @return An array of ItemStacks with the total amount of items equal to the amount argument
     */
    public static ItemStack @NotNull [] getItemAmountArray(ItemStack item, int amount) {

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

    public static @Nullable BlockDrops loadFromFile(File file) {

        try {

            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            return (BlockDrops) ois.readObject();

        } catch (IOException e) {
            AdvancedMining.getInstance().getLogger().warning("Failed to load block drops '" + file.getName() + "' - IO exception!");
        } catch (ClassNotFoundException e) {
            AdvancedMining.getInstance().getLogger().warning("Failed to load block drops '" + file.getName() + "'!");
        }

        return null;

    }

    public static void loadAll() {

        if (AdvancedMining.blockDropsFolder.listFiles() == null) return;
        for (File file : Objects.requireNonNull(AdvancedMining.blockDropsFolder.listFiles())) {
            BlockDrops blockDrops = BlockDrops.loadFromFile(file);
            if (blockDrops != null) loadedDrops.put(blockDrops.id, blockDrops);
        }

    }

    public void loadDropsMap() {
        entryMap = new HashMap<>();
        entries.forEach(entry -> entryMap.put(entry.id, entry));
    }

    @Serial
    private void readObject(@NonNull ObjectInputStream in) throws IOException, ClassNotFoundException {

        in.defaultReadObject();

        if (entryMap == null) entryMap = new HashMap<>();
        entries.forEach(entry -> entryMap.put(entry.id, entry));

        // Automatic naming of entries from old versions
        boolean wasFromOldVersion = false;
        for (int i = 0; i < entries.size(); i++) {
            Entry entry = entries.get(i);
            if (entry.id() == null) {
                String newId = "auto-migrated-" + i + "-" + PlainTextComponentSerializer.plainText().serialize(entry.item().effectiveName()).replace(" ", "_");
                entry.setId(newId);
                    AdvancedMining.getInstance().getLogger().info("Automatically renamed legacy Entry " + i + " from Block Drop '" + id + "' to '" + newId + "'");
                wasFromOldVersion = true;
            }
        }
        if (wasFromOldVersion) saveToFile();

    }

    public void modifyWithCommandContext(CommandContext<CommandSourceStack> context, String argName, Consumer<Entry> entry) {

        Entry dropEntry = entryMap().get(StringArgumentType.getString(context, argName));
        if (dropEntry == null) {
            context.getSource().getSender().sendRichMessage("<red>That entry doesn't exist");
            return;
        }

        entry.accept(dropEntry);
        saveToFile();
        context.getSource().getSender().sendRichMessage("<green>Block Drop Entry edited!");

    }

    public static HashMap<String, BlockDrops> loadedDrops() {
        return loadedDrops;
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

    public HashMap<String, Entry> entryMap() {
        return entryMap;
    }

    /**
     * Represents an entry that can be rolled to drop from a block
     */
    public static class Entry implements Serializable {

        @Serial private static final long serialVersionUID = 4427812365522579698L;

        private transient ItemStack itemStack;
        private int minAmount;
        private int maxAmount;
        private float chance;
        private byte[] item;
        private String id;
        private boolean affectedByFortune;
        private boolean silkTouchOnly;
        private boolean noRollByDefault; // if enabled, entry will only be rolled by other drops
        private ArrayList<String> extraDrops; // roll these entries and if none of them drop roll this one

        /**
         * @param itemStack The item to be dropped
         * @param minAmount The minimum amount of the item to be dropped
         * @param maxAmount The maximum amount of the item to be dropped
         * @param chance The chance of the item dropping from the block
         */
        public Entry(String id, @NotNull ItemStack itemStack, int minAmount, int maxAmount, float chance) {
            this.id = id;
            this.itemStack = itemStack;
            this.minAmount = minAmount;
            this.maxAmount = maxAmount;
            this.chance = chance;
            this.item = itemStack.serializeAsBytes();
            this.extraDrops = new ArrayList<>();
        }

        /**
         * @param itemStack The item to be dropped
         * @param minAmount The minimum amount of the item to be dropped
         * @param maxAmount The maximum amount of the item to be dropped
         * @param chance The chance of the item dropping from the block
         * @param affectedByFortune Should this drop be affected by fortune if enabled in the config
         * @param silkTouchOnly Should this drop be rolled only when the tool has silk touch
         * @param noRollByDefault If enabled, this drop will only be rolled by other drops
         * @param extraDrops Ids of drops that will be rolled before this one
         */
        public Entry(String id, ItemStack itemStack, int minAmount, int maxAmount, float chance, boolean affectedByFortune, boolean silkTouchOnly, boolean noRollByDefault, ArrayList<String> extraDrops) {
            this.id = id;
            this.itemStack = itemStack;
            this.minAmount = minAmount;
            this.maxAmount = maxAmount;
            this.chance = chance;
            this.affectedByFortune = affectedByFortune;
            this.silkTouchOnly = silkTouchOnly;
            this.noRollByDefault = noRollByDefault;
            this.extraDrops = extraDrops;
        }

        @Serial
        private void readObject(@NotNull ObjectInputStream stream) throws IOException, ClassNotFoundException {
            stream.defaultReadObject();
            itemStack = ItemStack.deserializeBytes(item);
            if (extraDrops == null) extraDrops = new ArrayList<>();
        }

        public ArrayList<ItemStack> roll() {

            ArrayList<ItemStack> droppedItems = new ArrayList<>();
            if (new Random().nextDouble() <= chance) droppedItems.addAll(List.of(getItemAmountArray(itemStack, new Random().nextInt(minAmount, maxAmount + 1))));

            return droppedItems;

        }

        public ArrayList<ItemStack> roll(ItemStack tool) {

            if (tool == null) return roll();

            ArrayList<ItemStack> droppedItems = new ArrayList<>();
            if (silkTouchOnly && !tool.containsEnchantment(Enchantment.SILK_TOUCH)) return droppedItems;
            int fortuneLevel = tool.getEnchantmentLevel(Enchantment.FORTUNE);

            if (AdvancedMining.Config.fortuneEnable) {

                if (affectedByFortune) {

                    if (AdvancedMining.Config.fortuneEffectType.equalsIgnoreCase("vanilla")) {

                        if (silkTouchOnly && !AdvancedMining.Config.fortuneVanillaIgnoreSilkTouch) return roll();

                        float normalDropChance = (float) 2 / (fortuneLevel+2);
                        boolean noBonus = new Random().nextDouble() <= normalDropChance;

                        if (noBonus) return roll(); // If no bonus, roll normally
                        int dropMultiplier = new Random().nextInt(2, fortuneLevel + 2);

                        if (!AdvancedMining.Config.fortuneVanillaBehavior.equals("additional-rolls")) { // Increase the max drop amount if the option says that

                            if (new Random().nextDouble() <= chance) droppedItems.addAll(List.of(getItemAmountArray(itemStack, new Random().nextInt(minAmount, maxAmount + 1) * dropMultiplier)));
                            return droppedItems;

                        } else return roll(); // If option is extra rolls, return a clean roll. That stuff is handled upstream

                    } else { // Custom effect type

                        float rollChance = chance + fortuneLevel * AdvancedMining.Config.fortuneDropChance;
                        int rollMinAmount = minAmount + fortuneLevel * AdvancedMining.Config.fortuneMinAmount;
                        int rollMaxAmount = maxAmount + fortuneLevel * AdvancedMining.Config.fortuneMaxAmount;

                        if (new Random().nextDouble() <= rollChance) droppedItems.addAll(List.of(getItemAmountArray(itemStack, new Random().nextInt(rollMinAmount, rollMaxAmount + 1))));

                        return droppedItems;

                    }

                } else return roll(); // If unaffected by fortune, return normal roll

            } else return roll();

        }

        public String id() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public boolean affectedByFortune() {
            return affectedByFortune;
        }

        public void setAffectedByFortune(boolean affectedByFortune) {
            this.affectedByFortune = affectedByFortune;
        }

        public boolean silkTouchOnly() {
            return silkTouchOnly;
        }

        public void setSilkTouchOnly(boolean silkTouchOnly) {
            this.silkTouchOnly = silkTouchOnly;
        }

        public ArrayList<String> extraDrops() {
            return extraDrops;
        }

        public void setExtraDrops(ArrayList<String> extraDrops) {
            this.extraDrops = extraDrops;
        }

        public boolean noRollByDefault() {
            return noRollByDefault;
        }

        public void setNoRollByDefault(boolean noRollByDefault) {
            this.noRollByDefault = noRollByDefault;
        }

    }

}
