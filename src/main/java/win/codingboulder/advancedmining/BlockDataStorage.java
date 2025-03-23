package win.codingboulder.advancedmining;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class BlockDataStorage {

    private BlockDataStorage() {}

    public static PersistentDataContainer getDataContainer(@NotNull Block block) {

        NamespacedKey blockKey = getBockKey(block);
        PersistentDataContainer chunkPdc = block.getChunk().getPersistentDataContainer();

        if (chunkPdc.has(blockKey, PersistentDataType.TAG_CONTAINER)) return chunkPdc.get(blockKey, PersistentDataType.TAG_CONTAINER);

        PersistentDataContainer newPdc = chunkPdc.getAdapterContext().newPersistentDataContainer();
        chunkPdc.set(blockKey, PersistentDataType.TAG_CONTAINER, newPdc);

        return newPdc;

    }

    public static PersistentDataContainer getDataContainer(@NotNull Block block, String namespace) {

        NamespacedKey blockKey = getBockKey(block, namespace);
        PersistentDataContainer chunkPdc = block.getChunk().getPersistentDataContainer();

        if (chunkPdc.has(blockKey, PersistentDataType.TAG_CONTAINER)) return chunkPdc.get(blockKey, PersistentDataType.TAG_CONTAINER);

        PersistentDataContainer newPdc = chunkPdc.getAdapterContext().newPersistentDataContainer();
        chunkPdc.set(blockKey, PersistentDataType.TAG_CONTAINER, newPdc);

        return newPdc;

    }

    public static boolean hasContainer(@NotNull Block block) {
        return block.getChunk().getPersistentDataContainer().has(getBockKey(block), PersistentDataType.TAG_CONTAINER);
    }

    public static boolean hasContainer(@NotNull Block block, String namespace) {
        return block.getChunk().getPersistentDataContainer().has(getBockKey(block, namespace), PersistentDataType.TAG_CONTAINER);
    }

    public static void setContainer(@NotNull Block block, PersistentDataContainer container) {
        block.getChunk().getPersistentDataContainer().set(getBockKey(block), PersistentDataType.TAG_CONTAINER, container);
    }

    public static void setContainer(@NotNull Block block, PersistentDataContainer container, String namespace) {
        block.getChunk().getPersistentDataContainer().set(getBockKey(block, namespace), PersistentDataType.TAG_CONTAINER, container);
    }

    @Contract("_ -> new")
    public static @NotNull NamespacedKey getBockKey(@NotNull Block block) {
        return new NamespacedKey("BlockDataStorage", block.getX() + "," + block.getY() + "," + block.getZ());
    }

    public static @NotNull NamespacedKey getBockKey(@NotNull Block block, String namespace) {
        return new NamespacedKey(namespace, block.getX() + "," + block.getY() + "," + block.getZ());
    }

}
