package com.olziedev.townymenu.menus.nation;

import com.olziedev.olziemenu.OlzieMenu;
import com.olziedev.olziemenu.framework.menu.FrameworkMenu;
import com.olziedev.townymenu.events.ChatTypeAction;
import com.olziedev.townymenu.menus.Menu;
import com.olziedev.townymenu.utils.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class NationMainMenu extends Menu {

    private final List<ConfigurationSection> createNation = new ArrayList<>();
    private final List<ConfigurationSection> joinNation = new ArrayList<>();

    public NationMainMenu(ConfigurationSection section, OlzieMenu olzieMenu) {
        super(section, olzieMenu);
        ConfigurationSection clickableItems = section.getConfigurationSection("clickable-items");
        if (clickableItems == null) return;

        for (String keys : clickableItems.getKeys(false)) {
            if (keys.startsWith("create-nation")) {
                createNation.add(section.getConfigurationSection("clickable-items." + keys));
            }
            if (keys.startsWith("join-nation")) {
                joinNation.add(section.getConfigurationSection("clickable-items." + keys));
            }
        }
    }

    @Override
    public FrameworkMenu open(Player player, Function<String, String> function) {
        FrameworkMenu menu = super.open(player, function);
        createNation.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        joinNation.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section)));
        return menu;
    }

    @Override
    public boolean onMenuClick(InventoryClickEvent event, FrameworkMenu menu) {
        int slot = event.getSlot();
        Player player = (Player) event.getWhoClicked();
        for (ConfigurationSection section : createNation) {
            if (section != null && slot == section.getInt("slot")) {
                ChatTypeAction.add(player, Configuration.getConfig().getString("lang.type-the-name-of-the-nation"), name -> {
                    townyAddon.execute(player, "create " + name);
                    return true;
                });
                return true;
            }
        }
        for (ConfigurationSection section : joinNation) {
            if (section != null && slot == section.getInt("slot")) {
                this.menuManager.getMenu(NationJoinMenu.class).open(player);
                return true;
            }
        }
        return true;
    }
}
