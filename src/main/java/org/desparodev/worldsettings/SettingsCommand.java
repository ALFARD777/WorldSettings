package org.desparodev.worldsettings;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.bukkit.ChatColor.*;

public class SettingsCommand implements CommandExecutor, Listener {
    List<String> scoreboardContent = new ArrayList<>();
    private ItemStack infoBook = new ItemStack(Material.BOOK);
    private final ItemStack scoreboardEditorItem;
    private final ItemStack eventEditorItem;
    private final ItemStack backToMainMenuArrow;
    private final ItemStack backToScoreboardEditorMenuArrow;
    private ItemStack scoreBoardEditorToDefault = new ItemStack(Material.TNT);
    private ItemStack addScoreboardItem = new ItemStack(Material.PAPER);
    private ItemStack blankLineItem = new ItemStack(Material.PAPER);
    private ItemStack customLineItem = new ItemStack(Material.MAP);
    private ItemStack realmNameLineItem = new ItemStack(Material.OAK_SIGN);
    private ItemStack eventPlayerJoin = new ItemStack(Material.OAK_DOOR);

    SettingsCommand() {
        this.scoreboardEditorItem = createItem(Material.FILLED_MAP, GREEN + "Редактировать Scoreboard", GRAY + "Редактирование текста в панели справа");
        this.eventEditorItem = createItem(Material.COBWEB, GREEN + "Редактировать события", GRAY + "Редактирование действий при происходящем");
        this.backToMainMenuArrow = createItem(Material.ARROW, YELLOW + "Назад", GRAY + "Вернуться в главное меню");
        this.backToScoreboardEditorMenuArrow = createItem(Material.ARROW, YELLOW + "Назад", GRAY + "Вернуться в меню редактирования");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player) {
            showMainInventory((Player) sender);
        }
        return true;
    }

    public void showMainInventory(Player player) {
        Inventory settingsMenu = player.getServer().createInventory(null, 54, "Настройки Realm");
        settingsMenu.setItem(10, scoreboardEditorItem);
        settingsMenu.setItem(11, eventEditorItem);
        player.openInventory(settingsMenu);
    }

    public void showScoreBoardEditorMenu(Player player) {
        Inventory scoreboardEditorMenu = player.getServer().createInventory(null, 54, "Настройки Scoreboard");
        scoreboardEditorMenu.setItem(49, backToMainMenuArrow);
        scoreBoardEditorToDefault = createItem(Material.TNT, RED + "Очистить Scoreboard", GRAY + "Очищает весь Scoreboard");
        scoreboardEditorMenu.setItem(48, scoreBoardEditorToDefault);
        addScoreboardItem = createItem(Material.PAPER, GREEN + "Добавить строку", GRAY + "Добавляет строку к Scoreboard");
        scoreboardEditorMenu.setItem(50, addScoreboardItem);
        List<String> bookLore = new ArrayList<>();
        bookLore.add(GRAY + "Позволяет вам редактировать отображаемую");
        bookLore.add(GRAY + "справа экрана панель, но только до");
        bookLore.add(GRAY + "10 строк — это ограничение Minecraft");
        infoBook = createItem(Material.BOOK, YELLOW + "Настройки Scoreboard", bookLore);
        scoreboardEditorMenu.setItem(53, infoBook);
        player.openInventory(scoreboardEditorMenu);
    }

    public void showEventEditorMenu(Player player) {
        Inventory eventEditorMenu = player.getServer().createInventory(null, 54, "Настройки событий");
        eventEditorMenu.setItem(49, backToMainMenuArrow);
        eventPlayerJoin = createItem(Material.OAK_DOOR, GREEN + "Подключение игрока", GRAY + "Происходит, когда игрок заходит на сервер");
        eventEditorMenu.setItem(10, eventPlayerJoin);

        player.openInventory(eventEditorMenu);
    }

    @NotNull
    private static ItemStack createItem(Material material, String displayName, String lore) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(displayName);

        List<String> itemLore = new ArrayList<>();
        itemLore.add(lore);
        itemMeta.setLore(itemLore);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @NotNull
    private static ItemStack createItem(Material material, String displayName, List<String> lore) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(lore);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null) {
            Player player = (Player) event.getWhoClicked();
            if (event.getCurrentItem() != null && event.getCurrentItem().isSimilar(infoBook)) {
                event.setCancelled(true);
            }
            if (event.getCurrentItem() != null && event.getCurrentItem().isSimilar(backToMainMenuArrow)) {
                event.setCancelled(true);
                showMainInventory(player);
            }
            if (event.getCurrentItem() != null && event.getCurrentItem().isSimilar(backToScoreboardEditorMenuArrow)) {
                event.setCancelled(true);
                showScoreBoardEditorMenu(player);
            }
            if (event.getCurrentItem() != null && event.getCurrentItem().isSimilar(scoreboardEditorItem)) {
                event.setCancelled(true);
                showScoreBoardEditorMenu(player);
            }
            if (event.getCurrentItem() != null && event.getCurrentItem().isSimilar(scoreBoardEditorToDefault)) {
                event.setCancelled(true);
            }
            if (event.getCurrentItem() != null && event.getCurrentItem().isSimilar(addScoreboardItem)) {
                event.setCancelled(true);
                Inventory addScoreboardLineMenu = player.getServer().createInventory(null, 54, "Добавить линию");

                blankLineItem = createItem(Material.PAPER, GREEN + "Пустая строка", getBlankLineItemLore());
                addScoreboardLineMenu.setItem(10, blankLineItem);
                customLineItem = createItem(Material.MAP, GREEN + "Произвольная строка", getCustomLineItemLore());
                addScoreboardLineMenu.setItem(11, customLineItem);
                realmNameLineItem = createItem(Material.OAK_SIGN, GREEN + "Название реалма", getRealmNameLineItemLore());
                addScoreboardLineMenu.setItem(12, realmNameLineItem);
                addScoreboardLineMenu.setItem(49, backToScoreboardEditorMenuArrow);
                player.openInventory(addScoreboardLineMenu);
            }
            if (event.getCurrentItem() != null && event.getCurrentItem().isSimilar(blankLineItem)) {
                event.setCancelled(true);
                scoreboardContent.add(WHITE + " ");
                updateScoreboard(player);
                player.sendMessage(GREEN + "Пустая строка успешно добавлена!");
            }
            if (event.getCurrentItem() != null && event.getCurrentItem().isSimilar(customLineItem)) {
                event.setCancelled(true);
                scoreboardContent.add(WHITE + "Привет, мир!");
                updateScoreboard(player);
                player.sendMessage(GREEN + "Произвольная строка успешно добавлена!");
            }
            if (event.getCurrentItem() != null && event.getCurrentItem().isSimilar(realmNameLineItem)) {
                event.setCancelled(true);
                scoreboardContent.add(WHITE + "RealmName");
                updateScoreboard(player);
                player.sendMessage(GREEN + "Название сервера успешно добавлено!");
            }
        }
    }

    @NotNull
    private static List<String> getBlankLineItemLore() {
        List<String> list = new ArrayList<>();
        list.add(DARK_GRAY + "1 строка");
        list.add(" ");
        list.add(GRAY + "Просто пустая строка");
        list.add(" ");
        return list;
    }
    @NotNull
    private static List<String> getCustomLineItemLore() {
        List<String> list = new ArrayList<>();
        list.add(DARK_GRAY + "1 строка");
        list.add(" ");
        list.add(GRAY + "Настраиваемая строка");
        list.add(" ");
        list.add(GRAY + "Пример:");
        list.add(WHITE + "Привет, мир!");
        list.add(" ");
        return list;
    }

    @NotNull
    private static List<String> getRealmNameLineItemLore() {
        List<String> list = new ArrayList<>();
        list.add(DARK_GRAY + "1 строка");
        list.add(" ");
        list.add(GRAY + "Отображает имя вашего реалма");
        list.add(" ");
        list.add(GRAY + "Пример:");
        list.add(WHITE + "Server");
        list.add(" ");
        return list;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        createNewScoreboard(event.getPlayer());
    }

    private void createNewScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = addPlayerLines(scoreboard.registerNewObjective("SCOREBOARD", "dummy"));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(YELLOW + "" + BOLD + "REALM");
        objective.getScore(GRAY + new SimpleDateFormat("dd/MM/yy").format(new Date())).setScore(90);
        objective.getScore(WHITE + " ").setScore(80);
        objective.getScore(WHITE + "").setScore(1);
        objective.getScore(YELLOW + "easy-realm.ru").setScore(0);
        player.setScoreboard(scoreboard);
    }

    private void updateScoreboard(Player player) {
        player.sendMessage(scoreboardContent.toString());
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = addPlayerLines(scoreboard.registerNewObjective("SCOREBOARD", "dummy"));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(YELLOW + "" + BOLD + "REALM");
        objective.getScore(GRAY + new SimpleDateFormat("dd/MM/yy").format(new Date())).setScore(90);
        objective.getScore(WHITE + " ").setScore(80);
        objective.getScore(WHITE + "").setScore(1);
        objective.getScore(YELLOW + "easy-realm.ru").setScore(0);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.setScoreboard(scoreboard);
        }
    }

    private Objective addPlayerLines(Objective objective) {
        if (!scoreboardContent.isEmpty()) {
            int scoreValue = scoreboardContent.size() + 1;
            for (String content : scoreboardContent) {
                objective.getScore(WHITE + content).setScore(scoreValue);
                scoreValue--;
            }
        }
        return objective;
    }
}