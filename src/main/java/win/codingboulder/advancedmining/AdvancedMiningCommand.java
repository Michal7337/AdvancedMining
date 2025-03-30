package win.codingboulder.advancedmining;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class AdvancedMiningCommand {

    public AdvancedMiningCommand(@NotNull JavaPlugin plugin) {

        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> event.registrar().register(

            Commands.literal("advmining")



                .build()

        ));

    }

}
