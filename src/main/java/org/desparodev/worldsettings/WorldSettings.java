package org.desparodev.worldsettings;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Objects;
import java.util.logging.Level;

public final class WorldSettings extends JavaPlugin {
    @Override
    public void onEnable() {
        Objects.requireNonNull(getCommand("settings")).setExecutor(new SettingsCommand());
        getServer().getPluginManager().registerEvents(new SettingsCommand(), this);
        getLogger().log(Level.INFO, "Запущен!");
    }
    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "Отключен!");
    }
}
