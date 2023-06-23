package com.olziedev.townymenu.menus.nation;

import com.olziedev.olziemenu.OlzieMenu;
import com.olziedev.olziemenu.framework.menu.FrameworkMenu;
import com.olziedev.townymenu.menus.Menu;
import com.olziedev.townymenu.menus.town.TownyMenu;
import com.olziedev.townymenu.utils.Utils;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.permissions.PermissionNodes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ToggleNationSettingsMenu extends Menu {

    private final List<ConfigurationSection> backButton = new ArrayList<>();
    private final Map<String, List<ConfigurationSection>> flags = new ConcurrentHashMap<>();

    private final String on;
    private final String off;

    public ToggleNationSettingsMenu(ConfigurationSection section, OlzieMenu olzieMenu) {
        super(section, olzieMenu);
        this.on = Utils.color(section.getString("values.on-toggle"));
        this.off = Utils.color(section.getString("values.off-toggle"));
        ConfigurationSection clickableItems = section.getConfigurationSection("clickable-items");
        if (clickableItems == null) return;

        for (String keys : clickableItems.getKeys(false)) {
            if (keys.startsWith("back")) {
                backButton.add(section.getConfigurationSection("clickable-items." + keys));
            }
            if (!keys.startsWith("toggle-")) continue;

            String flag = keys.replace("toggle-", "").split("-")[0];
            flags.computeIfAbsent(flag, x -> new ArrayList<>()).add(section.getConfigurationSection("clickable-items." + keys));
        }
    }

    @Override
    public FrameworkMenu open(Player player, Function<String, String> function) {
        FrameworkMenu menu = super.open(player, function);
        this.backButton.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));

        Town town = this.townyAddon.getTown(player);
        if (town == null) return menu;

        Nation nation = town.getNationOrNull();
        if (nation == null) return menu;

        this.flags.forEach((flag, sections) -> {
            BiFunction<List<String>, Boolean, List<String>> flagFunction = (s, v) -> s.stream().map(x -> x.replace("%value%", v ? this.on : this.off)).toList();
            switch (flag) {
                case "public":
                    sections.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section, x -> flagFunction.apply(Collections.singletonList(x), nation.isPublic()).get(0), x -> flagFunction.apply(x, nation.isPublic()))));
                    break;
                case "open":
                    sections.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section, x -> flagFunction.apply(Collections.singletonList(x), nation.isOpen()).get(0), x -> flagFunction.apply(x, nation.isOpen()))));
                    break;
                case "taxpercent":
                    sections.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section, x -> flagFunction.apply(Collections.singletonList(x), nation.isTaxPercentage()).get(0), x -> flagFunction.apply(x, nation.isTaxPercentage()))));
            }
        });

        return menu;
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
        Town town = this.townyAddon.getTown(player);
        if (town == null) return true;

        Nation nation = town.getNationOrNull();
        if (nation == null) return true;

        this.flags.forEach((flag, sections) -> {
            if (sections.stream().noneMatch(section -> section.getInt("slot") == slot)) return;

            switch (flag) {
                case "public":
                    if (!checkPerm(player, PermissionNodes.TOWNY_COMMAND_NATION_TOGGLE_PUBLIC.getNode())) break;

                    nation.setPublic(!nation.isPublic());
                    break;
                case "open":
                    if (!checkPerm(player, PermissionNodes.TOWNY_COMMAND_NATION_TOGGLE_OPEN.getNode())) break;

                    nation.setOpen(!nation.isOpen());
                    break;
                case "taxpercent":
                    if (!checkPerm(player, PermissionNodes.TOWNY_COMMAND_NATION_TOGGLE_TAXPERCENT.getNode())) break;

                    nation.setTaxPercentage(!nation.isTaxPercentage());
            }
            this.open(player);
        });
        return true;
    }
}
