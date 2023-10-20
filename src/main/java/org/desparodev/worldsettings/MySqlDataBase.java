package org.desparodev.worldsettings;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MySqlDataBase {
    public static Connection connection;

    MySqlDataBase(String host, int port, String database, String username, String password) {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createScoreboardTable() throws SQLException {
        connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS `scoreboards` (world_name VARCHAR(48) PRIMARY KEY, scoreboard_content LONGTEXT(660))");
    }

    public static void writeScoreboardTable(List<String> contents, String worldName, Player player) throws SQLException {
        StringBuilder sqlString = new StringBuilder();
        for (String content : contents) {
            sqlString.append(content).append("%%");
        }
        if (connection.prepareStatement("SELECT COUNT(*) FROM `scoreboards` WHERE `world_name` = " + worldName).executeQuery().getInt("COUNT(*)") != 0) {
            connection.createStatement().executeUpdate("UPDATE `scoreboards` SET `scoreboards_content` = \"" + sqlString + "\" WHERE `world_name` = " + getWorldName(player));
        } else {
            connection.createStatement().executeUpdate("INSERT INTO `scoreboards` (world_name, scoreboard_content) VALUES (" + getWorldName(player) + ", " + sqlString + ")");
        }
    }

    public static String getWorldName(Player player) throws SQLException {
        return connection.prepareStatement("SELECT `world_name` FROM `realm_worlds` WHERE `owner_name` = " + player.getName()).executeQuery().getString("world_name");
    }

    public static String getWorldOwnerName(String worldName) throws SQLException {
        return connection.prepareStatement("SELECT `owner_name` FROM `realm_worlds` WHERE `world_name` = " + worldName).executeQuery().getString("owner_name");
    }

    public static List<String> getScoreboardContent(Player player) throws SQLException {
        return Arrays.asList(connection.prepareStatement("SELECT `scoreboard_content` FROM `scoreboards` WHERE `world_name` = " + getWorldName(player)).executeQuery().getString("scoreboard_content").split("%%"));
    }
}