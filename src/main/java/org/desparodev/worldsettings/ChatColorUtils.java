package org.desparodev.worldsettings;

import org.bukkit.ChatColor;

public class ChatColorUtils {
    public static String formatColor(String format) {
        return ChatColor.translateAlternateColorCodes('&', format);
    }
}
