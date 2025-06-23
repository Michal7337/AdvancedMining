package win.codingboulder.advancedmining;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import win.codingboulder.advancedmining.mechanics.BlockDrops;

import java.io.*;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Represents a <b>Custom Block</b> which has properties such as strength and hardness and can be broken with the custom mining system. <br>
 * It is not associated with an actual block placed in a world. Instead, a real block stores the id of a custom block, which is then looked up from the loadedBlocks map.
 */
public class CustomBlock {

    public static final NamespacedKey blockIdKey = new NamespacedKey("advancedmining", "block_id");
    private static HashMap<String, CustomBlock> loadedBlocks = new HashMap<>();

    // Main attributes
    private String id;
    private String name;
    private Material material;
    private float strength;
    private int hardness;

    // Additional attributes
    private String bestTool;
    private String texture;
    private String breakSound;
    private String placeSound;
    private Material iconMaterial;
    private String dropsFile;

    // Converted attributes
    private transient Component nameComponent;
    private transient Key textureKey;
    private transient Key breakSoundKey;
    private transient Key placeSoundKey;
    private transient File blockDropsFile;
    private transient BlockDrops blockDrops;

    /**
     * @param id The ID of the block
     * @param name The name of the block in MiniMessage format
     * @param material The material of the block that will be placed in the world
     * @param strength The strength of the block. The player's mining speed is subtracted from this every tick until it reaches 0
     * @param hardness The hardness of the block. The player's breaking power needs to be equal to or greater than this
     * @param bestTool The tool type required to mine this block. If empty, all tools can mine the block
     * @param texture The custom texture that will be applied to the placed block using an Item Display entity
     * @param breakSound The sound that will be played when breaking the block
     * @param placeSound The sound that will be played when placing the block
     * @param iconMaterial The material that will be used to display the block in the inventory and used for breaking particles
     * @param dropsFile The id of the Block Drops the block will use
     */
    public CustomBlock (
        String id,
        String name,
        Material material,
        float strength,
        int hardness,
        String bestTool,
        String texture,
        String breakSound,
        String placeSound,
        Material iconMaterial,
        String dropsFile
    ) {

        this.id = id;
        this.name = name;
        this.material = material;
        this.strength = strength;
        this.hardness = hardness;
        this.bestTool = bestTool;
        this.texture = texture;
        this.breakSound = breakSound;
        this.placeSound = placeSound;
        this.iconMaterial = iconMaterial;
        this.dropsFile = dropsFile;

        constructAttributes();

    }


    /**
     * @param id The ID of the block
     * @param name The name of the block in MiniMessage format
     * @param material The material of the block that will be placed in the world
     * @param strength The strength of the block. The player's mining speed is subtracted from this every tick until it reaches 0
     * @param hardness The hardness of the block. The player's breaking power needs to be equal to or greater than this
     * @param bestTool The tool type required to mine this block. If empty, all tools can mine the block
     */
    public CustomBlock(String id, String name, Material material, float strength, int hardness, String bestTool) {

        this.id = id;
        this.name = name;
        this.material = material;
        this.strength = strength;
        this.hardness = hardness;
        this.bestTool = bestTool;
        this.texture = "";
        this.breakSound = "";
        this.placeSound = "";
        this.iconMaterial = material;
        this.dropsFile = "";

        constructAttributes();

    }

    public CustomBlock(String id, Component name, Material material, float strength, int hardness, String bestTool) {

        this.id = id;
        this.name = "";
        this.material = material;
        this.strength = strength;
        this.hardness = hardness;
        this.bestTool = bestTool;
        this.texture = "";
        this.breakSound = "";
        this.placeSound = "";
        this.iconMaterial = material;
        this.dropsFile = "";

        constructAttributes();
        this.nameComponent = name;

    }

    public CustomBlock(String id, Component name, Material material, float strength, int hardness, String bestTool, Key textureKey, Key breakSoundKey, Key placeSoundKey, String blockDrops) {

        this.id = id;
        this.nameComponent = name;
        this.material = material;
        this.strength = strength;
        this.hardness = hardness;
        this.bestTool = bestTool;
        this.textureKey = textureKey;
        this.breakSoundKey = breakSoundKey;
        this.placeSoundKey = placeSoundKey;
        this.dropsFile = blockDrops;

    }

    public void constructAttributes() {

        this.nameComponent = MiniMessage.miniMessage().deserialize(name);
        this.textureKey = texture == null || texture.isEmpty() ? null : Key.key(texture);
        this.placeSoundKey = placeSound == null || placeSound.isEmpty() ? null : Key.key(placeSound);
        this.breakSoundKey = breakSound == null || breakSound.isEmpty() ? null : Key.key(breakSound);
        this.blockDropsFile = new File(AdvancedMining.blockDropsFolder, dropsFile);

    }

    public void saveToFile() {

        File file = new File(AdvancedMining.blocksFolder, id + ".json");
        try (FileWriter writer = new FileWriter(file)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(this, writer);
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while saving custom block!", e);
        }

    }

    public static void loadAll() {

        File[] files = AdvancedMining.blocksFolder.listFiles();
        if (files == null) return;

        loadedBlocks = new HashMap<>();
        for (File file : files) {
            try {
                CustomBlock customBlock = new Gson().fromJson(new FileReader(file), CustomBlock.class);
                customBlock.constructAttributes();
                loadedBlocks.put(customBlock.id(), customBlock);
            } catch (IOException e) {
                AdvancedMining.getInstance().getLogger().warning("Failed to load block '" + file.getName() + "' - IO exception!");
            } catch (JsonSyntaxException e) {
                AdvancedMining.getInstance().getLogger().warning("Failed to load block '" + file.getName() + "' - Invalid JSON!");
            }
        }

    }

    public void editAndSave(@NotNull Consumer<CustomBlock> block) {

        block.accept(this);
        saveToFile();

    }

    /**
     * Gets the {@link CustomBlock} that is associated with the given Block. <br>
     * This checks the {@link BlockDataStorage} for the block id.
     * @param block The Block to check
     * @return The {@link CustomBlock} at the given Block or {@code null} if there is none
     */
    public static @Nullable CustomBlock getCustomBlock(@NotNull Block block) {

        if (!BlockDataStorage.hasContainer(block)) return null;
        String blockId = BlockDataStorage.getDataContainer(block).getOrDefault(blockIdKey, PersistentDataType.STRING, "");
        return loadedBlocks.get(blockId);

    }

    /**
     * Sets the {@link CustomBlock} at the given Block. If the CustomBlock doesn't exist, the ID will still be set, so if it does exist in the future, it will work fine.
     * @param block The block to set
     * @param id The ID of the CustomBlock
     */
    public static void setCustomBlock(Block block, String id) {

        BlockDataStorage.editContainer(block, pdc -> pdc.set(blockIdKey, PersistentDataType.STRING, id));

        CustomBlock customBlock = loadedBlocks.get(id);
        if (customBlock == null) return;
        block.setType(customBlock.material);
        customBlock.summonDisplayEntity(block);

    }

    /**
     * This method returns the ItemDisplay entity that displays the CustomBlock's custom texture, if the block has one.
     * @param block The Block to check
     * @return The {@link ItemDisplay} placed at the given Block or {@code null} if there is none
     */
    public static @Nullable ItemDisplay getDisplayEntity(Block block) {

        String uuidStr = BlockDataStorage.getDataContainer(block).get(
            new NamespacedKey("advancedmining", "display_entity"), PersistentDataType.STRING);

        if (uuidStr == null) return null;
        return (ItemDisplay) block.getWorld().getEntity(UUID.fromString(uuidStr));

    }

    public static void setDisplayEntity(Block block, @NotNull ItemDisplay itemDisplay) {

        BlockDataStorage.editContainer(block, pdc ->
            pdc.set(new NamespacedKey("advancedmining", "display_entity"), PersistentDataType.STRING, itemDisplay.getUniqueId().toString()));

    }

    /**
     * Summons an {@link ItemDisplay} entity with this CustomBlock's texture at the given Block.<br>
     * The entity's scale is {@code 1.001} to prevent Z-Fighting. <br>
     * The Material of the Block should be transparent (e.g. Glass) so the entity isn't black and reacts to light properly.<br>
     * If the Block has a custom texture, the IconMaterial will be used for the breaking effect.
     * @param block The block to summon the entity at
     */
    @SuppressWarnings("UnstableApiUsage")
    public void summonDisplayEntity(Block block) {

        if (textureKey == null) return;

        ItemStack item = ItemStack.of(Material.STONE);
        item.setData(DataComponentTypes.ITEM_MODEL, textureKey);

        ItemDisplay itemDisplay = block.getWorld().createEntity(block.getLocation().add(0.5, 0.5, 0.5), ItemDisplay.class);
        itemDisplay.setItemStack(item);
        itemDisplay.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(1.001f, 1.001f, 1.001f), new AxisAngle4f()));
        itemDisplay.setViewRange(2);
        itemDisplay.setShadowStrength(0);
        itemDisplay.addScoreboardTag("advmining_block");
        itemDisplay.addScoreboardTag("advmining_block_" + id);

        itemDisplay.spawnAt(block.getLocation().add(0.5, 0.5, 0.5));

        BlockDataStorage.editContainer(block, pdc ->
            pdc.set(new NamespacedKey("advancedmining", "display_entity"), PersistentDataType.STRING, itemDisplay.getUniqueId().toString()));

    }

    public static HashMap<String, CustomBlock> loadedBlocks() {
        return loadedBlocks;
    }

    public String id() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Component name() {
        return nameComponent;
    }

    public String rawName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.nameComponent = MiniMessage.miniMessage().deserialize(name);
    }

    public void setName(Component name) {
        this.nameComponent = name;
        this.name = MiniMessage.miniMessage().serialize(name);
    }

    public Material material() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public float strength() {
        return strength;
    }

    public void setStrength(float strength) {
        this.strength = strength;
    }

    public int hardness() {
        return hardness;
    }

    public void setHardness(int hardness) {
        this.hardness = hardness;
    }

    public String bestTool() {
        return bestTool;
    }

    public void setBestTool(String bestTool) {
        this.bestTool = bestTool;
    }

    @Subst("example:some_ore")
    public String rawTexture() {
        return texture;
    }

    public Key texture() {
        return textureKey;
    }

    public void setTexture(String texture) {
        this.texture = texture;
        this.textureKey = texture == null || texture.isEmpty() ? null : Key.key(texture);
    }

    public void setTexture(@NotNull Key texture) {
        this.textureKey = texture;
        this.texture = texture.asString();
    }

    @Subst("example:some_ore_break")
    public String rawBreakSound() {
        return breakSound;
    }

    public Key breakSound() {
        return breakSoundKey;
    }

    public void setBreakSound(String breakSound) {
        this.breakSound = breakSound;
        this.breakSoundKey = breakSound == null || breakSound.isEmpty() ? null : Key.key(breakSound);
    }

    public void setBreakSound(@NotNull Key breakSound) {
        this.breakSoundKey = breakSound;
        this.breakSound = breakSound.asString();
    }

    @Subst("example:some_ore_place")
    public String rawPlaceSound() {
        return placeSound;
    }

    public Key placeSound() {
        return placeSoundKey;
    }

    public void setPlaceSound(String placeSound) {
        this.placeSound = placeSound;
        this.placeSoundKey = placeSound == null || placeSound.isEmpty() ? null : Key.key(placeSound);
    }

    public void setPlaceSound(Key placeSound) {
        this.placeSoundKey = placeSound;
    }

    public Material iconMaterial() {
        return iconMaterial;
    }

    public void setIconMaterial(Material iconMaterial) {
        this.iconMaterial = iconMaterial;
    }

    public String rawDropsFile() {
        return dropsFile;
    }

    public File dropsFile() {
        return blockDropsFile;
    }

    public void setDropsFile(String dropsFile) {
        this.dropsFile = dropsFile;
        this.blockDropsFile = new File(AdvancedMining.blockDropsFolder, dropsFile);
    }

    public void setDropsFile(@NotNull File dropsFile) {
        this.blockDropsFile = dropsFile;
        this.dropsFile = dropsFile.getName();
    }

    public BlockDrops blockDrops() {
        return blockDrops;
    }

    public void setBlockDrops(BlockDrops blockDrops) {
        this.blockDrops = blockDrops;
    }

}
