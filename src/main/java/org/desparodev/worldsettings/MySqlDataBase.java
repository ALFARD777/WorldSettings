package org.desparodev.worldsettings;

import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
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
            ResultSet condition = connection.prepareStatement("SELECT COUNT(*) FROM `scoreboards` WHERE `world_name` = '" + worldName + "'").executeQuery();
            if (condition.next()) {
                if (condition.getInt("COUNT(*)") != 0) {
                    connection.createStatement().executeUpdate("UPDATE `scoreboards` SET `scoreboard_content` = '" + sqlString + "' WHERE `world_name` = '" + getWorldName(player) + "'");
                } else {
                    connection.createStatement().executeUpdate("INSERT INTO `scoreboards` (world_name, scoreboard_content) VALUES ('" + getWorldName(player) + "', '" + sqlString + "')");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteScoreboardTable(String worldName) {
        try {
            connection.createStatement().executeUpdate("DELETE FROM `scoreboards` WHERE world_name = '" + worldName + "'");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getWorldName(Player player) {
        try {
            ResultSet result = connection.prepareStatement("SELECT `world_name` FROM `realm_worlds` WHERE `owner_name` = '" + player.getName() + "'").executeQuery();
            if (result.next()) {
                return result.getString("world_name");
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getWorldOwnerName(String worldName) {
        try {
            ResultSet result = connection.prepareStatement("SELECT `owner_name` FROM `realm_worlds` WHERE `world_name` = '" + worldName + "'").executeQuery();
            if (result.next()) {
                return result.getString("owner_name");
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getScoreboardContent(Player player) {
        try {
            ResultSet result = connection.prepareStatement("SELECT `scoreboard_content` FROM `scoreboards` WHERE `world_name` = '" + getWorldName(player) + "'").executeQuery();
            if (result.next()) {
                if (result.getString("scoreboard_content").contains("%%")) {
                    return new ArrayList<>(Arrays.asList(result.getString("scoreboard_content").split("%%")));
                }
            }
            return new ArrayList<>();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}