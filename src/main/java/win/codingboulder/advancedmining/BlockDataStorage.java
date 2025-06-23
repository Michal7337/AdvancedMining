package win.codingboulder.advancedmining;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * <h3>A class for storing data "in blocks"</h3>
 * It works by saving data in the {@link PersistentDataContainer} of the chunk the block is in.
 */
public class BlockDataStorage {

    public static final String DEFAULT_NAMESPACE = "blockdatastorage";

    private BlockDataStorage() {}

    /**
     * Gets the {@link PersistentDataContainer} associated with this block in the default namespace.
     * If the block doesn't have a PDC a new one is created.<br>
     * Changes to this container WILL NOT be reflected in the saved container. To save them you have to use {@code setContainer()} or use the {@code editContainer()} utility method.
     * @param block The block
     * @return The {@link PersistentDataContainer} associated with the block
     */
    public static PersistentDataContainer getDataContainer(@NotNull Block block) {

        NamespacedKey blockKey = getBockKey(block);
        PersistentDataContainer chunkPdc = block.getChunk().getPersistentDataContainer();

        if (chunkPdc.has(blockKey, PersistentDataType.TAG_CONTAINER)) return chunkPdc.get(blockKey, PersistentDataType.TAG_CONTAINER);

        PersistentDataContainer newPdc = chunkPdc.getAdapterContext().newPersistentDataContainer();
        chunkPdc.set(blockKey, PersistentDataType.TAG_CONTAINER, newPdc);

        return newPdc;

    }

    /**
     * Gets the {@link PersistentDataContainer} associated with this block in the specified namespace.
     * If the block doesn't have a PDC a new one is created. <br>
     * Changes to this container WILL NOT be reflected in the saved container. To save them you have to use {@code setContainer()} or use the {@code editContainer()} utility method.
     * @param block The block
     * @param namespace The namespace
     * @return The {@link PersistentDataContainer} associated with the block
     */
    public static PersistentDataContainer getDataContainer(@NotNull Block block, String namespace) {

        NamespacedKey blockKey = getBockKey(block, namespace);
        PersistentDataContainer chunkPdc = block.getChunk().getPersistentDataContainer();

        if (chunkPdc.has(blockKey, PersistentDataType.TAG_CONTAINER)) return chunkPdc.get(blockKey, PersistentDataType.TAG_CONTAINER);

        PersistentDataContainer newPdc = chunkPdc.getAdapterContext().newPersistentDataContainer();
        chunkPdc.set(blockKey, PersistentDataType.TAG_CONTAINER, newPdc);

        return newPdc;

    }

    /**
     * Gets if the block has an associated PDC in the default namespace.
     * @param block The block
     * @return Weather the block has a PDC associated with it
     */
    public static boolean hasContainer(@NotNull Block block) {
        return block.getChunk().getPersistentDataContainer().has(getBockKey(block), PersistentDataType.TAG_CONTAINER);
    }

    /**
     * Gets if the block has an associated PDC in the specified namespace.
     * @param block The block
     * @param namespace The namespace
     * @return Weather the block has a PDC associated with it
     */
    public static boolean hasContainer(@NotNull Block block, String namespace) {
        return block.getChunk().getPersistentDataContainer().has(getBockKey(block, namespace), PersistentDataType.TAG_CONTAINER);
    }


    /**
     * Sets the {@link PersistentDataContainer} associated with this block in the default namespace.
     * @param block The block
     * @param container The container to associate
     */
    public static void setContainer(@NotNull Block block, PersistentDataContainer container) {
        block.getChunk().getPersistentDataContainer().set(getBockKey(block), PersistentDataType.TAG_CONTAINER, container);
    }

    /**
     * Sets the {@link PersistentDataContainer} associated with this block in the specified namespace.
     * @param block The block
     * @param container The container to associate
     * @param namespace The namespace
     */
    public static void setContainer(@NotNull Block block, PersistentDataContainer container, String namespace) {
        block.getChunk().getPersistentDataContainer().set(getBockKey(block, namespace), PersistentDataType.TAG_CONTAINER, container);
    }

    /**
     * This is a utility method for editing and saving the Block's PDC with a lambda. <br>
     * It works just like {@code ItemStack.editPersistentDataContainer()}
     * @param block The block to edit container of
     * @param pdc A consumer that edits the PDC
     */
    public static void editContainer(Block block, @NotNull Consumer<PersistentDataContainer> pdc) {

        PersistentDataContainer container = getDataContainer(block);
        pdc.accept(container);
        setContainer(block, container);

    }

    /**
     * This is a utility method for editing and saving the Block's PDC with a lambda. <br>
     * It works just like {@code ItemStack.editPersistentDataContainer()}
     * @param block The block to edit container of
     * @param namespace The namespace of the edited container
     * @param pdc A consumer that edits the PDC
     */
    public static void editContainer(Block block, String namespace,  @NotNull Consumer<PersistentDataContainer> pdc) {

        PersistentDataContainer container = getDataContainer(block, namespace);
        pdc.accept(container);
        setContainer(block, container, namespace);

    }

    /**
     * Gets the {@link NamespacedKey} for the block in the default namespace. <br>
     * Format: {@code blockdatastorage:x_y_z}
     * @param block The block to get key of
     * @return The key
     */
    public static @NotNull NamespacedKey getBockKey(@NotNull Block block) {
        return new NamespacedKey(DEFAULT_NAMESPACE, block.getX() + "_" + block.getY() + "_" + block.getZ());
    }

    /**
     * Gets the {@link NamespacedKey} for the block in the specified namespace. <br>
     * Format: {@code namespace:x_y_z}
     * @param block The block to get key of
     * @param namespace The namespace
     * @return The key
     */
    public static @NotNull NamespacedKey getBockKey(@NotNull Block block, String namespace) {
        return new NamespacedKey(namespace, block.getX() + "_" + block.getY() + "_" + block.getZ());
    }

}
