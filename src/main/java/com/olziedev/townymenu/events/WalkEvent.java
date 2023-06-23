package com.olziedev.townymenu.events;

import com.olziedev.townymenu.TownyMenuPlugin;
import com.olziedev.townymenu.border.BorderBlock;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import javax.swing.border.Border;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WalkEvent implements Listener {

    public static final Map<Player, Chunk> previousChunk = new HashMap<>();


    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        TownyMenuPlugin.getChunkViewPlayers().remove(player);
        BorderBlock.removeGhostBlocks(player);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Location to = event.getTo();
        Location from = event.getFrom();
        if (to == null || from == null) return;

        this.check(event.getPlayer(), to.getChunk());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Location to = event.getTo();
        Location from = event.getFrom();
        if (to == null || from == null) return;

        this.check(event.getPlayer(), to.getChunk());
    }

    @EventHandler
    public void OnItemInteract(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            BorderBlock.removeGhostBlocks(event.getPlayer(), clickedBlock);
        }
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        BorderBlock.getGhostBlocks(player).stream().filter(x -> x.getBlock().equals(clickedBlock))
                .findFirst()
                .ifPresent(x -> Bukkit.getScheduler().runTaskLater(TownyMenuPlugin.getInstance(), () -> x.create(player), 2));
    }

    public void check(Player player, Chunk newChunk) {
        if (!TownyMenuPlugin.getChunkViewPlayers().contains(player)) return;

        Chunk previous = previousChunk.get(player);
        if (previous != null && previous.equals(newChunk)) return;

        System.out.println("Player moved chunk");
        BorderBlock.createBorderBlocks(player, -1, newChunk);
        previousChunk.put(player, newChunk);
    }
}
