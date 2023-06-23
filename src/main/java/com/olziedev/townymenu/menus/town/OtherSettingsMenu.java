package com.olziedev.townymenu.menus.town;

import com.olziedev.olziemenu.OlzieMenu;
import com.olziedev.olziemenu.framework.menu.FrameworkMenu;
import com.olziedev.townymenu.events.ChatTypeAction;
import com.olziedev.townymenu.menus.Menu;
import com.olziedev.townymenu.utils.Configuration;
import com.olziedev.townymenu.utils.NumberUtils;
import com.olziedev.townymenu.utils.Utils;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class OtherSettingsMenu extends Menu {

    private final List<ConfigurationSection> backButton = new ArrayList<>();
    private final List<ConfigurationSection> setHomeButton = new ArrayList<>();
    private final List<ConfigurationSection> setSpawnButton = new ArrayList<>();
    private final List<ConfigurationSection> setTownName = new ArrayList<>();
    private final List<ConfigurationSection> setTownBoard = new ArrayList<>();

    public OtherSettingsMenu(ConfigurationSection section, OlzieMenu olzieMenu) {
        super(section, olzieMenu);
        ConfigurationSection clickableItems = section.getConfigurationSection("clickable-items");
        if (clickableItems == null) return;

        for (String keys : clickableItems.getKeys(false)) {
            if (keys.startsWith("back")) {
                backButton.add(section.getConfigurationSection("clickable-items." + keys));
            }
            if (keys.startsWith("set-home-button")) {
                setHomeButton.add(section.getConfigurationSection("clickable-items." + keys));
            }
            if (keys.startsWith("set-spawn-button")) {
                setSpawnButton.add(section.getConfigurationSection("clickable-items." + keys));
            }
            if (keys.startsWith("set-town-name")) {
                setTownName.add(section.getConfigurationSection("clickable-items." + keys));
            }
            if (keys.startsWith("set-town-board")) {
                setTownBoard.add(section.getConfigurationSection("clickable-items." + keys));
            }
        }
    }

    @Override
    public FrameworkMenu open(Player player, Function<String, String> function) {
        FrameworkMenu menu = super.open(player, function);
        this.backButton.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        this.setHomeButton.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        this.setSpawnButton.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        this.setTownName.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        this.setTownBoard.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        return menu;
    }

    @Override
    public boolean onMenuClick(InventoryClickEvent event, FrameworkMenu frameworkMenu) {
        int slot = event.getSlot();
        Player player = (Player) event.getWhoClicked();
        for (ConfigurationSection section : backButton) {
            if (section != null && slot == section.getInt("slot")) {
                this.menuManager.getMenu(TownyMenu.class).open(player);
                return true;
            }
        }
        for (ConfigurationSection section : setHomeButton) {
            if (section != null && slot == section.getInt("slot")) {
                this.townyAddon.execute(player, "set homeblock");
                return true;
            }
        }
        for (ConfigurationSection section : setSpawnButton) {
            if (section != null && slot == section.getInt("slot")) {
                this.townyAddon.execute(player, "set spawn");
                return true;
            }
        }
        Town town = this.townyAddon.getTown(player);
        if (town == null) return true;

        for (ConfigurationSection section : setTownName) {
            if (section != null && slot == section.getInt("slot")) {
                ChatTypeAction.add(player, Configuration.getConfig().getString("lang.enter-name").replace("%town%", town.getFormattedName()), input -> {
                    this.townyAddon.execute(player, "set name " + input);
                    this.menuManager.getMenu(OtherSettingsMenu.class).open(player);
                    return true;
                });
                return true;
            }
        }
        for (ConfigurationSection section : setTownBoard) {
            if (section != null && slot == section.getInt("slot")) {
                ChatTypeAction.add(player, Configuration.getConfig().getString("lang.enter-board").replace("%town%", town.getFormattedName()), input -> {
                    this.townyAddon.execute(player, "set board " + input);
                    this.menuManager.getMenu(OtherSettingsMenu.class).open(player);
                    return true;
                });
                return true;
            }
        }
        return true;
    }
}
