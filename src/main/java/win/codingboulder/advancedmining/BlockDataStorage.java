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

    @Contract("_ -> new")
    public static @NotNull NamespacedKey getBockKey(@NotNull Block block) {
        return new NamespacedKey("BlockDataStorage", block.getX() + "," + block.getY() + "," + block.getZ());
    }

}
