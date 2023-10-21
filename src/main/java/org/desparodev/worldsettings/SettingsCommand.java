package org.desparodev.worldsettings;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.ChatColor.*;

public class SettingsCommand implements CommandExecutor, Listener {
    // Контенты
    List<String> scoreboardContent = new ArrayList<>();

    // Общие
    private ItemStack infoBook = new ItemStack(Material.BARRIER);
    private ItemStack backToMainMenuArrow = new ItemStack(Material.BARRIER);
    private ItemStack emptyGlass = new ItemStack(Material.BARRIER);
    int indexToChange = -1;

    // Стрелки возврата
    private final ItemStack backToScoreboardEditorMenuArrow;

    // Главное меню
    private final ItemStack quitMenuItem;
    private final ItemStack scoreboardEditorItem;
    private final ItemStack eventEditorItem;

    // Редактор Scoreboard
    private Scoreboard scoreboard;
    private ItemStack scoreBoardEditorToDefault = new ItemStack(Material.BARRIER);
    private final int scoreItemPosition = 10;
    private ItemStack addScoreboardItem = new ItemStack(Material.BARRIER);
    private ItemStack blankLineItem = new ItemStack(Material.BARRIER);
    private ItemStack customLineItem = new ItemStack(Material.BARRIER);
    private ItemStack realmNameLineItem = new ItemStack(Material.BARRIER);
    private ItemStack guestsCountLineItem = new ItemStack(Material.BARRIER);
    private ItemStack currentGamemodeLineItem = new ItemStack(Material.BARRIER);
    private ItemStack textChangeOnCustomLine = new ItemStack(Material.BARRIER);

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
    private String currentAction = "";


    // КОНСТРУКТОР
    SettingsCommand() {
        this.quitMenuItem = createItem(Material.DARK_OAK_DOOR, RED + "Выйти из меню настроек");
        this.scoreboardEditorItem = createItem(Material.FILLED_MAP, GREEN + "Редактировать Scoreboard", GRAY + "Редактирование текста в панели справа");
        List<String> lore = new ArrayList<>();
        lore.add(GRAY + "Редактирование действий при происходящем");
        lore.add(" ");
        lore.add(RED + "Доступно в ближайших обновлениях...");
        this.eventEditorItem = createItem(Material.COBWEB, GREEN + "Редактировать события", lore);
        this.backToScoreboardEditorMenuArrow = createItem(Material.ARROW, YELLOW + "Назад", GRAY + "Вернуться в меню редактирования");
    }

    // ЗАПУСК КОМАНДЫ
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player) {
            if (command.getName().equals("settings")) {
                showMainInventory((Player) sender);
            } else if (command.getName().equals("reloadSB")) {
                updateScoreboards((Player) sender);
                ((Player) sender).sendMessage(GREEN + "Scoreboard успешно обновлен!");
            }
        }
        return true;
    }

    // ВСЕ НАЖАТИЯ ПО ИНВЕНТАРЯМ
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getCurrentItem() != null) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack item = event.getCurrentItem();

            // ОБЩЕЕ
            // Возврат в главное меню
            if (item.isSimilar(backToMainMenuArrow)) {
                showMainInventory(player);
            }

            // ГЛАВНОЕ МЕНЮ
            // Выход из меню
            if (item.isSimilar(quitMenuItem)) {
                player.closeInventory(InventoryCloseEvent.Reason.PLAYER);
            }
            // Открытие редактора ScoreBoard
            if (item.isSimilar(scoreboardEditorItem)) {
                showScoreBoardEditorMenu(player);
            }
            // Открытие редактора событий
//            if (item.isSimilar(eventEditorItem)) {
//                showEventEditorMenu(player);
//            }

            // РЕДАКТОР SCOREBOARD
            // Назад в редактор Scoreboard
            if (item.isSimilar(backToScoreboardEditorMenuArrow)) {
                showScoreBoardEditorMenu(player);
            }
            // Возврат к стандартным для Scoreboard
            if (item.isSimilar(scoreBoardEditorToDefault)) {
                currentAction = "ScoreBoardToDefault";
                showApplyActionMenu(player);
            }
            // Подменю - добавить строку в Scoreboard
            if (item.isSimilar(addScoreboardItem)) {
                Inventory addScoreboardLineMenu = player.getServer().createInventory(null, 54, "Добавить линию");
                emptyGlass = createItem(Material.BLACK_STAINED_GLASS_PANE, WHITE + " ");
                for (int i = 0; i < 54; i++) {
                    addScoreboardLineMenu.setItem(i, emptyGlass);
                }
                blankLineItem = createItem(Material.PAPER, GREEN + "Пустая строка", getBlankLineItemLore());
                addScoreboardLineMenu.setItem(10, blankLineItem);
                customLineItem = createItem(Material.MAP, GREEN + "Произвольная строка", getCustomLineItemLore());
                addScoreboardLineMenu.setItem(11, customLineItem);
                realmNameLineItem = createItem(Material.OAK_SIGN, GREEN + "Название реалма", getRealmNameLineItemLore());
                addScoreboardLineMenu.setItem(12, realmNameLineItem);
                guestsCountLineItem = createItem(Material.PLAYER_HEAD, GREEN + "Количество игроков", getGuestsCountLineItemLore());
                addScoreboardLineMenu.setItem(13, guestsCountLineItem);
                currentGamemodeLineItem = createItem(Material.DIAMOND, GREEN + "Текущий игровой режим", getCurrentGamemodeLineItemLore());
                addScoreboardLineMenu.setItem(14, currentGamemodeLineItem);
                addScoreboardLineMenu.setItem(49, backToScoreboardEditorMenuArrow);
                player.openInventory(addScoreboardLineMenu);
            }
            // Добавить пустую строку Scoreboard
            if (item.isSimilar(blankLineItem)) {
                if (scoreboardContent.size() < 10) {
                    scoreboardContent.add("**blankLine**");
                    MySqlDataBase.writeScoreboardTable(scoreboardContent, player.getWorld().getName(), player);
                    updateScoreboards(player);
                    showScoreBoardEditorMenu(player);
                    player.sendMessage(GREEN + "Пустая строка успешно добавлена!");
                }
            }
            // Добавить кастомную строку Scoreboard
            if (item.isSimilar(customLineItem)) {
                if (scoreboardContent.size() < 10) {
                    scoreboardContent.add("Привет, мир!");
                    MySqlDataBase.writeScoreboardTable(scoreboardContent, player.getWorld().getName(), player);
                    updateScoreboards(player);
                    showScoreBoardEditorMenu(player);
                    player.sendMessage(GREEN + "Произвольная строка успешно добавлена!");
                }
            }
            // Добавить строку названия реалма в Scoreboard
            if (item.isSimilar(realmNameLineItem)) {
                if (scoreboardContent.size() < 10) {
                    scoreboardContent.add("**realmName**");
                    MySqlDataBase.writeScoreboardTable(scoreboardContent, player.getWorld().getName(), player);
                    updateScoreboards(player);
                    showScoreBoardEditorMenu(player);
                    player.sendMessage(GREEN + "Название сервера успешно добавлено!");
                }
            }
            if (item.isSimilar(guestsCountLineItem)) {
                if (scoreboardContent.size() < 10) {
                    scoreboardContent.add("**playersCount**");
                    MySqlDataBase.writeScoreboardTable(scoreboardContent, player.getWorld().getName(), player);
                    updateScoreboards(player);
                    showScoreBoardEditorMenu(player);
                    player.sendMessage(GREEN + "Количество игроков успешно добавлено!");
                }
            }
            if (item.isSimilar(currentGamemodeLineItem)) {
                if (scoreboardContent.size() < 10) {
                    scoreboardContent.add("**currentGamemode**");
                    MySqlDataBase.writeScoreboardTable(scoreboardContent, player.getWorld().getName(), player);
                    updateScoreboards(player);
                    showScoreBoardEditorMenu(player);
                    player.sendMessage(GREEN + "Текущий игровой режим успешно добавлен!");
                }
            }
            // Удаление строки Scoreboard
            if (item.getItemMeta().isUnbreakable() && event.isRightClick() && !event.isShiftClick()) {
                Inventory inventory = event.getClickedInventory();
                inventory.setItem(event.getSlot(), null);
                int row = event.getSlot() / 9;
                int startSlot = row * 9;
                int endSlot = inventory.getSize() - 10;
                for (int slot = event.getSlot() + 1; slot <= endSlot; slot++) {
                    ItemStack nextItem = inventory.getItem(slot);
                    if (nextItem != null) {
                        inventory.setItem(slot - 1, nextItem);
                        inventory.setItem(slot, null);
                    }
                }
                emptyGlass = createItem(Material.BLACK_STAINED_GLASS_PANE, WHITE + " ");
                inventory.setItem(endSlot, emptyGlass);
                player.updateInventory();
                if (inventory.contains(scoreBoardEditorToDefault)) {
                    scoreboardContent.remove(event.getSlot() - 9);
                    MySqlDataBase.writeScoreboardTable(scoreboardContent, player.getWorld().getName(), player);
                    updateScoreboards(player);
                }
                player.sendMessage(GREEN + "Компонент успешно удален!");
            }
            // Редактирование произвольной строки Scoreboard
            if (item.getItemMeta().isUnbreakable() && Objects.requireNonNull(item.getLore()).contains(YELLOW + "Нажмите ЛКМ для редактирования") && event.isLeftClick() && !event.isShiftClick()) {
                Inventory inventory = event.getClickedInventory();
                if (inventory.contains(scoreBoardEditorToDefault)) {
                    Inventory editMenu = player.getServer().createInventory(null, 36, "Настройки компонента");
                    emptyGlass = createItem(Material.BLACK_STAINED_GLASS_PANE, WHITE + " ");
                    for (int i = 0; i < 36; i++) {
                        editMenu.setItem(i, emptyGlass);
                    }
                    editMenu.setItem(31, backToScoreboardEditorMenuArrow);
                    List<String> lore = new ArrayList<>();
                    lore.add(" ");
                    lore.add(GRAY + "Текущее значение:");
                    lore.add(scoreboardContent.get(event.getSlot() - 9));
                    textChangeOnCustomLine = createItem(Material.BOOK, GREEN + "Текст", lore);
                    editMenu.setItem(10, textChangeOnCustomLine);
                    player.openInventory(editMenu);
                    indexToChange = event.getSlot() - 9;
                }
            }
            if (item.isSimilar(textChangeOnCustomLine)) {
                player.closeInventory();
                String currentValue = Objects.requireNonNull(item.getLore()).get(2);
                player.sendMessage(GREEN + "Введите текст в чат, который Вы хотите установить или \"UNDO\" для отмены");
                player.sendMessage(MiniMessage.miniMessage().deserialize("<color:gray>[<color:red><hover:show_text:'<color:green>Нажмите с зажатым SHIFT'><insert:" + currentValue + ">Прошлое значение</insert></hover><color:gray>]"));
                currentAction = "ScoreboardCustomLineChange";
                inputMap.put(player, "%waitingInput%");
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
                switch (currentAction) {
                    case "ScoreBoardToDefault":
                        currentAction = "";
                        scoreboardContent.clear();
                        MySqlDataBase.writeScoreboardTable(scoreboardContent, player.getWorld().getName(), player);
                        updateScoreboards(player);
                        showScoreBoardEditorMenu(player);
                        break;
                    case "Coming Soon...":
                        break;
                }
            }
            // Если отменил
            if (item.isSimilar(applyActionNo)) {
                switch (currentAction) {
                    case "ScoreBoardToDefault":
                        currentAction = "";
                        showScoreBoardEditorMenu(player);
                        break;
                    case "Coming Soon...":
                        break;
                }
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

    // Получение лора для строки Scoreboard с количеством игроков
    @NotNull
    private List<String> getGuestsCountLineItemLore() {
        List<String> list = new ArrayList<>();
        list.add(DARK_GRAY + "1 строка");
        list.add(" ");
        list.add(GRAY + "Отображает количество игроков на вашем реалме");
        list.add(" ");
        list.add(GRAY + "Пример:");
        list.add(WHITE + "Игроки: 10");
        if (scoreboardContent.size() >= 10) list.add(RED + "Scoreboard полон!");
        return list;
    }

    // Получение лора для строки Scoreboard с текущим игровым режимом
    @NotNull
    private List<String> getCurrentGamemodeLineItemLore() {
        List<String> list = new ArrayList<>();
        list.add(DARK_GRAY + "1 строка");
        list.add(" ");
        list.add(GRAY + "Отображает текущий игровой режим каждому игроку");
        list.add(" ");
        list.add(GRAY + "Пример:");
        list.add(WHITE + "Режим: " + YELLOW + "CREATIVE");
        if (scoreboardContent.size() >= 10) list.add(RED + "Scoreboard полон!");
        return list;
    }

    // ВСЕ СОБЫТИЯ
    // Подключение игрока
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        updateScoreboards(event.getPlayer());

        // ЧТОБЫ РАБОТАЛ, А НЕ КАМНИ СВОИ ЕБУЧИЕ СТАВИЛ
        Player player = event.getPlayer();
        player.setInvulnerable(true); // Заморозить игрока
        player.setAllowFlight(false); // Запретить полет
        player.setHealth(20); // Установить полное здоровье
        player.setFoodLevel(20); // Установить максимальный уровень голода
        player.setWalkSpeed(0.0f); // Установить скорость ходьбы на 0.0, что предотвращает передвижение
        player.setFlySpeed(0.0f); // Установить скорость полета на 0.0, что предотвращает полет
        player.setJumping(false); // Отключить возможность прыжков
    }

    // Отключение игрока
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        inputMap.remove(event.getPlayer());
    }

    // ПРОЧЕЕ
    // Отображение главного меню
    private void showMainInventory(Player player) {
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
    public void showScoreBoardEditorMenu(Player player) {
        Inventory scoreboardEditorMenu = Bukkit.getServer().createInventory(null, 54, "Настройки Scoreboard");
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
        for (int i = 0; i < scoreboardContent.size(); i++) {
            if (scoreboardContent.get(i).contains("**blankLine")) {
                List<String> lore = new ArrayList<>();
                lore.add(YELLOW + "Нажмите ПКМ для удаления");
                lore.add(GRAY + "Используйте SHIFT + ПКМ/ЛКМ для смены позиции");
                scoreboardEditorMenu.setItem(9 + i, new ItemStackBuilder(createItem(Material.PAPER, GREEN + "Пустая строка", lore)).setUnbreakable(true).build());
            }
            else if (scoreboardContent.get(i).contains("**realmName**")) {
                List<String> lore = new ArrayList<>();
                lore.add(YELLOW + "Нажмите ПКМ для удаления");
                lore.add(GRAY + "Используйте SHIFT + ПКМ/ЛКМ для смены позиции");
                scoreboardEditorMenu.setItem(9 + i, new ItemStackBuilder(createItem(Material.OAK_SIGN, GREEN + "Название сервера", lore)).setUnbreakable(true).build());
            }
            else if (scoreboardContent.get(i).contains("**playersCount**")) {
                List<String> lore = new ArrayList<>();
                lore.add(YELLOW + "Нажмите ПКМ для удаления");
                lore.add(GRAY + "Используйте SHIFT + ПКМ/ЛКМ для смены позиции");
                scoreboardEditorMenu.setItem(9 + i, new ItemStackBuilder(createItem(Material.PLAYER_HEAD, GREEN + "Количество игроков", lore)).setUnbreakable(true).build());
            }
            else if (scoreboardContent.get(i).contains("**currentGamemode**")) {
                List<String> lore = new ArrayList<>();
                lore.add(YELLOW + "Нажмите ПКМ для удаления");
                lore.add(GRAY + "Используйте SHIFT + ПКМ/ЛКМ для смены позиции");
                scoreboardEditorMenu.setItem(9 + scoreboardContent.size(), new ItemStackBuilder(createItem(Material.DIAMOND, GREEN + "Текущий игровой режим", lore)).setUnbreakable(true).build());
            }
            else {
                List<String> lore = new ArrayList<>();
                lore.add(GRAY + "Изменение настроек строки");
                lore.add(YELLOW + "Нажмите ЛКМ для редактирования");
                lore.add(YELLOW + "Нажмите ПКМ для удаления");
                lore.add(GRAY + "Используйте SHIFT + ПКМ/ЛКМ для смены позиции");
                scoreboardEditorMenu.setItem(9 + i, new ItemStackBuilder(createItem(Material.MAP, GREEN + "Произвольная строка", lore)).setUnbreakable(true).build());
            }

        }
        player.openInventory(scoreboardEditorMenu);
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
        List<String> bookLore = new ArrayList<>();
        bookLore.add(GRAY + "Позволяет вам редактировать отображаемую");
        bookLore.add(GRAY + "справа экрана панель, но только до");
        bookLore.add(GRAY + "10 строк — это ограничение Minecraft");
        infoBook = createItem(Material.BOOK, YELLOW + "Настройки Scoreboard", bookLore);
        eventEditorMenu.setItem(53, infoBook);
        player.openInventory(eventEditorMenu);
    }

    // Отображение окна подтверждения
    public void showApplyActionMenu(Player player) {
        Inventory applyActionMenu = player.getServer().createInventory(null, 9, RED + "" + BOLD + "Вы уверены?");
        emptyGlass = createItem(Material.BLACK_STAINED_GLASS_PANE, WHITE + " ");
        for (int i = 0; i < 9; i++) {
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
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = addPlayerLines(scoreboard.registerNewObjective("SCOREBOARD", "dummy"), player);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(YELLOW + "" + BOLD + "REALM");
        objective.getScore(GRAY + new SimpleDateFormat("dd/MM/yy").format(new Date())).setScore(scoreboardContent.size() + 3);
        objective.getScore(WHITE + " ").setScore(scoreboardContent.size() + 2);
        objective.getScore(WHITE + "").setScore(1);
        objective.getScore(YELLOW + "easy-realm.ru").setScore(0);
    }

    // Обновление Scoreboard у всех пользователей
    private void updateScoreboards(Player player) {
        createNewScoreboard(player);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.setScoreboard(scoreboard);
        }
    }

    // Добавление пользовательских строк в Scoreboard
    private Objective addPlayerLines(Objective objective, Player player) {
        scoreboardContent = MySqlDataBase.getScoreboardContent(player);
        if (!scoreboardContent.isEmpty()) {
            int scoreValue = scoreboardContent.size() + 1;
            List<ChatColor> colorsList = new ArrayList<>(Arrays.asList(
                    BLACK, DARK_BLUE, DARK_GREEN,
                    DARK_AQUA, DARK_RED, DARK_PURPLE,
                    GOLD, DARK_GRAY, BLUE,
                    GREEN, AQUA, RED, LIGHT_PURPLE
            ));
            for (String content : scoreboardContent) {
                int clr = ThreadLocalRandom.current().nextInt(colorsList.size());
                if (content.contains("**blankLine**"))
                    objective.getScore(colorsList.get(clr) + "" + RESET + " ").setScore(scoreValue);
                else if (content.contains("**playersCount**"))
                    objective.getScore(colorsList.get(clr) + "" + RESET + "Игроки: " + player.getServer().getOnlinePlayers().size()).setScore(scoreValue);
                else if (content.contains("**realmName**"))
                    objective.getScore(colorsList.get(clr) + "" + RESET + "Название: " + MySqlDataBase.getWorldName(player)).setScore(scoreValue);
                else if (content.contains("**currentGamemode**"))
                    objective.getScore(colorsList.get(clr) + "" + RESET + "Игровой режим: " + YELLOW + player.getGameMode()).setScore(scoreValue);
                else
                    objective.getScore(colorsList.get(clr) + "" + RESET + formatColor(content)).setScore(scoreValue);
                colorsList.remove(clr);
                scoreValue--;
            }
        }
        return objective;
    }

    // CALLBACK ДЛЯ ЧАТА
    private final Map<Player, String> inputMap = new HashMap<>();

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (inputMap.containsKey(player) && inputMap.containsValue("%waitingInput%")) {
            event.setCancelled(true);
            if (message.equalsIgnoreCase("UNDO")) {
                String userInput = inputMap.get(player);
                inputMap.remove(player);
                player.sendMessage(RED + "Вы отменили ввод");
            } else {
                inputMap.put(player, message);
                player.sendMessage(GREEN + "Новое значение: " + formatColor(message));
                player.sendMessage(RED + "" + BOLD + "Для обновления Scoreboard введите команду /reloadsb или перезайдите");
                switch (currentAction) {
                    case "ScoreboardCustomLineChange":
                        scoreboardContent.set(indexToChange, message);
                        MySqlDataBase.writeScoreboardTable(scoreboardContent, player.getWorld().getName(), player);
                        inputMap.remove(player);
                        updateScoreboards(player);
                        showScoreBoardEditorMenu(player);
                        break;
                    case "Coming Soon...":
                        break;
                }
            }
        }
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

    // ПРОЧЕЕ
    public static String formatColor(String format){
        return ChatColor.translateAlternateColorCodes('&', format);
    }
}