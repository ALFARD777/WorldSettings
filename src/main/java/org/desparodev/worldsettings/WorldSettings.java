package org.desparodev.worldsettings;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Objects;
import java.util.logging.Level;

public final class WorldSettings extends JavaPlugin {
    @Override
    public void onEnable() {
        SettingsCommand sc = new SettingsCommand();
        Objects.requireNonNull(getCommand("settings")).setExecutor(sc);
        Objects.requireNonNull(getCommand("reloadSB")).setExecutor(sc);
        getServer().getPluginManager().registerEvents(sc, this);
        getLogger().log(Level.INFO, "Запущен!");
    }
    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "Отключен!");
    }
}
