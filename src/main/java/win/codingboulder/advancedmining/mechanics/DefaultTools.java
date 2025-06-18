package win.codingboulder.advancedmining.mechanics;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Material;
import win.codingboulder.advancedmining.AdvancedMining;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;

public class DefaultTools {

    public static final File defaultToolsFile = new File(AdvancedMining.getInstance().getDataFolder(), "default-tools.json");
    private static HashMap<Material, Tool> defaultTools = new HashMap<>();

    private static final Type type = new TypeToken<HashMap<Material, Tool>>() {}.getType();

    public static void loadFromFile() {

        if (!defaultToolsFile.exists()) return;
        try (FileReader reader = new FileReader(defaultToolsFile)) {

            defaultTools = new Gson().fromJson(reader, type);

        } catch (IOException e) {
            AdvancedMining.getInstance().getLogger().warning("Could not load default tools from file!\n" + e);
        }

    }

    public static void saveToFile() {

        try (FileWriter writer = new FileWriter(defaultToolsFile)) {

            //noinspection ResultOfMethodCallIgnored
            defaultToolsFile.createNewFile();
            new GsonBuilder().setPrettyPrinting().create().toJson(defaultTools, type, writer);

        } catch (IOException e) {
            AdvancedMining.getInstance().getLogger().warning("Could not save default tools to file!\n" + e);
        }

    }

    public static Tool getDefaultMapping(Material material) {
        return defaultTools.get(material);
    }

    public static void addDefaultTool(Material tool, float miningSpeed, int breakingPower, String toolType) {
        defaultTools.put(tool, new Tool(miningSpeed, breakingPower, toolType));
    }

    public static HashMap<Material, Tool> defaultTools() {
        return defaultTools;
    }

    public static class Tool {

        private float miningSpeed;
        private int breakingPower;
        private String toolType;

        public Tool(float miningSpeed, int breakingPower, String toolType) {
            this.miningSpeed = miningSpeed;
            this.breakingPower = breakingPower;
            this.toolType = toolType;
        }

        public float miningSpeed() {
            return miningSpeed;
        }

        public void setMiningSpeed(float miningSpeed) {
            this.miningSpeed = miningSpeed;
        }

        public int breakingPower() {
            return breakingPower;
        }

        public void setBreakingPower(int breakingPower) {
            this.breakingPower = breakingPower;
        }

        public String toolType() {
            return toolType;
        }

        public void setToolType(String toolType) {
            this.toolType = toolType;
        }

    }

}
