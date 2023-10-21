package org.desparodev.worldsettings;

import org.bukkit.entity.Player;
import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class MySqlDataBase {
    public static Connection connection;

    MySqlDataBase(String jdbc) {
        try {
            connection = DriverManager.getConnection(jdbc);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        createScoreboardTable();
    }

    public static void createScoreboardTable() {
        try {
            connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS `scoreboards` (world_name VARCHAR(48) PRIMARY KEY, scoreboard_content LONGTEXT)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeScoreboardTable(List<String> contents, String worldName, Player player) {
        StringBuilder sqlString = new StringBuilder();
        for (String content : contents) {
            sqlString.append(content).append("%%");
        }
        try {
            if (connection.prepareStatement("SELECT COUNT(*) FROM `scoreboards` WHERE `world_name` = '" + worldName + "'").executeQuery().getInt("COUNT(*)") != 0) {
                connection.createStatement().executeUpdate("UPDATE `scoreboards` SET `scoreboards_content` = \"" + sqlString + "\" WHERE `world_name` = '" + getWorldName(player) + "'");
            } else {
                connection.createStatement().executeUpdate("INSERT INTO `scoreboards` (world_name, scoreboard_content) VALUES ('" + getWorldName(player) + "', '" + sqlString + "')");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getWorldName(Player player) {
        try {
            return connection.prepareStatement("SELECT `world_name` FROM `realm_worlds` WHERE `owner_name` = '" + player.getName()).executeQuery().getString("owner_name" + "'");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getWorldOwnerName(String worldName) {
        try {
            return connection.prepareStatement("SELECT `owner_name` FROM `realm_worlds` WHERE `world_name` = '" + worldName).executeQuery().getString("world_name" + "'");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getScoreboardContent(Player player) {
        try {
            return Arrays.asList(connection.prepareStatement("SELECT `scoreboard_content` FROM `scoreboards` WHERE `world_name` = '" + getWorldName(player) + "'").executeQuery().getString("scoreboard_content").split("%%"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}