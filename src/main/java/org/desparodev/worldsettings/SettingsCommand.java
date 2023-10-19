package org.desparodev.worldsettings;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.security.cert.CertPathValidatorException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


import static org.bukkit.ChatColor.*;

public class SettingsCommand implements CommandExecutor, Listener {
    // Контенты
    List<String> scoreboardContent = new ArrayList<>();

    // Общие
    private ItemStack infoBook;
    private ItemStack backToMainMenuArrow = new ItemStack(Material.BARRIER);
    private ItemStack emptyGlass = new ItemStack(Material.BARRIER);

    // Стрелки возврата
    private ItemStack backToScoreboardEditorMenuArrow = new ItemStack(Material.BARRIER);

    // Все меню
    private Inventory scoreboardEditorMenu;

    // Главное меню
    private ItemStack quitMenuItem = new ItemStack(Material.BARRIER);
    private ItemStack scoreboardEditorItem = new ItemStack(Material.BARRIER);
    private ItemStack eventEditorItem = new ItemStack(Material.BARRIER);

    // Редактор Scoreboard
    private ItemStack scoreBoardEditorToDefault = new ItemStack(Material.BARRIER);
    private final int scoreItemPosition = 10;
    private ItemStack addScoreboardItem = new ItemStack(Material.BARRIER);
    private ItemStack blankLineItem = new ItemStack(Material.BARRIER);
    private ItemStack customLineItem = new ItemStack(Material.BARRIER);
    private ItemStack realmNameLineItem = new ItemStack(Material.BARRIER);
    private final List<ChatColor> colorsList = new ArrayList<>(Arrays.asList(
            BLACK, DARK_BLUE, DARK_GREEN,
            DARK_AQUA, DARK_RED, DARK_PURPLE,
            GOLD, DARK_GRAY, BLUE,
            GREEN, AQUA, RED, LIGHT_PURPLE
    ));

    // Редактор событий
    private ItemStack eventPlayerJoin = new ItemStack(Material.BARRIER);
    private ItemStack eventPlayerQuit = new ItemStack(Material.BARRIER);
    private ItemStack eventPlayerDeath = new ItemStack(Material.BARRIER);
    private ItemStack eventPlayerKill = new ItemStack(Material.BARRIER);
    private ItemStack eventPlayerRespawn = new ItemStack(Material.BARRIER);
    private ItemStack eventFishCaught = new ItemStack(Material.BARRIER);
    private ItemStack eventPlayerEnterPortal = new ItemStack(Material.BARRIER);
    private ItemStack eventPlayerDamage = new ItemStack(Material.BARRIER);
    private ItemStack eventBlockBreak = new ItemStack(Material.BARRIER);


    // Подтверждение
    private ItemStack applyActionYes = new ItemStack(Material.BARRIER);
    private ItemStack applyActionNo = new ItemStack(Material.BARRIER);
    private String applyAction = "";


    // КОНСТРУКТОР
    SettingsCommand() {
        this.scoreboardEditorMenu = createScoreBoardEditorMenu();
        this.quitMenuItem = createItem(Material.DARK_OAK_DOOR, RED + "Выйти из меню настроек");
        this.scoreboardEditorItem = createItem(Material.FILLED_MAP, GREEN + "Редактировать Scoreboard", GRAY + "Редактирование текста в панели справа");
        this.eventEditorItem = createItem(Material.COBWEB, GREEN + "Редактировать события", GRAY + "Редактирование действий при происходящем");
        this.backToScoreboardEditorMenuArrow = createItem(Material.ARROW, YELLOW + "Назад", GRAY + "Вернуться в меню редактирования");
    }

    // ЗАПУСК КОМАНДЫ
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player) {
            showMainInventory((Player) sender);
        }
        return true;
    }

    // ВСЕ НАЖАТИЯ ПО ИНВЕНТАРЯМ
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getCurrentItem() != null) {
            Player player = (Player) event.getWhoClicked();
            ItemStack item = event.getCurrentItem();

            // ОБЩЕЕ
            // Книга описания
            if (item.isSimilar(infoBook)) {
                event.setCancelled(true);
            }
            // Возврат в главное меню
            if (item.isSimilar(backToMainMenuArrow)) {
                event.setCancelled(true);
                showMainInventory(player);
            }

            // ГЛАВНОЕ МЕНЮ
            // Выход из меню
            if (item.isSimilar(quitMenuItem)) {
                event.setCancelled(true);
                player.closeInventory(InventoryCloseEvent.Reason.PLAYER);
            }
            // Открытие редактора ScoreBoard
            if (item.isSimilar(scoreboardEditorItem)) {
                event.setCancelled(true);
                player.openInventory(scoreboardEditorMenu);
            }
            // Открытие редактора событий
            if (item.isSimilar(eventEditorItem)) {
                event.setCancelled(true);
                showEventEditorMenu(player);
            }

            // РЕДАКТОР SCOREBOARD
            // Назад в редактор Scoreboard
            if (item.isSimilar(backToScoreboardEditorMenuArrow)) {
                event.setCancelled(true);
                player.openInventory(scoreboardEditorMenu);
            }
            // Возврат к стандартным
            if (item.isSimilar(scoreBoardEditorToDefault)) {
                event.setCancelled(true);
                applyAction = "ScoreBoardToDefault";
                showApplyActionMenu(player);
            }
            // Подменю - добавить строку в Scoreboard
            if (item.isSimilar(addScoreboardItem)) {
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
            // Добавить пустую строку
            if (item.isSimilar(blankLineItem)) {
                event.setCancelled(true);
                if (scoreboardContent.size() < 10) {
                    List<String> lore = new ArrayList<>();
                    lore.add(YELLOW + "Нажмите ПКМ для удаления");
                    lore.add(GRAY + "Используйте SHIFT + ПКМ/ЛКМ для смены позиции");
                    scoreboardEditorMenu.setItem(9 + scoreboardContent.size(), new ItemStackBuilder(createItem(Material.PAPER, GREEN + "Пустая строка", lore)).setUnbreakable(true).build());
                    int clr = ThreadLocalRandom.current().nextInt(colorsList.size());
                    scoreboardContent.add(colorsList.get(clr) + " ");
                    colorsList.remove(clr);
                    updateScoreboards();
                    player.openInventory(scoreboardEditorMenu);
                    player.sendMessage(GREEN + "Пустая строка успешно добавлена!");
                }
            }
            // Добавить кастомную строку
            if (item.isSimilar(customLineItem)) {
                event.setCancelled(true);
                if (scoreboardContent.size() < 10) {
                    List<String> lore = new ArrayList<>();
                    lore.add(GRAY + "Изменение настроек строки");
                    lore.add(YELLOW + "Нажмите ЛКМ для редактирования");
                    lore.add(YELLOW + "Нажмите ПКМ для удаления");
                    lore.add(GRAY + "Используйте SHIFT + ПКМ/ЛКМ для смены позиции");
                    scoreboardEditorMenu.setItem(9 + scoreboardContent.size(), new ItemStackBuilder(createItem(Material.MAP, GREEN + "Произвольная строка", lore)).setUnbreakable(true).build());
                    int clr = ThreadLocalRandom.current().nextInt(colorsList.size());
                    scoreboardContent.add(colorsList.get(clr) + "" + RESET + "Привет, мир!");
                    colorsList.remove(clr);
                    updateScoreboards();
                    player.openInventory(scoreboardEditorMenu);
                    player.sendMessage(GREEN + "Произвольная строка успешно добавлена!");
                }
            }
            // Добавить строку названия реалма
            if (item.isSimilar(realmNameLineItem)) {
                event.setCancelled(true);
                if (scoreboardContent.size() < 10) {
                    List<String> lore = new ArrayList<>();
                    lore.add(YELLOW + "Нажмите ПКМ для удаления");
                    lore.add(GRAY + "Используйте SHIFT + ПКМ/ЛКМ для смены позиции");
                    scoreboardEditorMenu.setItem(9 + scoreboardContent.size(), new ItemStackBuilder(createItem(Material.OAK_SIGN, GREEN + "Название сервера", lore)).setUnbreakable(true).build());
                    int clr = ThreadLocalRandom.current().nextInt(colorsList.size());
                    scoreboardContent.add(colorsList.get(clr) + "" + RESET + "RealmName");
                    colorsList.remove(clr);
                    updateScoreboards();
                    player.openInventory(scoreboardEditorMenu);
                    player.sendMessage(GREEN + "Название сервера успешно добавлено!");
                }
            }
            // Удаление
            if (item.getItemMeta().isUnbreakable() && event.isRightClick() && !event.isShiftClick()) {
                event.setCancelled(true);
                Inventory inventory = event.getClickedInventory();
                inventory.setItem(event.getSlot(), null);
                int row = event.getSlot() / 9;
                int startSlot = row * 9;
                int endSlot = row * 9 + 8;
                for (int slot = event.getSlot() + 1; slot <= endSlot; slot++) {
                    ItemStack nextItem = inventory.getItem(slot);
                    if (nextItem != null) {
                        inventory.setItem(slot - 1, nextItem);
                        inventory.setItem(slot, null);
                    }
                }
                player.updateInventory();
                player.sendMessage(GREEN + "Компонент успешно удален!");
            }


            if (item.isSimilar(eventPlayerJoin)) {
                event.setCancelled(true);
            }
            if (item.isSimilar(eventPlayerQuit)) {
                event.setCancelled(true);
            }
            if (item.isSimilar(eventPlayerDeath)) {
                event.setCancelled(true);
            }
            if (item.isSimilar(eventPlayerKill)) {
                event.setCancelled(true);
            }
            if (item.isSimilar(eventPlayerRespawn)) {
                event.setCancelled(true);
            }
            if (item.isSimilar(eventFishCaught)) {
                event.setCancelled(true);
            }
            if (item.isSimilar(eventPlayerEnterPortal)) {
                event.setCancelled(true);
            }
            if (item.isSimilar(eventPlayerDamage)) {
                event.setCancelled(true);
            }
            if (item.isSimilar(eventBlockBreak)) {
                event.setCancelled(true);
            }


            // МЕНЮ ПОДТВЕРЖДЕНИЯ ДЕЙСТВИЯ
            // Если подтвердил
            if (item.isSimilar(applyActionYes)) {
                event.setCancelled(true);
                switch (applyAction) {
                    case "ScoreBoardToDefault":
                        applyAction = "";
                        player.openInventory(scoreboardEditorMenu);
                        scoreboardContent.clear();
                        updateScoreboards();
                        for (int i = 10; i < 45; i++) {
                            scoreboardEditorMenu.clear(i);
                        }
                        break;
                }
            }
            // Если отменил
            if (item.isSimilar(applyActionNo)) {
                event.setCancelled(true);
                switch (applyAction) {
                    case "ScoreBoardToDefault":
                        applyAction = "";
                        player.openInventory(scoreboardEditorMenu);
                        break;
                }
            }
            // Стекло пустоты
            if (item.isSimilar(emptyGlass)) {
                event.setCancelled(true);
            }
        }
    }

    // Получение лора для пустой строки Scoreboard
    @NotNull
    private List<String> getBlankLineItemLore() {
        List<String> list = new ArrayList<>();
        list.add(DARK_GRAY + "1 строка");
        list.add(" ");
        list.add(GRAY + "Просто пустая строка");
        if (scoreboardContent.size() >= 10) list.add(RED + "Scoreboard полон!");
        return list;
    }

    // Получение лора для кастомной строки Scoreboard
    @NotNull
    private List<String> getCustomLineItemLore() {
        List<String> list = new ArrayList<>();
        list.add(DARK_GRAY + "1 строка");
        list.add(" ");
        list.add(GRAY + "Настраиваемая строка");
        list.add(" ");
        list.add(GRAY + "Пример:");
        list.add(WHITE + "Привет, мир!");
        if (scoreboardContent.size() >= 10) list.add(RED + "Scoreboard полон!");
        return list;
    }

    // Получение лора для строки Scoreboard с названием сервера
    @NotNull
    private List<String> getRealmNameLineItemLore() {
        List<String> list = new ArrayList<>();
        list.add(DARK_GRAY + "1 строка");
        list.add(" ");
        list.add(GRAY + "Отображает имя вашего реалма");
        list.add(" ");
        list.add(GRAY + "Пример:");
        list.add(WHITE + "Server");
        if (scoreboardContent.size() >= 10) list.add(RED + "Scoreboard полон!");
        return list;
    }

    // ВСЕ СОБЫТИЯ
    // Подключение игрока
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        createNewScoreboard(event.getPlayer());
    }


    // ПРОЧЕЕ
    // Отображение главного меню
    public void showMainInventory(Player player) {
        Inventory settingsMenu = player.getServer().createInventory(null, 54, "Настройки Realm");
        emptyGlass = createItem(Material.BLACK_STAINED_GLASS_PANE, WHITE + " ");
        for (int i = 0; i < 54; i++) {
            settingsMenu.setItem(i, emptyGlass);
        }
        settingsMenu.setItem(49, quitMenuItem);
        settingsMenu.setItem(10, scoreboardEditorItem);
        settingsMenu.setItem(11, eventEditorItem);
        player.openInventory(settingsMenu);
    }

    // Создание редактора Scoreboard
    public Inventory createScoreBoardEditorMenu() {
        scoreboardEditorMenu = Bukkit.getServer().createInventory(null, 54, "Настройки Scoreboard");
        emptyGlass = createItem(Material.BLACK_STAINED_GLASS_PANE, WHITE + " ");
        for (int i = 0; i < 54; i++) {
            scoreboardEditorMenu.setItem(i, emptyGlass);
        }
        backToMainMenuArrow = createItem(Material.ARROW, YELLOW + "Назад", GRAY + "Вернуться в главное меню");
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
        return scoreboardEditorMenu;
    }

    // Отображение редактора событий
    public void showEventEditorMenu(Player player) {
        Inventory eventEditorMenu = player.getServer().createInventory(null, 54, "Настройки событий");
        emptyGlass = createItem(Material.BLACK_STAINED_GLASS_PANE, WHITE + " ");
        for (int i = 0; i < 54; i++) {
            eventEditorMenu.setItem(i, emptyGlass);
        }
        backToMainMenuArrow = createItem(Material.ARROW, YELLOW + "Назад", GRAY + "Вернуться в главное меню");
        eventEditorMenu.setItem(49, backToMainMenuArrow);
        eventPlayerJoin = createItem(Material.OAK_DOOR, GREEN + "Подключение игрока", GRAY + "Происходит, когда игрок заходит на сервер");
        eventPlayerQuit = createItem(Material.RED_BED, GREEN + "Выход игрока", GRAY + "Происходит, когда игрок выходит с сервера");
        eventPlayerDeath = createItem(Material.DIAMOND_SWORD, GREEN + "Смерть игрока", GRAY + "Происходит, когда игрок умирает");
        eventPlayerKill = createItem(Material.REDSTONE, GREEN + "Убийство игрока", GRAY + "Происходит, когда игрок убивает другого игрока");
        eventPlayerRespawn = createItem(Material.GOLDEN_APPLE, GREEN + "Возрождение игрока", GRAY + "Происходит, когда игрок возрождается после смерти");
        eventFishCaught = createItem(Material.FISHING_ROD, GREEN + "Пойманная рыба", GRAY + "Происходит, когда игрок ловит рыбу");
        eventPlayerEnterPortal = createItem(Material.END_PORTAL_FRAME, GREEN + "Вход в портал", GRAY + "Происходит, когда игрок входит в портал");
        eventPlayerDamage = createItem(Material.DIAMOND_CHESTPLATE, GREEN + "Полученный урон", GRAY + "Происходит, когда игрок получает урон");
        eventBlockBreak = createItem(Material.DIAMOND_PICKAXE, GREEN + "Разрушение блока", GRAY + "Происходит, когда игрок разрушает блок");
        eventEditorMenu.setItem(10, eventPlayerJoin);
        eventEditorMenu.setItem(11, eventPlayerQuit);
        eventEditorMenu.setItem(12, eventPlayerDeath);
        eventEditorMenu.setItem(13, eventPlayerKill);
        eventEditorMenu.setItem(14, eventPlayerRespawn);
        eventEditorMenu.setItem(15, eventFishCaught);
        eventEditorMenu.setItem(16, eventPlayerEnterPortal);
        eventEditorMenu.setItem(17, eventPlayerDamage);
        eventEditorMenu.setItem(19, eventBlockBreak);
        player.openInventory(eventEditorMenu);
    }

    // Отображение окна подтверждения
    public void showApplyActionMenu(Player player) {
        Inventory applyActionMenu = player.getServer().createInventory(null, 9, RED + "" + BOLD + "Вы уверены?");
        emptyGlass = createItem(Material.BLACK_STAINED_GLASS_PANE, WHITE + " ");
        for (int i = 0; i < 54; i++) {
            applyActionMenu.setItem(i, emptyGlass);
        }
        applyActionYes = createItem(Material.GREEN_CONCRETE, GREEN + "ДА");
        applyActionNo = createItem(Material.RED_CONCRETE, RED + "НЕТ");
        applyActionMenu.setItem(3, applyActionYes);
        applyActionMenu.setItem(5, applyActionNo);
        player.openInventory(applyActionMenu);
    }


    // Создание нового Scoreboard
    private void createNewScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = addPlayerLines(scoreboard.registerNewObjective("SCOREBOARD", "dummy"));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(YELLOW + "" + BOLD + "REALM");
        objective.getScore(GRAY + new SimpleDateFormat("dd/MM/yy").format(new Date())).setScore(scoreboardContent.size() + 3);
        objective.getScore(WHITE + " ").setScore(scoreboardContent.size() + 2);
        objective.getScore(WHITE + "").setScore(1);
        objective.getScore(YELLOW + "easy-realm.ru").setScore(0);
        player.setScoreboard(scoreboard);
    }

    // Обновление Scoreboard у всех пользователей
    private void updateScoreboards() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = addPlayerLines(scoreboard.registerNewObjective("SCOREBOARD", "dummy"));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(YELLOW + "" + BOLD + "REALM");
        objective.getScore(GRAY + new SimpleDateFormat("dd/MM/yy").format(new Date())).setScore(scoreboardContent.size() + 3);
        objective.getScore(WHITE + " ").setScore(scoreboardContent.size() + 2);
        objective.getScore(WHITE + "").setScore(1);
        objective.getScore(YELLOW + "easy-realm.ru").setScore(0);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.setScoreboard(scoreboard);
        }
    }

    // Добавление пользовательских строк в Scoreboard
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

    // ВСЕ МЕТОДЫ СОЗДАНИЯ ПРЕДМЕТОВ
    @NotNull
    private static ItemStack createItem(Material material, String displayName, String lore) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(displayName);

        List<String> itemLore = new ArrayList<>();
        itemLore.add(lore);
        itemMeta.setLore(itemLore);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @NotNull
    private static ItemStack createItem(Material material, String displayName, List<String> lore) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(lore);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @NotNull
    private static ItemStack createItem(Material material, String displayName) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(displayName);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}