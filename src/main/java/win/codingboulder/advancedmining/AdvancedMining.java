package win.codingboulder.advancedmining;

import org.bukkit.plugin.java.JavaPlugin;

public final class AdvancedMining extends JavaPlugin {

    @Override
    public void onEnable() {

        new AdvancedMiningCommand(this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
