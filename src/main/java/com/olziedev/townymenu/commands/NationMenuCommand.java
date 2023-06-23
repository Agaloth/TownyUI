package com.olziedev.townymenu.commands;

import com.olziedev.olziecommand.v1_3_3.framework.CommandExecutor;
import com.olziedev.olziecommand.v1_3_3.framework.ExecutorType;
import com.olziedev.olziecommand.v1_3_3.framework.api.FrameworkCommand;
import com.olziedev.townymenu.TownyMenuPlugin;
import com.olziedev.townymenu.addons.TownyAddon;
import com.olziedev.townymenu.managers.MenuManager;
import com.olziedev.townymenu.menus.nation.NationMainMenu;
import com.olziedev.townymenu.menus.nation.NationMenu;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.entity.Player;

public class NationMenuCommand extends FrameworkCommand {

    private final TownyAddon townyAddon;
    private final MenuManager menuManager;

    public NationMenuCommand() {
        super("nationmenu");
        this.setExecutorType(ExecutorType.PLAYER_ONLY);
        this.townyAddon = TownyMenuPlugin.getAddonManager().getAddon(TownyAddon.class);
        this.menuManager = TownyMenuPlugin.getMenuManager();
    }

    @Override
    public void onExecute(CommandExecutor cmd) {
        Player player = (Player) cmd.getSender();
        Nation town = townyAddon.getNation(player);
        if (town == null) {
            this.menuManager.getMenu(NationMainMenu.class).open(player);
            return;
        }
        this.menuManager.getMenu(NationMenu.class).open(player);
    }
}
