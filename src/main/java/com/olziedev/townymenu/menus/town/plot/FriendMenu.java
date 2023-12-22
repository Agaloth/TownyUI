package com.olziedev.townymenu.menus.town.plot;

import com.olziedev.olziemenu.OlzieMenu;
import com.olziedev.olziemenu.framework.menu.FrameworkMenu;
import com.olziedev.townymenu.menus.Menu;
import com.olziedev.townymenu.menus.PageMenu;
import com.olziedev.townymenu.menus.town.TownyMenu;
import com.olziedev.townymenu.utils.Configuration;
import com.olziedev.townymenu.utils.Utils;
import com.palmergames.bukkit.towny.exceptions.AlreadyRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FriendMenu extends PageMenu {

    private final List<ConfigurationSection> backButton = new ArrayList<>();
    private final List<ConfigurationSection> nextButton = new ArrayList<>();
    private final List<ConfigurationSection> previousButton = new ArrayList<>();

    public FriendMenu(ConfigurationSection section, OlzieMenu olzieMenu) {
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
        Town town = this.townyAddon.getTown(player);
        if (town == null) return null;

        TownBlock townBlock = this.townyAddon.getTownBlock(player.getLocation());
        if (townBlock == null) return null;

        Resident playerResident = this.townyAddon.getResident(player);
        List<Player> players = !pageItems.isEmpty() ? Collections.emptyList() : Bukkit.getOnlinePlayers().stream().filter(x -> !x.getUniqueId().equals(playerResident.getUUID())).collect(Collectors.toList());
        for (Player resident : players) {
            Function<List<String>, List<String>> replaceFunction = strings -> strings.stream().map(s -> s.replace("%player%", resident.getName())).collect(Collectors.toList());
            pageItems.add(new PageItem<>(() -> this.createItem(this.section.getConfigurationSection(playerResident.getFriends().stream().anyMatch(x -> x.getUUID().equals(resident.getUniqueId())) ? "friend-item" : "no-friend-item"), x -> replaceFunction.apply(Collections.singletonList(x)).get(0), replaceFunction), (click, item) -> {
                try {
                    playerResident.addFriend(this.townyAddon.getResident(resident));
                    Utils.sendMessage(player, Configuration.getConfig().getString("lang.friend-added").replace("%player%", resident.getName()));
                } catch (AlreadyRegisteredException e) {
                    playerResident.removeFriend(this.townyAddon.getResident(resident));
                    Utils.sendMessage(player, Configuration.getConfig().getString("lang.friend-removed").replace("%player%", resident.getName()));
                }
                this.resetCache(player);
                this.dontActivateClose(player);
                this.open(player, function);
            }, resident));
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
                this.menuManager.getMenu(PlotMenu.class).open(player);
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
