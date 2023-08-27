package me.michal737.advancedmining;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A utility class that allows to easily store information in blocks.
 */
@SuppressWarnings("unused")
public class BlockDataStorage {

    private static final String MAIN_NAMESPACE = "blockdatastorage";
    private static File blockDatabaseFile;

    public static void initialise(File file){

        blockDatabaseFile = file;

    }

    /**
     * Adds or removes a block from the block database
     * @param block The block to add/remove from the database
     * @param action The action. Can be "add" or "remove"
     */
    public static void updateBlockDatabase(Block block, String action){

        if (blockDatabaseFile == null) return;

        try {

            //noinspection ResultOfMethodCallIgnored
            blockDatabaseFile.createNewFile();

            FileReader fileReader = new FileReader(blockDatabaseFile);
            CustomBlockList customBlockList = new Gson().fromJson(fileReader, CustomBlockList.class);
            fileReader.close();
            ArrayList<String> blockList = customBlockList.getBlocks();

            if (action.equals("add")){

                blockList.add(locationToString(block));
                customBlockList.setBlocks(blockList);

            }if (action.equals("remove")){

                blockList.remove(locationToString(block));
                customBlockList.setBlocks(blockList);

            }else return;

            FileWriter fileWriter = new FileWriter(blockDatabaseFile);
            new GsonBuilder().setPrettyPrinting().create().toJson(customBlockList, fileWriter);
            fileWriter.close();

        }catch (IOException e){throw new RuntimeException();}

    }

    /**
     * Stores data in a block
     * @param block The {@link Block} to set the data of
     * @param namespace The namespace that will contain the key
     * @param key The key under which the data is stored
     * @param data The data to store
     */
    public static void setData(@NotNull Block block, String namespace, String key, String data){

        PersistentDataContainer chunkPDC = block.getChunk().getPersistentDataContainer();
        PersistentDataContainer dataContainer = chunkPDC.get(new NamespacedKey(MAIN_NAMESPACE, locationToString(block)), PersistentDataType.TAG_CONTAINER);
        if (dataContainer == null) dataContainer = chunkPDC.getAdapterContext().newPersistentDataContainer();
        dataContainer.set(new NamespacedKey(namespace, key), PersistentDataType.STRING, data);
        chunkPDC.set(new NamespacedKey(MAIN_NAMESPACE, locationToString(block)), PersistentDataType.TAG_CONTAINER, dataContainer);

    }

    /**
     * Reads data from a block
     * @param block The block to read data from
     * @param namespace The namespace containing the key
     * @param key The key under which the data is stored
     * @return The stored data or null if no data is stored
     */
    public static @Nullable String getData(@NotNull Block block, String namespace, String key){

        PersistentDataContainer chunkPDC = block.getChunk().getPersistentDataContainer();
        PersistentDataContainer dataContainer = chunkPDC.get(new NamespacedKey(MAIN_NAMESPACE, locationToString(block)), PersistentDataType.TAG_CONTAINER);
        if (dataContainer == null) return null;

        return dataContainer.get(new NamespacedKey(namespace, key), PersistentDataType.STRING);

    }

    /**
     * Checks if a block contains any data
     * @param block The block to check
     * @return The result
     */
    public static boolean hasData(@NotNull Block block){

        PersistentDataContainer chunkPDC = block.getChunk().getPersistentDataContainer();

        return chunkPDC.has(new NamespacedKey(MAIN_NAMESPACE, locationToString(block)));

    }

    /**
     * Checks if a block contains any data under a given key
     * @param block The block to check
     * @param namespace The namespace containing the key
     * @param key The key to check
     * @return The result
     */
    public static boolean hasData(@NotNull Block block, String namespace, String key){

        PersistentDataContainer chunkPDC = block.getChunk().getPersistentDataContainer();
        PersistentDataContainer dataContainer = chunkPDC.get(new NamespacedKey(MAIN_NAMESPACE, locationToString(block)), PersistentDataType.TAG_CONTAINER);
        if (dataContainer == null) return false;

        return dataContainer.has(new NamespacedKey(namespace, key));

    }


    /**
     * Deletes data from a block
     * @param block The block to remove data from
     * @param namespace The namespace containing the key
     * @param key The key to delete
     */
    public static void removeData(@NotNull Block block, String namespace, String key){

        PersistentDataContainer chunkPDC = block.getChunk().getPersistentDataContainer();
        PersistentDataContainer dataContainer = chunkPDC.get(new NamespacedKey(MAIN_NAMESPACE, locationToString(block)), PersistentDataType.TAG_CONTAINER);
        if (dataContainer == null) return;
        dataContainer.remove(new NamespacedKey(namespace, key));
        chunkPDC.set(new NamespacedKey(MAIN_NAMESPACE, locationToString(block)), PersistentDataType.TAG_CONTAINER, dataContainer);

    }

    /**
     * Deletes ALL the data from a block. Use with caution!
     * @param block The block to delete data from
     */
    public static void removeData(@NotNull Block block){

        PersistentDataContainer chunkPDC = block.getChunk().getPersistentDataContainer();
        chunkPDC.remove(new NamespacedKey(MAIN_NAMESPACE, locationToString(block)));


    }

    private static @NotNull String locationToString(@NotNull Block block){

        return block.getX() + "_" + block.getY() + "_" + block.getZ() + "_";

    }

    public static class CustomBlockList {

        private ArrayList<String> blocks;

        public CustomBlockList(ArrayList<String> blocks) {
            this.blocks = blocks;
        }

        public ArrayList<String> getBlocks() {
            return blocks;
        }

        public void setBlocks(ArrayList<String> blocks) {
            this.blocks = blocks;
        }

    }

}
