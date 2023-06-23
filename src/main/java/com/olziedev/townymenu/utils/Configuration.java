package com.olziedev.townymenu.utils;

import com.olziedev.townymenu.TownyMenuPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.net.URL;
import java.security.CodeSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Configuration {

    private final JavaPlugin plugin;
    private static FileConfiguration config;
    private static Map<String, FileConfiguration> guisMenus;

    public Configuration(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public static Material getCreateMaterial() {
        return Material.getMaterial(config.getString("settings.chunk-view-material", "RED_WOOL"));
    }

    public void load() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);

        guisMenus = new ConcurrentHashMap<>();
        CodeSource src = TownyMenuPlugin.class.getProtectionDomain().getCodeSource();
        if (src == null) return;

        try {
            URL jar = src.getLocation();
            ZipInputStream zip = new ZipInputStream(jar.openStream());
            while (true) {
                ZipEntry e = zip.getNextEntry();
                if (e == null) break;

                String name = e.getName();
                if (!name.startsWith("guis/")) continue;
                if (!name.endsWith(".yml")) continue;

                File file = new File(plugin.getDataFolder(), name);
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    plugin.saveResource(name, false);
                }
                guisMenus.put(name.replace("guis/", ""), YamlConfiguration.loadConfiguration(file));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static FileConfiguration getConfig() {
        return config;
    }

    public static Map<String, FileConfiguration> getGuisMenus() {
        return guisMenus;
    }

    public static FileConfiguration getGuiMenu(String name) {
        return guisMenus.get(name);
    }

    public static String getString(ConfigurationSection section, String s) {
        if (section == null) return "";

        return section.getString(s, "");
    }

    public static String getString(YamlConfiguration config, String s) {
        return config.getString(s, "");
    }
}
