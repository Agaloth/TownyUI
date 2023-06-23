package com.olziedev.townymenu.menus.town.plot;

import com.olziedev.olziemenu.OlzieMenu;
import com.olziedev.olziemenu.framework.menu.FrameworkMenu;
import com.olziedev.townymenu.menus.Menu;
import com.olziedev.townymenu.menus.town.ToggleSettingsMenu;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PlotMenu extends Menu {

    private final List<ConfigurationSection> toggleSettingsMenu = new ArrayList<>();
    private final List<ConfigurationSection> permissionMenu = new ArrayList<>();
    private final List<ConfigurationSection> administrationMenu = new ArrayList<>();
    private final List<ConfigurationSection> friendMenu = new ArrayList<>();


    public PlotMenu(ConfigurationSection section, OlzieMenu olzieMenu) {
        super(section, olzieMenu);
        ConfigurationSection clickableItems = section.getConfigurationSection("clickable-items");
        if (clickableItems == null) return;

        for (String keys : clickableItems.getKeys(false)) {
            if (keys.startsWith("toggle-settings-menu")) {
                toggleSettingsMenu.add(section.getConfigurationSection("clickable-items." + keys));
            }
            if (keys.startsWith("permission-menu")) {
                permissionMenu.add(section.getConfigurationSection("clickable-items." + keys));
            }
            if (keys.startsWith("administration-menu")) {
                administrationMenu.add(section.getConfigurationSection("clickable-items." + keys));
            }
            if (keys.startsWith("friend-menu")) {
                friendMenu.add(section.getConfigurationSection("clickable-items." + keys));
            }
        }
    }

    @Override
    public FrameworkMenu open(Player player, Function<String, String> function) {
        FrameworkMenu menu = super.open(player, function);
        this.toggleSettingsMenu.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        this.permissionMenu.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        this.administrationMenu.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        this.friendMenu.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        return menu;
    }

    @Override
    public boolean onMenuClick(InventoryClickEvent event, FrameworkMenu menu) {
        int slot = event.getSlot();
        Player player = (Player) event.getWhoClicked();
        for (ConfigurationSection section : toggleSettingsMenu) {
            if (section.getInt("slot") == slot) {
                menuManager.getMenu(PlotToggleSettingsMenu.class).open(player);
                return true;
            }
        }
        for (ConfigurationSection section : permissionMenu) {
            if (section.getInt("slot") == slot) {
                menuManager.getMenu(PlotPermissionsMenu.class).open(player);
                return true;
            }
        }
        for (ConfigurationSection section : administrationMenu) {
            if (section.getInt("slot") == slot) {
                menuManager.getMenu(PlotAdminMenu.class).open(player);
                return true;
            }
        }
        for (ConfigurationSection section : friendMenu) {
            if (section.getInt("slot") == slot) {
                menuManager.getMenu(FriendMenu.class).open(player);
                return true;
            }
        }
        return true;
    }
}
