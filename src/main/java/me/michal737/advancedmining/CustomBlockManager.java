package me.michal737.advancedmining;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class CustomBlockManager {

    private static ArrayList<CustomBlock> customBlocks;

    public static void updateBlockList(){

        File[] customBlockFiles = AdvancedMining.getCustomBlocksFolder().listFiles();
        if (customBlockFiles == null) return;
        customBlocks = new ArrayList<>();

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

    public static void setBlock(String blockName, Block block){

        BlockDataStorage.setData(block, "advancedmining", "block_name", blockName);
        block.setType(Objects.requireNonNull(getBlock(blockName)).getMaterial());
        //todo: fix block database
        //BlockDataStorage.addBlockToDatabase(block);

    }

    public static void setBlock(@NotNull CustomBlock customBlock, Block block){

        BlockDataStorage.setData(block, "advancedmining", "block_name", customBlock.getName());
        block.setType(customBlock.getMaterial());
        //BlockDataStorage.addBlockToDatabase(block);

    }

    public static void removeBlock(Block block){

        BlockDataStorage.removeData(block, "advancedmining", "block_name");

    }

    public static ArrayList<CustomBlock> getCustomBlocks() {
        return customBlocks;
    }

    public static @NotNull ArrayList<String> getCustomBlockNames(){

        ArrayList<String> names = new ArrayList<>();
        for (CustomBlock customBlock : customBlocks) names.add(customBlock.getName());
        return names;

    }

    public static String[] getCustomBlockNamesArray(){

        String[] names = new String[customBlocks.size()];
        for (int i = 0; i <= customBlocks.size(); i++){
            names[i] = customBlocks.get(i).getName();
        }

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

    @Contract(pure = true)
    public static @NotNull ArgumentSuggestions<CommandSender> getAllBlocksArgumentSuggestions(){

        return ArgumentSuggestions.stringsAsync(suggestionInfo -> CompletableFuture.supplyAsync(CustomBlockManager::getCustomBlockNamesArray));

    }

}
