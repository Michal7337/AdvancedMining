package win.codingboulder.advancedmining;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class CustomBlock {

    public static HashMap<String, CustomBlock> loadedBlocks = new HashMap<>();

    private String id;
    private Component name;
    private Material material;
    private float strength;
    private int hardness;

    private Key texture;
    private Key breakSound;
    private Key placeSound;
    private Material iconMaterial;
    private File dropsFile;

    public CustomBlock(String id, Component name, Material material, float strength, int hardness, Key texture, Key breakSound, Key placeSound, Material iconMaterial, File dropsFile) {

        this.id = id;
        this.name = name;
        this.material = material;
        this.strength = strength;
        this.hardness = hardness;
        this.texture = texture;
        this.breakSound = breakSound;
        this.placeSound = placeSound;
        this.iconMaterial = iconMaterial;

        this.dropsFile = dropsFile;

    }

    public static @Nullable CustomBlock getCustomBlock(@NotNull Block block) {

        String blockId = BlockDataStorage.getDataContainer(block).getOrDefault(
            new NamespacedKey("AdvancedMining", "block_id"), PersistentDataType.STRING, "");

        return loadedBlocks.get(blockId);

    }

    public static @Nullable ItemDisplay getDisplayEntity(Block block) {

        String uuidStr = BlockDataStorage.getDataContainer(block).get(
            new NamespacedKey("AdvancedMining", "display_entity"), PersistentDataType.STRING);

        if (uuidStr == null) return null;
        return (ItemDisplay) block.getWorld().getEntity(UUID.fromString(uuidStr));

    }

    public static void setDisplayEntity(Block block, @NotNull ItemDisplay itemDisplay) {

        BlockDataStorage.getDataContainer(block).set(new NamespacedKey("AdvancedMining", "display_entity"), PersistentDataType.STRING, itemDisplay.getUniqueId().toString());

    }

    public static CustomBlock constructFromInfo(CustomBlockInfo blockInfo) {

        Key texture = !blockInfo.texture().isEmpty() ? Key.key(blockInfo.texture()) : null;
        Key placeSound = !blockInfo.placeSound().isEmpty() ? Key.key(blockInfo.placeSound()) : null;
        Key breakSound = !blockInfo.breakSound().isEmpty() ? Key.key(blockInfo.breakSound()) : null;

        return new CustomBlock(
            blockInfo.id(),
            MiniMessage.miniMessage().deserialize(blockInfo.name()),
            blockInfo.material(),
            blockInfo.strength(),
            blockInfo.hardness(),
            texture,
            breakSound,
            placeSound,
            blockInfo.iconMaterial(),
            new File(AdvancedMining.blockDropsFolder, blockInfo.dropsFile())
        );

    }

    public static void loadBlocks() {

        CustomBlockInfo.loadBlocks();

        loadedBlocks = new HashMap<>();
        for (CustomBlockInfo blockInfo : CustomBlockInfo.loadedBlocks)
            loadedBlocks.put(blockInfo.id(), constructFromInfo(blockInfo));

    }

    public String id() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Component name() {
        return name;
    }

    public void setName(Component name) {
        this.name = name;
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

    public Key texture() {
        return texture;
    }

    public void setTexture(Key texture) {
        this.texture = texture;
    }

    public Key breakSound() {
        return breakSound;
    }

    public void setBreakSound(Key breakSound) {
        this.breakSound = breakSound;
    }

    public Key placeSound() {
        return placeSound;
    }

    public void setPlaceSound(Key placeSound) {
        this.placeSound = placeSound;
    }

    public Material iconMaterial() {
        return iconMaterial;
    }

    public void setIconMaterial(Material iconMaterial) {
        this.iconMaterial = iconMaterial;
    }

    public File dropsFile() {
        return dropsFile;
    }

    public void setDropsFile(File dropsFile) {
        this.dropsFile = dropsFile;
    }

}
