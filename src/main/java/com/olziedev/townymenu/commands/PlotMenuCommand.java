package com.olziedev.townymenu.commands;

import com.olziedev.olziecommand.v1_3_3.framework.CommandExecutor;
import com.olziedev.olziecommand.v1_3_3.framework.ExecutorType;
import com.olziedev.olziecommand.v1_3_3.framework.api.FrameworkCommand;
import com.olziedev.townymenu.TownyMenuPlugin;
import com.olziedev.townymenu.addons.TownyAddon;
import com.olziedev.townymenu.managers.MenuManager;
import com.olziedev.townymenu.menus.TownyMainMenu;
import com.olziedev.townymenu.menus.town.plot.PlotMenu;
import com.olziedev.townymenu.utils.Configuration;
import com.olziedev.townymenu.utils.Utils;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import org.bukkit.entity.Player;

public class PlotMenuCommand extends FrameworkCommand {

    private final TownyAddon townyAddon;
    private final MenuManager menuManager;

    public PlotMenuCommand() {
        super("polotmenu");
        this.setExecutorType(ExecutorType.PLAYER_ONLY);
        this.townyAddon = TownyMenuPlugin.getAddonManager().getAddon(TownyAddon.class);
        this.menuManager = TownyMenuPlugin.getMenuManager();
    }

    @Override
    public void onExecute(CommandExecutor cmd) {
        Player player = (Player) cmd.getSender();
        Town town = townyAddon.getTown(player);
        if (town == null) {
            this.menuManager.getMenu(TownyMainMenu.class).open(player);
            return;
        }
        TownBlock townBlock = townyAddon.getTownBlock(player.getLocation());
        if (townBlock == null) {
            Utils.sendMessage(player, Configuration.getConfig().getString("lang.not-in-claim"));
            return;
        }
        this.menuManager.getMenu(PlotMenu.class).open(player);
    }
}