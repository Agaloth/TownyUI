package com.olziedev.townymenu.events;

import com.olziedev.townymenu.TownyMenuPlugin;
import com.olziedev.townymenu.utils.Configuration;
import com.olziedev.townymenu.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ChatTypeAction implements Listener {

    private static Map<Player, Predicate<String>> input;

    public ChatTypeAction(TownyMenuPlugin plugin) {
        input = new ConcurrentHashMap<>();
        EventPriority priorty = EventPriority.HIGHEST;
        try {
            priorty = EventPriority.valueOf(Configuration.getConfig().getString("settings.wait-chat-event-priority"));
        } catch (Exception ignored) {}

        Bukkit.getPluginManager().registerEvent(AsyncPlayerChatEvent.class, this, priorty, (listener, e) -> {
            AsyncPlayerChatEvent event = (AsyncPlayerChatEvent) e;
            Player player = event.getPlayer();
            Predicate<String> consumer = input.get(player);
            if (consumer == null) return;

            event.setCancelled(true);
            event.getRecipients().clear();
            if (event.getMessage().equalsIgnoreCase(Configuration.getConfig().getString("lang.cancel-chat-wait"))) {
                Utils.sendMessage(player, Configuration.getConfig().getString("lang.chat-wait-cancelled"));
                return;
            }
            if (consumer.test(event.getMessage())) input.remove(player);
        }, plugin);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        input.remove(event.getPlayer());
    }

    public static void add(Player player, String message, Predicate<String> consumer) {
        input.put(player, consumer);
        player.closeInventory();
        Utils.sendMessage(player, message);
    }
}
