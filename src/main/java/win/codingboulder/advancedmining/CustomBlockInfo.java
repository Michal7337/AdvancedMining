package win.codingboulder.advancedmining;

import org.bukkit.Material;

public class CustomBlockInfo {

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

    public String texture() {
        return texture;
    }

    public void texture(String texture) {
        this.texture = texture;
    }

    public String breakSound() {
        return breakSound;
    }

    public void breakSound(String breakSound) {
        this.breakSound = breakSound;
    }

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
