package org.desparodev.worldsettings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class Players {
    public static Map<String, List<String>> playersSettings = new HashMap<>();

    public static void addNewPlayer(String name) {
        playersSettings.put(name, new ArrayList<>());
    }
    public static void enableElement(String name, String value) {
        if (!playersSettings.containsKey(name)) addNewPlayer(name);
        if (!playersSettings.get(name).contains(value)) {
            playersSettings.get(name).add(value);
        }

    }
    public static void updateElements(String name) {
        List<String> playerSettings = playersSettings.get(name);
        for (String playerSetting : playerSettings) {
            if (playerSetting.contains("speed-boost")) {
                WorldSettingsProvider.getPlugin() playerSetting.split(":");
            }
        }
    }
}
