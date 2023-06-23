package com.olziedev.townymenu.menus.nation;

import com.olziedev.olziemenu.OlzieMenu;
import com.olziedev.olziemenu.framework.menu.FrameworkMenu;
import com.olziedev.townymenu.menus.Menu;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class NationMenu extends Menu {

    private final List<ConfigurationSection> toggleSettingsMenu = new ArrayList<>();
    private final List<ConfigurationSection> townListMenu = new ArrayList<>();
    private final List<ConfigurationSection> residentMenu = new ArrayList<>();
    private final List<ConfigurationSection> economyMenu = new ArrayList<>();
    private final List<ConfigurationSection> settingsMenu = new ArrayList<>();
    private final List<ConfigurationSection> inviteTownMenu = new ArrayList<>();
    private final List<ConfigurationSection> extraNationInfo = new ArrayList<>();

    public NationMenu(ConfigurationSection section, OlzieMenu olzieMenu) {
        super(section, olzieMenu);
        ConfigurationSection clickableItems = section.getConfigurationSection("clickable-items");
        if (clickableItems == null) return;

        for (String keys : clickableItems.getKeys(false)) {
            if (keys.startsWith("toggle-nation-settings-menu")) {
                toggleSettingsMenu.add(clickableItems.getConfigurationSection(keys));
            }
            if (keys.startsWith("town-list-menu")) {
                townListMenu.add(clickableItems.getConfigurationSection(keys));
            }
            if (keys.startsWith("nation-resident-menu")) {
                residentMenu.add(clickableItems.getConfigurationSection(keys));
            }
            if (keys.startsWith("nation-economy-menu")) {
                economyMenu.add(clickableItems.getConfigurationSection(keys));
            }
            if (keys.startsWith("nation-settings-menu")) {
                settingsMenu.add(clickableItems.getConfigurationSection(keys));
            }
            if (keys.startsWith("nation-invite-town-menu")) {
                inviteTownMenu.add(clickableItems.getConfigurationSection(keys));
            }
            if (keys.startsWith("extra-nation-info-menu")) {
                extraNationInfo.add(clickableItems.getConfigurationSection(keys));
            }
        }
    }

    @Override
    public FrameworkMenu open(Player player, Function<String, String> function) {
        FrameworkMenu menu = super.open(player, function);
        this.toggleSettingsMenu.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        this.townListMenu.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        this.residentMenu.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        this.economyMenu.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        this.settingsMenu.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        this.inviteTownMenu.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        this.extraNationInfo.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        return menu;
    }

    @Override
    public boolean onMenuClick(InventoryClickEvent event, FrameworkMenu menu) {
        int slot = event.getSlot();
        Player player = (Player) event.getWhoClicked();
        for (ConfigurationSection section : toggleSettingsMenu) {
            if (section.getInt("slot") == slot) {
                menuManager.getMenu(ToggleNationSettingsMenu.class).open(player);
                return true;
            }
        }
        for (ConfigurationSection section : townListMenu) {
            if (section.getInt("slot") == slot) {
                menuManager.getMenu(TownListMenu.class).open(player);
                return true;
            }
        }
        for (ConfigurationSection section : residentMenu) {
            if (section.getInt("slot") == slot) {
                menuManager.getMenu(NationResidentMenu.class).open(player);
                return true;
            }
        }
        for (ConfigurationSection section : economyMenu) {
            if (section.getInt("slot") == slot) {
                menuManager.getMenu(NationEcoMenu.class).open(player);
                return true;
            }
        }
        for (ConfigurationSection section : settingsMenu) {
            if (section.getInt("slot") == slot) {
                menuManager.getMenu(NationSettingsMenu.class).open(player);
                return true;
            }
        }
        for (ConfigurationSection section : inviteTownMenu) {
            if (section.getInt("slot") == slot) {
                menuManager.getMenu(InviteTownMenu.class).open(player);
                return true;
            }
        }
        for (ConfigurationSection section : extraNationInfo) {
            if (section.getInt("slot") == slot) {
                menuManager.getMenu(ExtraNationInfoMenu.class).open(player);
                return true;
            }
        }
        return true;
    }
}
