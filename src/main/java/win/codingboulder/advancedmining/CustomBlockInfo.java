package win.codingboulder.advancedmining;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Material;
import org.intellij.lang.annotations.Subst;

import java.io.*;
import java.util.ArrayList;

public class CustomBlockInfo {

    public static ArrayList<CustomBlockInfo> loadedBlocks = new ArrayList<>();

    private String id;
    private String name;
    private Material material;
    private float strength;
    private int hardness;

    private String bestTool;
    private String texture;
    private String breakSound;
    private String placeSound;
    private Material iconMaterial;
    private String dropsFile;

    public CustomBlockInfo(
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

        loadedBlocks = new ArrayList<>();
        for (File file : files) {
            try {
                CustomBlockInfo blockInfo = new Gson().fromJson(new FileReader(file), CustomBlockInfo.class);
                loadedBlocks.add(blockInfo);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public String id() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String name() {
        return name;
    }

    public void setName(String name) {
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

    public String bestTool() {
        return bestTool;
    }

    public void setBestTool(String bestTool) {
        this.bestTool = bestTool;
    }

    @Subst("example:some_ore")
    public String texture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    @Subst("example:some_ore_break")
    public String breakSound() {
        return breakSound;
    }

    public void setBreakSound(String breakSound) {
        this.breakSound = breakSound;
    }

    @Subst("example:some_ore_place")
    public String placeSound() {
        return placeSound;
    }

    public void setPlaceSound(String placeSound) {
        this.placeSound = placeSound;
    }

    public Material iconMaterial() {
        return iconMaterial;
    }

    public void setIconMaterial(Material iconMaterial) {
        this.iconMaterial = iconMaterial;
    }

    public String dropsFile() {
        return dropsFile;
    }

    public void setDropsFile(String dropsFile) {
        this.dropsFile = dropsFile;
    }

}
