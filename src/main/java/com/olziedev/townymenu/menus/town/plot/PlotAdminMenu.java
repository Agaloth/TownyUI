package com.olziedev.townymenu.menus.town.plot;

import com.olziedev.olziemenu.OlzieMenu;
import com.olziedev.olziemenu.framework.menu.FrameworkMenu;
import com.olziedev.townymenu.events.ChatTypeAction;
import com.olziedev.townymenu.menus.Menu;
import com.olziedev.townymenu.utils.Configuration;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PlotAdminMenu extends Menu {

    private final List<ConfigurationSection> backButton = new ArrayList<>();
    private final List<ConfigurationSection> setPlotForSaleButtons = new ArrayList<>();
    private final List<ConfigurationSection> setPlotNotForSaleButtons = new ArrayList<>();
    private final List<ConfigurationSection> setPlotTypeButtons = new ArrayList<>();
    private final List<ConfigurationSection> evictPlotOwnerButtons = new ArrayList<>();

    public PlotAdminMenu(ConfigurationSection section, OlzieMenu olzieMenu) {
        super(section, olzieMenu);
        ConfigurationSection clickableItems = section.getConfigurationSection("clickable-items");
        if (clickableItems == null) return;

        for (String keys : clickableItems.getKeys(false)) {
            if (keys.startsWith("back")) {
                backButton.add(section.getConfigurationSection("clickable-items." + keys));
            }
            if (keys.startsWith("set-plot-for-sale")) {
                setPlotForSaleButtons.add(section.getConfigurationSection("clickable-items." + keys));
            }
            if (keys.startsWith("set-plot-not-for-sale")) {
                setPlotNotForSaleButtons.add(section.getConfigurationSection("clickable-items." + keys));
            }
            if (keys.startsWith("set-plot-type")) {
                setPlotTypeButtons.add(section.getConfigurationSection("clickable-items." + keys));
            }
            if (keys.startsWith("evict-plot-owner")) {
                evictPlotOwnerButtons.add(section.getConfigurationSection("clickable-items." + keys));
            }
        }
    }

    @Override
    public FrameworkMenu open(Player player, Function<String, String> function) {
        FrameworkMenu menu = super.open(player, function);
        this.backButton.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        this.setPlotForSaleButtons.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        this.setPlotNotForSaleButtons.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        this.setPlotTypeButtons.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        this.evictPlotOwnerButtons.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        return menu;
    }

    @Override
    public boolean onMenuClick(InventoryClickEvent event, FrameworkMenu menu) {
        int slot = event.getSlot();
        Player player = (Player) event.getWhoClicked();
        for (ConfigurationSection section : backButton) {
            if (section != null && slot == section.getInt("slot")) {
                this.menuManager.getMenu(PlotMenu.class).open(player);
                return true;
            }
        }
        Town town = this.townyAddon.getTown(player);
        if (town == null) return true;

        for (ConfigurationSection section : setPlotForSaleButtons) {
            if (section != null && slot == section.getInt("slot")) {
                ChatTypeAction.add(player, Configuration.getConfig().getString("lang.enter-sale"), price -> {
                    this.townyAddon.executePlot(player, "forsale " + price);
                    this.menuManager.getMenu(PlotAdminMenu.class).open(player);
                    return true;
                });
                return true;
            }
        }
        for (ConfigurationSection section : setPlotNotForSaleButtons) {
            if (section != null && slot == section.getInt("slot")) {
                ChatTypeAction.add(player, Configuration.getConfig().getString("lang.enter-no-sale"), price -> {
                    if (price.equalsIgnoreCase("confirm")) this.townyAddon.executePlot(player, "notforsale");

                    this.menuManager.getMenu(PlotAdminMenu.class).open(player);
                    return true;
                });
                return true;
            }
        }
        for (ConfigurationSection section : setPlotTypeButtons) {
            if (section != null && slot == section.getInt("slot")) {
                ChatTypeAction.add(player, Configuration.getConfig().getString("lang.enter-plot-type"), type -> {
                    this.townyAddon.executePlot(player, "set " + type);
                    this.menuManager.getMenu(PlotAdminMenu.class).open(player);
                    return true;
                });
                return true;
            }
        }
        for (ConfigurationSection section : evictPlotOwnerButtons) {
            if (section != null && slot == section.getInt("slot")) {
                ChatTypeAction.add(player, Configuration.getConfig().getString("lang.enter-evict-resident"), resident -> {
                   this.townyAddon.executePlot(player, "evict " + resident);
                    this.menuManager.getMenu(PlotAdminMenu.class).open(player);
                    return true;
                });
                return true;
            }
        }
        return true;
    }
}
