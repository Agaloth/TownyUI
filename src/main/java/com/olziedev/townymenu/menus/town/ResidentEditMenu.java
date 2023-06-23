package com.olziedev.townymenu.menus.town;

import com.olziedev.olziemenu.OlzieMenu;
import com.olziedev.olziemenu.framework.menu.FrameworkMenu;
import com.olziedev.townymenu.events.ChatTypeAction;
import com.olziedev.townymenu.menus.Menu;
import com.olziedev.townymenu.utils.Configuration;
import com.olziedev.townymenu.utils.Utils;
import com.palmergames.bukkit.towny.object.Resident;
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

public class ResidentEditMenu extends Menu {

    public final Map<UUID, Resident> residents = new ConcurrentHashMap<>();
    private final List<ConfigurationSection> backButton = new ArrayList<>();
    private final List<ConfigurationSection> setRankButton = new ArrayList<>();
    private final List<ConfigurationSection> kickPlayerButton = new ArrayList<>();
    private final List<ConfigurationSection> setTitleButton = new ArrayList<>();
    private final List<ConfigurationSection> giveMayorButton = new ArrayList<>();

    public ResidentEditMenu(ConfigurationSection section, OlzieMenu olzieMenu) {
        super(section, olzieMenu);
        ConfigurationSection clickableItems = section.getConfigurationSection("clickable-items");
        if (clickableItems == null) return;

        for (String keys : clickableItems.getKeys(false)) {
            if (keys.startsWith("back")) {
                backButton.add(section.getConfigurationSection("clickable-items." + keys));
            }
            if (keys.startsWith("kick-player")) {
                kickPlayerButton.add(section.getConfigurationSection("clickable-items." + keys));
            }
            if (keys.startsWith("set-title")) {
                setTitleButton.add(section.getConfigurationSection("clickable-items." + keys));
            }
            if (keys.startsWith("set-resident-rank")) {
                setRankButton.add(section.getConfigurationSection("clickable-items." + keys));
            }
            if (keys.startsWith("give-resident-mayor")) {
                giveMayorButton.add(section.getConfigurationSection("clickable-items." + keys));
            }
        }
    }

    @Override
    public FrameworkMenu open(Player player, Function<String, String> function) {
        FrameworkMenu menu = super.open(player, function);
        this.backButton.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        this.kickPlayerButton.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        this.setTitleButton.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        this.setRankButton.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        this.giveMayorButton.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
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
        for (ConfigurationSection section : kickPlayerButton) {
            if (section != null && slot == section.getInt("slot") && this.checkPerm(player, PermissionNodes.TOWNY_COMMAND_TOWN_KICK.getNode())) {
                Resident resident = this.residents.get(player.getUniqueId());
                if (resident == null) return true;

                Town town = resident.getTownOrNull();
                if (town == null) return true;

                try {
                    town.removeResident(resident);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                resident.save();
                Utils.sendMessage(player, Configuration.getConfig().getString("lang.kicked-player").replace("%player%", resident.getName()));
                this.menuManager.getMenu(ResidentMenu.class).open(player);
                return true;
            }
        }
        for (ConfigurationSection section : setTitleButton) {
            if (section != null && slot == section.getInt("slot") && this.checkPerm(player, PermissionNodes.TOWNY_COMMAND_TOWN_SET_TITLE.getNode())) {
                Resident resident = this.residents.get(player.getUniqueId());
                if (resident == null) return true;

                Town town = resident.getTownOrNull();
                if (town == null) return true;

                ChatTypeAction.add(player, Configuration.getConfig().getString("lang.enter-title").replace("%player%", resident.getName()), input -> {
                    if (input.length() > 10) {
                        Utils.sendMessage(player, Configuration.getConfig().getString("lang.title-too-long"));
                        return false;
                    }
                    Utils.sendMessage(player, Configuration.getConfig().getString("lang.added-title").replace("%title%", input).replace("%player%", resident.getName()));
                    resident.setTitle(input);
                    resident.save();
                    this.menuManager.getMenu(ResidentMenu.class).open(player);
                    return true;
                });
                return true;
            }
        }
        for (ConfigurationSection section : setRankButton) {
            if (section != null && slot == section.getInt("slot") && this.checkPerm(player, PermissionNodes.TOWNY_COMMAND_TOWN_RANK.getNode())) {
                Resident resident = this.residents.get(player.getUniqueId());
                if (resident == null) return true;

                ChatTypeAction.add(player, Configuration.getConfig().getString("lang.enter-rank").replace("%player%", resident.getName()), input -> {
                    if (input.equalsIgnoreCase("remove")) {
                        resident.getTownRanks().forEach(resident::removeTownRank);
                        resident.save();
                        Utils.sendMessage(player, Configuration.getConfig().getString("lang.removed-rank").replace("%player%", resident.getName()));
                        return true;
                    }
                    if (!this.townyAddon.getTownRanks().contains(input)) {
                        Utils.sendMessage(player, Configuration.getConfig().getString("lang.invalid-rank"));
                        return false;
                    }
                    Utils.sendMessage(player, Configuration.getConfig().getString("lang.added-rank").replace("%rank%", input).replace("%player%", resident.getName()));
                    resident.addTownRank(input);
                    resident.save();
                    this.menuManager.getMenu(ResidentMenu.class).open(player);
                    return true;
                });
                return true;
            }
        }
        for (ConfigurationSection section : giveMayorButton) {
            if (section != null && slot == section.getInt("slot") && this.checkPerm(player, PermissionNodes.TOWNY_COMMAND_TOWN_SET_MAYOR.getNode())) {
                Resident resident = this.residents.get(player.getUniqueId());
                if (resident == null) return true;

                Town town = resident.getTownOrNull();
                if (town == null) return true;

                Utils.sendMessage(player, Configuration.getConfig().getString("lang.gave-mayor").replace("%player%", resident.getName()));
                town.setMayor(resident);
                this.menuManager.getMenu(ResidentMenu.class).open(player);
                return true;
            }
        }
        return true;
    }

    @Override
    public boolean onMenuClose(InventoryCloseEvent event, FrameworkMenu menu) {
        Player player = (Player) event.getPlayer();
        this.residents.remove(player.getUniqueId());
        return false;
    }
}
