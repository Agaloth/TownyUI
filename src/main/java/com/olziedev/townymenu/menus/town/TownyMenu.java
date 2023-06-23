package com.olziedev.townymenu.menus.town;

import com.olziedev.olziemenu.OlzieMenu;
import com.olziedev.olziemenu.framework.menu.FrameworkMenu;
import com.olziedev.townymenu.menus.Menu;
import com.olziedev.townymenu.menus.town.plot.PlotMenu;
import com.olziedev.townymenu.utils.Configuration;
import com.olziedev.townymenu.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TownyMenu extends Menu {

    private final List<ConfigurationSection> toggleSettingsMenu = new ArrayList<>();
    private final List<ConfigurationSection> residentMenu = new ArrayList<>();
    private final List<ConfigurationSection> permissionMenu = new ArrayList<>();
    private final List<ConfigurationSection> otherSettingsMenu = new ArrayList<>();
    private final List<ConfigurationSection> economyMenu = new ArrayList<>();
    private final List<ConfigurationSection> invitePlayerMenu = new ArrayList<>();
    private final List<ConfigurationSection> extraTownyInfoMenu = new ArrayList<>();
    private final List<ConfigurationSection> plotManagementMenu = new ArrayList<>();

    public TownyMenu(ConfigurationSection section, OlzieMenu olzieMenu) {
        super(section, olzieMenu);
        ConfigurationSection clickableItems = section.getConfigurationSection("clickable-items");
        if (clickableItems == null) return;

        for (String keys : clickableItems.getKeys(false)) {
            if (keys.startsWith("toggle-settings-menu")) {
                toggleSettingsMenu.add(section.getConfigurationSection("clickable-items." + keys));
            }
            if (keys.startsWith("resident-menu")) {
                residentMenu.add(section.getConfigurationSection("clickable-items." + keys));
            }
            if (keys.startsWith("permission-menu")) {
                permissionMenu.add(section.getConfigurationSection("clickable-items." + keys));
            }
            if (keys.startsWith("other-settings-menu")) {
                otherSettingsMenu.add(section.getConfigurationSection("clickable-items." + keys));
            }
            if (keys.startsWith("economy-menu")) {
                economyMenu.add(section.getConfigurationSection("clickable-items." + keys));
            }
            if (keys.startsWith("invite-player-menu")) {
                invitePlayerMenu.add(section.getConfigurationSection("clickable-items." + keys));
            }
            if (keys.startsWith("extra-towny-info-menu")) {
                extraTownyInfoMenu.add(section.getConfigurationSection("clickable-items." + keys));
            }
            if (keys.startsWith("plot-management-menu")) {
                plotManagementMenu.add(section.getConfigurationSection("clickable-items." + keys));
            }
        }
    }

    @Override
    public FrameworkMenu open(Player player, Function<String, String> function) {
        FrameworkMenu menu = super.open(player, function);
        toggleSettingsMenu.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        residentMenu.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        permissionMenu.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        otherSettingsMenu.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        economyMenu.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        invitePlayerMenu.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        extraTownyInfoMenu.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        plotManagementMenu.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        return menu;
    }

    @Override
    public boolean onMenuClick(InventoryClickEvent event, FrameworkMenu menu) {
        int slot = event.getSlot();
        Player player = (Player) event.getWhoClicked();
        for (ConfigurationSection section : toggleSettingsMenu) {
            if (section.getInt("slot") == slot) {
                menuManager.getMenu(ToggleSettingsMenu.class).open(player);
                return true;
            }
        }
        for (ConfigurationSection section : residentMenu) {
            if (section.getInt("slot") == slot) {
                menuManager.getMenu(ResidentMenu.class).open(player);
                return true;
            }
        }
        for (ConfigurationSection section : permissionMenu) {
            if (section.getInt("slot") == slot) {
                menuManager.getMenu(PermissionsMenu.class).open(player);
                return true;
            }
        }
        for (ConfigurationSection section : otherSettingsMenu) {
            if (section.getInt("slot") == slot) {
                menuManager.getMenu(OtherSettingsMenu.class).open(player);
                return true;
            }
        }
        for (ConfigurationSection section : economyMenu) {
            if (section.getInt("slot") == slot) {
                menuManager.getMenu(EcoMenu.class).open(player);
                return true;
            }
        }
        for (ConfigurationSection section : invitePlayerMenu) {
            if (section.getInt("slot") == slot) {
                menuManager.getMenu(InvitePlayerMenu.class).open(player);
                return true;
            }
        }
        for (ConfigurationSection section : extraTownyInfoMenu) {
            if (section.getInt("slot") == slot) {
                menuManager.getMenu(ExtraTownMenu.class).open(player);
                return true;
            }
        }
        for (ConfigurationSection section : plotManagementMenu) {
            if (section.getInt("slot") == slot) {
                if (townyAddon.getTownBlock(player.getLocation()) == null) {
                    Utils.sendMessage(player, Configuration.getConfig().getString("lang.not-in-claim"));
                    return true;
                }
                menuManager.getMenu(PlotMenu.class).open(player);
                return true;
            }
        }
        return true;
    }
}
