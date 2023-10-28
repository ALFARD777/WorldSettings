package org.desparodev.worldsettings;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public class WorldSettingsItems {
    public static ItemStack getItem(String name) {
        FileConfiguration items = loadItemsConfig();
        for (String itemKey : items.getKeys(false)) {
            if (Objects.equals(itemKey, name)) {
                String displayName = ChatColorUtils.formatColor(items.getString(itemKey + ".name"));
                Material material = Material.matchMaterial(Objects.requireNonNull(items.getString(itemKey + ".material")));
                assert material != null;
                ItemStack item = new ItemStack(material, 1);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(displayName);
                List<String> lore = items.getStringList(itemKey + ".lore");
                for (String element : lore) {
                    lore.set(lore.indexOf(element), ChatColorUtils.formatColor(element));
                }
                meta.setLore(lore);
                if (!items.getStringList(itemKey + ".flags").isEmpty()) {
                    for (String flags : items.getStringList(itemKey + ".flags")) {
                        meta.addItemFlags(ItemFlag.valueOf(flags));
                    }
                }
                item.setItemMeta(meta);
                return item;
            }
        }
        return null;
    }

    private static FileConfiguration loadItemsConfig() {
        if (!WorldSettingsProvider.getPlugin().getDataFolder().exists()) {
            WorldSettingsProvider.getPlugin().getDataFolder().mkdir();
        }
        Path customConfigFile = WorldSettingsProvider.getPlugin().getDataFolder().toPath().resolve("items.yml");
        try {
            Files.delete(customConfigFile);
            InputStream defaultConfig = WorldSettingsProvider.getPlugin().getResource("items.yml");
            assert defaultConfig != null;
            Files.copy(defaultConfig, customConfigFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return YamlConfiguration.loadConfiguration(customConfigFile.toFile());
    }

    private static ItemStack addGlow(ItemStack itemStack, int value) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            if (value > 0) {
                meta.addEnchant(Enchantment.LURE, 1, false);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            } else {
                meta.removeEnchant(Enchantment.LURE);
            }

            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }
}
