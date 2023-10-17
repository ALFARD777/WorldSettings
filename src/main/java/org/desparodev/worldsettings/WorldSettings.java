package org.desparodev.worldsettings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

import static org.bukkit.ChatColor.*;

import java.util.Objects;
import java.util.logging.Level;

public final class WorldSettings extends JavaPlugin {
    @Override
    public void onEnable() {
        // Создаем Scoreboard Manager
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();

        // Получаем Scoreboard
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();

        // Создаем или получаем главный Objective (по умолчанию)
        Objective objective = scoreboard.getObjective("main");
        if (objective == null) {
            objective = scoreboard.registerNewObjective("main", "dummy", GREEN + "Scoreboard");
        }
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        // Создаем Scoreboard Team
        Team team = scoreboard.getTeam("teamName");
        if (team == null) {
            team = scoreboard.registerNewTeam("teamName");
        }
        // Настройка Team, например, цвет
        team.setPrefix(GREEN.toString());

        // Установка Scoreboard для всех игроков
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(scoreboard);
        }

        getLogger().info("Scoreboard создан и установлен!");
        Objects.requireNonNull(getCommand("settings")).setExecutor(new SettingsCommand());
        getServer().getPluginManager().registerEvents(new SettingsCommand(), this);
        getLogger().log(Level.INFO, "Запущен!");
    }
    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "Отключен!");
    }
}
