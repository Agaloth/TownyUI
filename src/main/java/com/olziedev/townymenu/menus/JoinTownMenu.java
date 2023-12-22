package com.olziedev.townymenu.menus;

import com.olziedev.olziemenu.OlzieMenu;
import com.olziedev.olziemenu.framework.menu.FrameworkMenu;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class JoinTownMenu extends PageMenu {

    private final List<ConfigurationSection> backButton = new ArrayList<>();
    private final List<ConfigurationSection> nextButton = new ArrayList<>();
    private final List<ConfigurationSection> previousButton = new ArrayList<>();

    public JoinTownMenu(ConfigurationSection section, OlzieMenu olzieMenu) {
        super(section, olzieMenu);
        ConfigurationSection clickableItems = section.getConfigurationSection("clickable-items");
        if (clickableItems == null) return;

        for (String keys : clickableItems.getKeys(false)) {
            if (keys.startsWith("back")) {
                backButton.add(section.getConfigurationSection("clickable-items." + keys));
            }
            if (keys.startsWith("next-page")) {
                nextButton.add(section.getConfigurationSection("clickable-items." + keys));
            }
            if (keys.startsWith("previous-page")) {
                previousButton.add(section.getConfigurationSection("clickable-items." + keys));
            }
        }
    }

    @Override
    public FrameworkMenu open(Player player, Function<String, String> function) {
        List<PageItem> pageItems = this.getItems(player);
        List<Town> towns = !pageItems.isEmpty() ? Collections.emptyList() : townyAddon.getJoinableTowns();
        for (Town town : towns) {
            Function<List<String>, List<String>> replaceFunction = strings -> strings.stream().map(s -> s.replace("%mayor%", town.getMayor().getName()).replace("%town%", town.getFormattedName()).replace("%residents%", String.valueOf(town.getNumResidents()))).toList();
            pageItems.add(new PageItem<>(() -> this.createItem(this.section.getConfigurationSection("town-item"), x -> replaceFunction.apply(Collections.singletonList(x)).get(0), replaceFunction), (click, item) -> {
                this.townyAddon.execute(player, "join " + town.getName());
                Bukkit.getScheduler().runTask(plugin, () -> player.closeInventory());
            }, town));
        }
        FrameworkMenu menu = super.build(function);
        this.backButton.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        this.nextButton.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        this.previousButton.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        return super.open(player, pageItems, menu);
    }

    @Override
    public boolean onMenuClick(InventoryClickEvent event, FrameworkMenu menu) {
        int slot = event.getSlot();
        Player player = (Player) event.getWhoClicked();
        for (ConfigurationSection section : backButton) {
            if (section != null && slot == section.getInt("slot")) {
                this.menuManager.getMenu(TownyMainMenu.class).open(player);
                return true;
            }
        }
        for (ConfigurationSection section : nextButton) {
            if (section != null && slot == section.getInt("slot")) {
                this.nextPage(player);
                return true;
            }
        }
        for (ConfigurationSection section : previousButton) {
            if (section != null && slot == section.getInt("slot")) {
                this.previousPage(player);
                return true;
            }
        }
        return super.onMenuClick(event, menu);
    }
}
