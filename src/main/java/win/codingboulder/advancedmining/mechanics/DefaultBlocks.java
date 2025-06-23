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

/**
 * This class handles default blocks. A default block is a vanilla material mapped to a custom block id.<br>
 * This is useful if you want every block of a material to be a custom block.
 */
public class DefaultBlocks {

    public static final File defaultBlocksFile = new File(AdvancedMining.getInstance().getDataFolder(), "default-blocks.json");
    private static HashMap<Material, String> defaultBlocks = new HashMap<>();

    public static HashMap<Material, String> defaultBlocks() {
        return defaultBlocks;
    }

    /**
     * Gets the custom block mapped to the specified material
     * @param material The material to get the mapping of
     * @return The custom block mapped to this material or {@code null} if there is no default block mapped to this material
     */
    public static @Nullable CustomBlock getDefaultMapping(Material material) {

        if (!defaultBlocks.containsKey(material)) return null;
        return CustomBlock.loadedBlocks().get(defaultBlocks.get(material));

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
