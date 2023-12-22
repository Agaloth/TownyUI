package com.olziedev.townymenu.menus.nation;

import com.olziedev.olziemenu.OlzieMenu;
import com.olziedev.olziemenu.framework.menu.FrameworkMenu;
import com.olziedev.olziemenu.framework.menu.PaginationMenuAdapter;
import com.olziedev.townymenu.menus.Menu;
import com.olziedev.townymenu.menus.PageMenu;
import com.olziedev.townymenu.utils.Configuration;
import com.olziedev.townymenu.utils.Utils;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TownListMenu extends PageMenu {

    private final List<ConfigurationSection> backButton = new ArrayList<>();
    private final List<ConfigurationSection> nextButton = new ArrayList<>();
    private final List<ConfigurationSection> previousButton = new ArrayList<>();

    public TownListMenu(ConfigurationSection section, OlzieMenu olzieMenu) {
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
        List<PaginationMenuAdapter.PageItem> pageItems = this.getItems(player);
        Town town = this.townyAddon.getTown(player);
        if (town == null) return null;

        Nation nation = town.getNationOrNull();
        if (nation == null) return null;

        List<Town> townsList = !pageItems.isEmpty() ? Collections.emptyList() : nation.getTowns().stream().filter(x -> !x.equals(nation.getKing().getTownOrNull())).toList();
        for (Town towns : townsList) {
            Function<List<String>, List<String>> replaceFunction = strings -> strings.stream().map(s -> s.replace("%mayor%", towns.getMayor().getName()).replace("%town%", towns.getFormattedName()).replace("%residents%", String.valueOf(towns.getNumResidents()))).toList();
            pageItems.add(new PaginationMenuAdapter.PageItem<>(() -> this.createItem(this.section.getConfigurationSection("town-item"), x -> replaceFunction.apply(Collections.singletonList(x)).get(0), replaceFunction), (click, item) -> {
                TownListEditMenu menu = this.menuManager.getMenu(TownListEditMenu.class);
                menu.towns.put(player.getUniqueId(), towns);
                menu.open(player);
            }, towns));
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
                this.menuManager.getMenu(NationMenu.class).open(player);
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
