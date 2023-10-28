package org.desparodev.worldsettings;

public class WorldSettingsProvider {
    private static WorldSettings plugin;

    public WorldSettingsProvider() {
    }

    public static WorldSettings getPlugin() {
        return plugin;
    }

    public static void setPlugin(WorldSettings plugin) {
        WorldSettingsProvider.plugin = plugin;
    }
}
