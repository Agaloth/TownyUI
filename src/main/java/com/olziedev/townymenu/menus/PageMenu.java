package com.olziedev.townymenu.menus;

import com.olziedev.olziemenu.OlzieMenu;
import com.olziedev.olziemenu.framework.menu.PaginationMenuAdapter;
import com.olziedev.townymenu.TownyMenuPlugin;
import com.olziedev.townymenu.addons.TownyAddon;
import com.olziedev.townymenu.managers.MenuManager;
import com.olziedev.townymenu.utils.ItemFactory;
import com.olziedev.townymenu.utils.NumberUtils;
import com.olziedev.townymenu.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;

import java.util.stream.Collectors;

public abstract class PageMenu extends PaginationMenuAdapter implements ItemFactory {

    protected ConfigurationSection section;
    protected MenuManager menuManager;
    protected TownyAddon townyAddon;

    public PageMenu(ConfigurationSection section, OlzieMenu olzieMenu) {
        super(section.getInt("size") / 9, Utils.color(section.getString("title", "")), section.getStringList("blacklisted-slots").stream().map(x -> NumberUtils.toInt(x, -1)).collect(Collectors.toList()), olzieMenu);
        this.section = section;
        this.menuManager = TownyMenuPlugin.getMenuManager();
        this.townyAddon = TownyMenuPlugin.getAddonManager().getAddon(TownyAddon.class);
    }

    @Override
    public void load() {
        ConfigurationSection itemSection = this.section.getConfigurationSection("items");
        if (itemSection == null) return;

        for (String key : itemSection.getKeys(false)) {
            ConfigurationSection item = itemSection.getConfigurationSection(key);
            if (item == null) continue;

            this.setItem(item.getInt("slot"), this.createItem(item));
        }
    }
}
