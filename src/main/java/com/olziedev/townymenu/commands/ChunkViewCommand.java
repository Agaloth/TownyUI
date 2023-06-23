package com.olziedev.townymenu.commands;

import com.olziedev.olziecommand.v1_3_3.framework.CommandExecutor;
import com.olziedev.olziecommand.v1_3_3.framework.ExecutorType;
import com.olziedev.olziecommand.v1_3_3.framework.api.FrameworkCommand;
import com.olziedev.townymenu.TownyMenuPlugin;
import com.olziedev.townymenu.border.BorderBlock;
import com.olziedev.townymenu.events.WalkEvent;
import com.olziedev.townymenu.utils.Configuration;
import com.olziedev.townymenu.utils.Utils;
import org.bukkit.entity.Player;

import java.util.List;

public class ChunkViewCommand extends FrameworkCommand {

    public ChunkViewCommand() {
        super("chunkview");
        this.setExecutorType(ExecutorType.PLAYER_ONLY);
    }

    @Override
    public void onExecute(CommandExecutor cmd) {
        Player player = (Player) cmd.getSender();
        List<Player> chunkViewPlayers = TownyMenuPlugin.getChunkViewPlayers();
        if (chunkViewPlayers.contains(player)) {
            chunkViewPlayers.remove(player);
            BorderBlock.removeAll(player);
            Utils.sendMessage(player, Configuration.getConfig().getString("lang.chunk-view-disabled"));
            return;
        }
        chunkViewPlayers.add(player);
        Utils.sendMessage(player, Configuration.getConfig().getString("lang.chunk-view-enabled"));
    }
}
