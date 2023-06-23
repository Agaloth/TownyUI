package com.olziedev.townymenu.menus.town;

import com.olziedev.olziemenu.OlzieMenu;
import com.olziedev.olziemenu.framework.menu.FrameworkMenu;
import com.olziedev.townymenu.menus.Menu;
import com.olziedev.townymenu.utils.Utils;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.object.TownyPermissionChange;
import com.palmergames.bukkit.towny.permissions.PermissionNodes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class PermissionsMenu extends Menu {

    private final List<ConfigurationSection> backButton = new ArrayList<>();
    private final List<ConfigurationSection> resetButton = new ArrayList<>();
    private final List<ConfigurationSection> turnOnButton = new ArrayList<>();
    private final Map<String, List<ConfigurationSection>> permissionButtons = new ConcurrentHashMap<>();
    private final String on;
    private final String off;

    public PermissionsMenu(ConfigurationSection section, OlzieMenu olzieMenu) {
        super(section, olzieMenu);
        this.on = Utils.color(section.getString("values.on-toggle"));
        this.off = Utils.color(section.getString("values.off-toggle"));
        ConfigurationSection clickableItems = section.getConfigurationSection("clickable-items");
        if (clickableItems == null) return;

        for (String keys : clickableItems.getKeys(false)) {
            if (keys.startsWith("back")) {
                backButton.add(section.getConfigurationSection("clickable-items." + keys));
            }
            if (keys.startsWith("reset-all")) {
                resetButton.add(section.getConfigurationSection("clickable-items." + keys));
            }
            if (keys.startsWith("turn-all-on")) {
                turnOnButton.add(section.getConfigurationSection("clickable-items." + keys));
            }
            if (!keys.startsWith("permission-")) continue;

            String permission = keys.replace("permission-", "").split("-")[0];
            String townyPermission = keys.replace("permission-", "").split("-")[1];
            permissionButtons.computeIfAbsent(permission + "/" + townyPermission, x -> new ArrayList<>()).add(section.getConfigurationSection("clickable-items." + keys));
        }
    }

    @Override
    public FrameworkMenu open(Player player, Function<String, String> function) {
        Town town = this.townyAddon.getTown(player);
        if (town == null) return null;

        FrameworkMenu menu = super.open(player, function);
        this.backButton.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        this.resetButton.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        this.turnOnButton.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));

        this.permissionButtons.forEach((permission, sections) -> {
            String[] split = permission.split("/");
            try {
                TownyPermission.ActionType actionType = TownyPermission.ActionType.valueOf(split[0].toUpperCase());
                TownyPermission.PermLevel permLevel = TownyPermission.PermLevel.valueOf(split[1].toUpperCase());
                boolean hasPermission = town.getPermissions().getPerm(permLevel, actionType);
                sections.forEach(section -> {
                    Function<List<String>, List<String>> replaceFunction = x -> x.stream().map(x2 -> x2.replace("%value%", hasPermission ? this.on : this.off)).toList();
                    menu.setItem(section.getInt("slot"), this.createItem(section, x -> replaceFunction.apply(Collections.singletonList(x)).get(0), replaceFunction));
                });
            } catch (Exception ex) {
                ex.printStackTrace();
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
                this.menuManager.getMenu(TownyMenu.class).open(player);
                return true;
            }
        }
        Town town = this.townyAddon.getTown(player);
        if (town == null) return true;

        for (ConfigurationSection section : resetButton) {
            if (section != null && slot == section.getInt("slot") && this.checkPerm(player, PermissionNodes.TOWNY_COMMAND_TOWN_SET_PERM.getNode())) {
                town.getPermissions().reset();
                town.save();
                this.open(player);
                return true;
            }
        }
        for (ConfigurationSection section : turnOnButton) {
            if (section != null && slot == section.getInt("slot") && this.checkPerm(player, PermissionNodes.TOWNY_COMMAND_TOWN_SET_PERM.getNode())) {
                town.getPermissions().setAll(true);
                town.save();
                this.open(player);
                return true;
            }
        }
        for (Map.Entry<String, List<ConfigurationSection>> entry : permissionButtons.entrySet()) {
            String[] split = entry.getKey().split("/");
            try {
                TownyPermission.ActionType actionType = TownyPermission.ActionType.valueOf(split[0].toUpperCase());
                TownyPermission.PermLevel permLevel = TownyPermission.PermLevel.valueOf(split[1].toUpperCase());

                for (ConfigurationSection section : entry.getValue()) {
                    if (section != null && slot == section.getInt("slot") && this.checkPerm(player, PermissionNodes.TOWNY_COMMAND_TOWN_SET_PERM.getNode())) {
                        town.getPermissions().set((split[1] + split[0]).replace("_", "").toLowerCase(), !town.getPermissions().getPerm(permLevel, actionType));
                        town.save();
                        this.open(player);
                        return true;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return true;
    }
}
