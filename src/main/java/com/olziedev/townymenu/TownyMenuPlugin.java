package com.olziedev.townymenu;

import com.olziedev.olziecommand.v1_3_3.OlzieCommand;
import com.olziedev.olziecommand.v1_3_3.framework.action.CommandActionType;
import com.olziedev.townymenu.border.BorderBlock;
import com.olziedev.townymenu.events.ChatTypeAction;
import com.olziedev.townymenu.events.WalkEvent;
import com.olziedev.townymenu.managers.AddonManager;
import com.olziedev.townymenu.managers.MenuManager;
import com.olziedev.townymenu.utils.Configuration;
import com.olziedev.townymenu.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class TownyMenuPlugin extends JavaPlugin {

    private static TownyMenuPlugin instance;

    private AddonManager addonManager;
    private MenuManager menuManager;

    private List<Player> chunkViewPlayers;

    @Override
    public void onEnable() {
        instance = this;
        this.chunkViewPlayers = new ArrayList<>();
        new Configuration(this).load();

        this.addonManager = new AddonManager(this);
        this.addonManager.setup();
        this.addonManager.load();

        this.menuManager = new MenuManager(this);
        this.menuManager.setup();
        this.menuManager.load();

        new OlzieCommand(this, getClass())
                .getActionRegister()
                .registerAction(CommandActionType.CMD_NO_PERMISSION, cmd -> {
                    Utils.sendMessage(cmd.getSender(), Configuration.getConfig().getString("lang.no-permission"));
                })
                .registerAction(CommandActionType.CMD_NOT_PLAYER, cmd -> {
                    Utils.sendMessage(cmd.getSender(), Configuration.getConfig().getString("lang.not-player"));
                }).buildActions()
                .registerCommands(); // automatically register commands

        Bukkit.getPluginManager().registerEvents(new ChatTypeAction(this), this);
        Bukkit.getPluginManager().registerEvents(new WalkEvent(), this);
    }

    @Override
    public void onDisable() {
        for (Player player : this.chunkViewPlayers) {
            BorderBlock.removeAll(player);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!this.menuManager.getOlzieMenu().isOlzieMenu(player.getOpenInventory().getTopInventory())) continue;

            player.closeInventory();
        }
        this.addonManager.close();
        this.addonManager = null;

        this.menuManager.close();
        this.menuManager = null;

        Bukkit.getScheduler().cancelTasks(this);
        instance = null;
    }

    public static TownyMenuPlugin getInstance() {
        return instance;
    }

    public static MenuManager getMenuManager() {
        return instance.menuManager;
    }

    public static AddonManager getAddonManager() {
        return instance.addonManager;
    }

    public static List<Player> getChunkViewPlayers() {
        return instance.chunkViewPlayers;
    }
}
