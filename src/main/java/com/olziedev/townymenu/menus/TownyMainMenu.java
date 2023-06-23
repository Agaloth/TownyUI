package com.olziedev.townymenu.menus;

import com.olziedev.olziemenu.OlzieMenu;
import com.olziedev.olziemenu.framework.menu.FrameworkMenu;
import com.olziedev.townymenu.events.ChatTypeAction;
import com.olziedev.townymenu.utils.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TownyMainMenu extends Menu {

    private final List<ConfigurationSection> createTown = new ArrayList<>();
    private final List<ConfigurationSection> joinTown = new ArrayList<>();

    public TownyMainMenu(ConfigurationSection section, OlzieMenu olzieMenu) {
        super(section, olzieMenu);
        ConfigurationSection clickableItems = section.getConfigurationSection("clickable-items");
        if (clickableItems == null) return;

        for (String keys : clickableItems.getKeys(false)) {
            if (keys.startsWith("create-town")) {
                createTown.add(section.getConfigurationSection("clickable-items." + keys));
            }
            if (keys.startsWith("join-town")) {
                joinTown.add(section.getConfigurationSection("clickable-items." + keys));
            }
        }
    }

    @Override
    public FrameworkMenu open(Player player, Function<String, String> function) {
        FrameworkMenu menu = super.open(player, function);
        createTown.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        joinTown.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        return menu;
    }

    @Override
    public boolean onMenuClick(InventoryClickEvent event, FrameworkMenu menu) {
        int slot = event.getSlot();
        Player player = (Player) event.getWhoClicked();
        for (ConfigurationSection section : createTown) {
            if (section != null && slot == section.getInt("slot")) {
                ChatTypeAction.add(player, Configuration.getConfig().getString("lang.type-the-name-of-the-town"), name -> {
                    townyAddon.execute(player, "create " + name);
                    return true;
                });
                return true;
            }
        }
        for (ConfigurationSection section : joinTown) {
            if (section != null && slot == section.getInt("slot")) {
                this.menuManager.getMenu(JoinTownMenu.class).open(player);
                return true;
            }
        }
        return true;
    }
}
