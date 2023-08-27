package me.michal737.advancedmining;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;

@SuppressWarnings("unused")
public class CustomBlockManager {

    private static ArrayList<CustomBlock> customBlocks;

    public static void updateBlockList(){

        File[] customBlockFiles = AdvancedMining.getCustomBlocksFolder().listFiles();
        if (customBlockFiles == null) return;

        for (File customBlockFile : customBlockFiles){

            try {

                FileReader reader = new FileReader(customBlockFile);
                customBlocks.add(new Gson().fromJson(reader, CustomBlock.class));

            } catch (FileNotFoundException e) {throw new RuntimeException(e);}

        }

    }

    public static @Nullable CustomBlock getBlock(String name){

        for (CustomBlock customBlock : customBlocks) if (customBlock.getName().equals(name)) return customBlock;

        return null;

    }

    public static @Nullable CustomBlock getBlockAtLocation(Block block){

        String blockName = BlockDataStorage.getData(block, "advancedmining", "block_name");
        if (blockName == null) return null;

        return getBlock(blockName);

    }

    public static ArrayList<CustomBlock> getCustomBlocks() {
        return customBlocks;
    }

    public static @NotNull ArrayList<String> getCustomBlockNames(){

        ArrayList<String> names = new ArrayList<>();
        for (CustomBlock customBlock : customBlocks) names.add(customBlock.getName());
        return names;

    }

    public static void deleteCustomBlock(String name){

        File customBlockFile = new File(AdvancedMining.getCustomBlocksFolder().getAbsolutePath() + "/" + name + ".json");
        //noinspection ResultOfMethodCallIgnored
        customBlockFile.delete();
        updateBlockList();

    }

    public static void storeBlock(@NotNull CustomBlock customBlock){

        try {

            FileWriter writer = new FileWriter(AdvancedMining.getCustomBlocksFolder().getAbsolutePath() + "/" + customBlock.getName() + ".json");
            new GsonBuilder().setPrettyPrinting().create().toJson(customBlock, writer);
            writer.close();

        } catch (IOException e) {throw new RuntimeException(e);}

        updateBlockList();

    }

}
