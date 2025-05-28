package win.codingboulder.advancedmining;

import com.google.gson.Gson;
import org.bukkit.Material;
import org.intellij.lang.annotations.Subst;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

public class CustomBlockInfo {

    public static ArrayList<CustomBlockInfo> loadedBlocks = new ArrayList<>();

    private String id;
    private String name;
    private Material material;
    private int strength;
    private int hardness;

    private String texture;
    private String breakSound;
    private String placeSound;
    private Material iconMaterial;
    private String dropsFile;

    public CustomBlockInfo(
        String id,
        String name,
        Material material,
        int strength,
        int hardness,
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
        this.texture = texture;
        this.breakSound = breakSound;
        this.placeSound = placeSound;
        this.iconMaterial = iconMaterial;
        this.dropsFile = dropsFile;

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

    public void id(String id) {
        this.id = id;
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }

    public Material material() {
        return material;
    }

    public void material(Material material) {
        this.material = material;
    }

    public int strength() {
        return strength;
    }

    public void strength(int strength) {
        this.strength = strength;
    }

    public int hardness() {
        return hardness;
    }

    public void hardness(int hardness) {
        this.hardness = hardness;
    }

    @Subst("example:some_ore")
    public String texture() {
        return texture;
    }

    public void texture(String texture) {
        this.texture = texture;
    }

    @Subst("example:some_ore_break")
    public String breakSound() {
        return breakSound;
    }

    public void breakSound(String breakSound) {
        this.breakSound = breakSound;
    }

    @Subst("example:some_ore_place")
    public String placeSound() {
        return placeSound;
    }

    public void placeSound(String placeSound) {
        this.placeSound = placeSound;
    }

    public Material iconMaterial() {
        return iconMaterial;
    }

    public void iconMaterial(Material iconMaterial) {
        this.iconMaterial = iconMaterial;
    }

    public String dropsFile() {
        return dropsFile;
    }

    public void dropsFile(String dropsFile) {
        this.dropsFile = dropsFile;
    }

}
