package win.codingboulder.advancedmining.mechanics;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;
import win.codingboulder.advancedmining.AdvancedMining;
import win.codingboulder.advancedmining.CustomBlock;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;

public class DefaultBlocks {

    public static final File defaultBlocksFile = new File(AdvancedMining.getInstance().getDataFolder(), "default-blocks.json");
    private static HashMap<Material, String> defaultBlocks = new HashMap<>();

    public static HashMap<Material, String> defaultBlocks() {
        return defaultBlocks;
    }

    public static @Nullable CustomBlock getDefaultMapping(Material material) {

        if (!defaultBlocks.containsKey(material)) return null;
        return CustomBlock.loadedBlocks.get(defaultBlocks.get(material));

    }

    private static final Type type = new TypeToken<HashMap<Material, String>>() {}.getType();

    public static void loadFromFile() {

        if (!defaultBlocksFile.exists()) return;
        try (FileReader reader = new FileReader(defaultBlocksFile)) {

            defaultBlocks = new Gson().fromJson(reader, type);

        } catch (IOException e) {
            AdvancedMining.getInstance().getLogger().warning("Could not load default blocks from file!\n" + e);
        }

    }

    public static void saveToFile() {

        try (FileWriter writer = new FileWriter(defaultBlocksFile)) {

            //noinspection ResultOfMethodCallIgnored
            defaultBlocksFile.createNewFile();
            new GsonBuilder().setPrettyPrinting().create().toJson(defaultBlocks, type, writer);

        } catch (IOException e) {
            AdvancedMining.getInstance().getLogger().warning("Could not save default blocks to file!\n" + e);
        }

    }

}
