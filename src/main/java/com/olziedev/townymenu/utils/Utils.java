package com.olziedev.townymenu.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private final static Pattern HEX_PATTERN = Pattern.compile("\\{#([A-Fa-f0-9]){6}}");

    public static String color(String s) {
        if (s == null || s.trim().isEmpty()) return "";

        Matcher matcher = HEX_PATTERN.matcher(s);

        while (matcher.find()) {
            String hexString = matcher.group();
            hexString = hexString.substring(1, hexString.length() - 1);
            s = s.substring(0, matcher.start()) + ChatColor.of(hexString) + s.substring(matcher.end());
            matcher = HEX_PATTERN.matcher(s);
        }
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', s);
    }

    public static void sendMessage(CommandSender sender, String s) {
        if (s == null || s.trim().isEmpty() || sender == null) return;

        s = color(s);
        if (!(sender instanceof Player)) {
            Bukkit.getServer().getConsoleSender().sendMessage(s);
            return;
        }
        sender.sendMessage(s);
    }
}
