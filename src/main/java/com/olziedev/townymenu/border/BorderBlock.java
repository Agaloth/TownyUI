package com.olziedev.townymenu.border;

import com.olziedev.townymenu.events.WalkEvent;
import com.olziedev.townymenu.utils.Configuration;
import com.sun.jdi.event.MonitorWaitEvent;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class BorderBlock {

    private final Block block;
    private static final HashMap<Player, List<BorderBlock>> ghostBlocks = new HashMap<>();

    public BorderBlock(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return this.block;
    }

    private void remove(Player player) {
        player.sendBlockChange(block.getLocation(), block.getBlockData());
    }

    public void create(Player player) {
        player.sendBlockChange(block.getLocation(), Configuration.getCreateMaterial().createBlockData());
    }

    public static List<BorderBlock> createBorderBlocks(Player player, int y, Chunk chunk) {
        removeAll(player);
        List<BorderBlock> borderBlocks = new ArrayList<>();
        int minX = chunk.getX() * 16;
        int minZ = chunk.getZ() * 16;
        World world = chunk.getWorld();
        for(int x = minX; x < minX + 16; x++) {
            for(int z = minZ; z < minZ + 16; z++) {
                borderBlocks.add(new BorderBlock(y == -1 ? world.getHighestBlockAt(minX, z) : world.getBlockAt(minX, y, z)));
                borderBlocks.add(new BorderBlock(y == -1 ? world.getHighestBlockAt(x, minZ) : world.getBlockAt(x, y, minZ)));
                borderBlocks.add(new BorderBlock(y == -1 ? world.getHighestBlockAt(minX + 15, z) : world.getBlockAt(minX + 15, y, z)));
                borderBlocks.add(new BorderBlock(y == -1 ? world.getHighestBlockAt(x, minZ + 15) : world.getBlockAt(x, y, minZ + 15)));
            }
        }
        borderBlocks.forEach(block -> block.create(player));

        setGhostBlocks(player, borderBlocks);
        return borderBlocks;
    }

    public static void removeAll(Player player) {
        List<BorderBlock> borderBlocks = getGhostBlocks(player);
        borderBlocks.forEach(block -> block.remove(player));

        removeGhostBlocks(player);
    }

    public static void setGhostBlocks(Player player, List<BorderBlock> blocks) {
        ghostBlocks.put(player, blocks);
    }

    public static void removeGhostBlocks(Player player) {
        ghostBlocks.remove(player);
        WalkEvent.previousChunk.remove(player);
    }

    public static void removeGhostBlocks(Player player, Block block) {
        if (ghostBlocks.containsKey(player)) {
            ghostBlocks.get(player).removeIf(borderBlock -> borderBlock.getBlock().equals(block));
        }
    }

    public static List<BorderBlock> getGhostBlocks(Player player) {
        return ghostBlocks.getOrDefault(player, Collections.emptyList());
    }
}
