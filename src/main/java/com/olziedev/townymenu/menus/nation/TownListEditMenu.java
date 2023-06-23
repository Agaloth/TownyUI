package com.olziedev.townymenu.menus.nation;

import com.olziedev.olziemenu.OlzieMenu;
import com.olziedev.olziemenu.framework.menu.FrameworkMenu;
import com.olziedev.townymenu.menus.Menu;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.permissions.PermissionNodes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class TownListEditMenu extends Menu {

    public final Map<UUID, Town> towns = new ConcurrentHashMap<>();
    private final List<ConfigurationSection> backButton = new ArrayList<>();
    private final List<ConfigurationSection> kickTownButtons = new ArrayList<>();

    public TownListEditMenu(ConfigurationSection section, OlzieMenu olzieMenu) {
        super(section, olzieMenu);
        ConfigurationSection clickableItems = section.getConfigurationSection("clickable-items");
        if (clickableItems == null) return;

        for (String keys : clickableItems.getKeys(false)) {
            if (keys.startsWith("back")) {
                backButton.add(section.getConfigurationSection("clickable-items." + keys));
            }
            if (keys.startsWith("kick-town")) {
                kickTownButtons.add(section.getConfigurationSection("clickable-items." + keys));
            }
        }
    }

    @Override
    public FrameworkMenu open(Player player, Function<String, String> function) {
        FrameworkMenu menu = super.open(player, function);
        this.backButton.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        this.kickTownButtons.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        return menu;
    }

    @Override
    public boolean onMenuClick(InventoryClickEvent event, FrameworkMenu frameworkMenu) {
        int slot = event.getSlot();
        Player player = (Player) event.getWhoClicked();
        for (ConfigurationSection section : backButton) {
            if (section != null && slot == section.getInt("slot")) {
                this.menuManager.getMenu(NationMenu.class).open(player);
                return true;
            }
        }
        Town town = this.towns.get(player.getUniqueId());
        if (town == null) return true;

        for (ConfigurationSection section : kickTownButtons) {
            if (section != null && slot == section.getInt("slot") && this.checkPerm(player, PermissionNodes.TOWNY_COMMAND_NATION_KICK.getNode())) {
                this.townyAddon.executeNation(player, "kick " + town.getName());
                this.menuManager.getMenu(TownListMenu.class).open(player);
            }
        }
        return true;
    }

    @Override
    public boolean onMenuClose(InventoryCloseEvent event, FrameworkMenu menu) {
        Player player = (Player) event.getPlayer();
        this.towns.remove(player.getUniqueId());
        return false;
    }
}
