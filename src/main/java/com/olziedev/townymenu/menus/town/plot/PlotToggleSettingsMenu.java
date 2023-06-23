package com.olziedev.townymenu.menus.town.plot;

import com.olziedev.olziemenu.OlzieMenu;
import com.olziedev.olziemenu.framework.menu.FrameworkMenu;
import com.olziedev.townymenu.menus.Menu;
import com.olziedev.townymenu.menus.town.TownyMenu;
import com.olziedev.townymenu.utils.Utils;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.permissions.PermissionNodes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

public class PlotToggleSettingsMenu extends Menu {

    private final List<ConfigurationSection> backButton = new ArrayList<>();
    private final Map<String, List<ConfigurationSection>> flags = new ConcurrentHashMap<>();

    private final String on;
    private final String off;

    public PlotToggleSettingsMenu(ConfigurationSection section, OlzieMenu olzieMenu) {
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

        TownBlock townBlock = this.townyAddon.getTownBlock(player.getLocation());
        if (townBlock == null) return menu;

        this.flags.forEach((flag, sections) -> {
            BiFunction<List<String>, Boolean, List<String>> flagFunction = (s, v) -> {
                return s.stream().map(x -> x.replace("%value%", v ? this.on : this.off)).toList();
            };
            switch (flag) {
                case "firespread":
                    sections.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section, x -> flagFunction.apply(Collections.singletonList(x), town.getPermissions().fire).get(0), x -> flagFunction.apply(x, town.getPermissions().fire))));
                    break;
                case "mobspawning":
                    sections.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section, x -> flagFunction.apply(Collections.singletonList(x), town.getPermissions().mobs).get(0), x -> flagFunction.apply(x, town.getPermissions().mobs))));
                    break;
                case "explosions":
                    sections.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section, x -> flagFunction.apply(Collections.singletonList(x), town.getPermissions().explosion).get(0), x -> flagFunction.apply(x, town.getPermissions().explosion))));
                case "pvp":
                    sections.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section, x -> flagFunction.apply(Collections.singletonList(x), town.getPermissions().pvp).get(0), x -> flagFunction.apply(x, town.getPermissions().pvp))));
                    break;
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
                this.menuManager.getMenu(PlotMenu.class).open(player);
                return true;
            }
        }
        Town town = this.townyAddon.getTown(player);
        if (town == null) return true;

        this.flags.forEach((flag, sections) -> {
            if (sections.stream().noneMatch(section -> section.getInt("slot") == slot)) return;

            switch (flag) {
                case "firespread":
                    if (!checkPerm(player, PermissionNodes.TOWNY_COMMAND_PLOT_TOGGLE_FIRE.getNode())) break;

                    town.setFire(!town.isFire());
                    break;
                case "mobspawning":
                    if (!checkPerm(player, PermissionNodes.TOWNY_COMMAND_PLOT_TOGGLE_MOBS.getNode())) break;

                    town.setHasMobs(!town.hasMobs());
                    break;
                case "explosions":
                    if (!checkPerm(player, PermissionNodes.TOWNY_COMMAND_PLOT_TOGGLE_EXPLOSION.getNode())) break;

                    town.setExplosion(!town.isExplosion());
                    break;
                case "pvp":
                    if (!checkPerm(player, PermissionNodes.TOWNY_COMMAND_PLOT_TOGGLE_PVP.getNode())) break;

                    town.setPVP(!town.isPVP());
                    break;
            }
            this.open(player);
        });
        return true;
    }
}

