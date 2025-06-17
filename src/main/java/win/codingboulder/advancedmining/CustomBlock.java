package win.codingboulder.advancedmining;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

public class CustomBlock {

    public static final NamespacedKey blockIdKey = new NamespacedKey("advancedmining", "block_id");
    public static HashMap<String, CustomBlock> loadedBlocks = new HashMap<>();

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

    public CustomBlock(
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

    public static void loadBlocks() {

        File[] files = AdvancedMining.blocksFolder.listFiles();
        if (files == null) return;

        loadedBlocks = new HashMap<>();
        for (File file : files) {
            try {
                CustomBlock customBlock = new Gson().fromJson(new FileReader(file), CustomBlock.class);
                customBlock.constructAttributes();
                loadedBlocks.put(customBlock.id(), customBlock);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void editAndSave(@NotNull Consumer<CustomBlock> block) {
        block.accept(this);
        saveToFile();
    }

    public static @Nullable CustomBlock getCustomBlock(@NotNull Block block) {

        if (!BlockDataStorage.hasContainer(block)) return null;
        String blockId = BlockDataStorage.getDataContainer(block).getOrDefault(blockIdKey, PersistentDataType.STRING, "");
        return loadedBlocks.get(blockId);

    }

    public static void setCustomBlock(Block block, String id) {

        BlockDataStorage.editContainer(block, pdc -> pdc.set(blockIdKey, PersistentDataType.STRING, id));

        CustomBlock customBlock = loadedBlocks.get(id);
        if (customBlock == null) return;
        block.setType(customBlock.material);
        customBlock.summonDisplayEntity(block);

    }

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
