package com.olziedev.townymenu.menus.town;

import com.olziedev.olziemenu.OlzieMenu;
import com.olziedev.olziemenu.framework.menu.FrameworkMenu;
import com.olziedev.townymenu.events.ChatTypeAction;
import com.olziedev.townymenu.menus.Menu;
import com.olziedev.townymenu.utils.Configuration;
import com.olziedev.townymenu.utils.NumberUtils;
import com.olziedev.townymenu.utils.Utils;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.permissions.PermissionNodes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class EcoMenu extends Menu {

    private final List<ConfigurationSection> backButton = new ArrayList<>();
    private final List<ConfigurationSection> currentBalance = new ArrayList<>();
    private final List<ConfigurationSection> depositMoney = new ArrayList<>();
    private final List<ConfigurationSection> withdrawMoney = new ArrayList<>();
    private final List<ConfigurationSection> setTax = new ArrayList<>();

    public EcoMenu(ConfigurationSection section, OlzieMenu olzieMenu) {
        super(section, olzieMenu);
        ConfigurationSection clickableItems = section.getConfigurationSection("clickable-items");
        if (clickableItems == null) return;

        for (String keys : clickableItems.getKeys(false)) {
            if (keys.startsWith("back")) {
                backButton.add(section.getConfigurationSection("clickable-items." + keys));
            }
            if (keys.startsWith("current-balance")) {
                currentBalance.add(section.getConfigurationSection("clickable-items." + keys));
            }
            if (keys.startsWith("deposit-money")) {
                depositMoney.add(section.getConfigurationSection("clickable-items." + keys));
            }
            if (keys.startsWith("withdraw-money")) {
                withdrawMoney.add(section.getConfigurationSection("clickable-items." + keys));
            }
            if (keys.startsWith("set-tax")) {
                setTax.add(section.getConfigurationSection("clickable-items." + keys));
            }
        }
    }

    @Override
    public FrameworkMenu open(Player player, Function<String, String> function) {
        Town town = this.townyAddon.getTown(player);
        if (town == null) return null;

        FrameworkMenu menu = super.open(player, function);
        Function<List<String>, List<String>> replaceFunction = strings -> strings.stream().map(string -> string.replace("%balance%", town.getAccount().getHoldingFormattedBalance())).toList();
        this.backButton.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section, x -> replaceFunction.apply(Collections.singletonList(x)).get(0), replaceFunction)));
        this.currentBalance.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section, x -> replaceFunction.apply(Collections.singletonList(x)).get(0), replaceFunction)));
        this.depositMoney.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section, x -> replaceFunction.apply(Collections.singletonList(x)).get(0), replaceFunction)));
        this.withdrawMoney.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section, x -> replaceFunction.apply(Collections.singletonList(x)).get(0), replaceFunction)));
        this.setTax.forEach(section -> menu.setItem(section.getInt("slot"), this.createItem(section, x -> replaceFunction.apply(Collections.singletonList(x)).get(0), replaceFunction)));
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
        for (ConfigurationSection section : depositMoney) {
            if (section != null && slot == section.getInt("slot") && this.checkPerm(player, PermissionNodes.TOWNY_COMMAND_TOWN_DEPOSIT.getNode())) {
                ChatTypeAction.add(player, Configuration.getConfig().getString("lang.enter-deposit"), input -> {
                    int amount = NumberUtils.isDigits(input) ? Integer.parseInt(input) : 0;
                    if (amount <= 0) {
                        Utils.sendMessage(player, Configuration.getConfig().getString("lang.invalid-number"));
                        return false;
                    }
                    this.townyAddon.execute(player, "deposit " + amount);
                    this.menuManager.getMenu(EcoMenu.class).open(player);
                    return true;
                });
                return true;
            }
        }
        for (ConfigurationSection section : withdrawMoney) {
            if (section != null && slot == section.getInt("slot") && this.checkPerm(player, PermissionNodes.TOWNY_COMMAND_TOWN_WITHDRAW.getNode())) {
                ChatTypeAction.add(player, Configuration.getConfig().getString("lang.enter-withdraw"), input -> {
                    int amount = NumberUtils.isDigits(input) ? Integer.parseInt(input) : 0;
                    if (amount <= 0) {
                        Utils.sendMessage(player, Configuration.getConfig().getString("lang.invalid-number"));
                        return false;
                    }
                    this.townyAddon.execute(player, "withdraw " + amount);
                    this.menuManager.getMenu(EcoMenu.class).open(player);
                    return true;
                });
                return true;
            }
        }
        for (ConfigurationSection section : setTax) {
            if (section != null && slot == section.getInt("slot") && this.checkPerm(player, PermissionNodes.TOWNY_COMMAND_TOWN_SET_TAXES.getNode())) {
                ChatTypeAction.add(player, Configuration.getConfig().getString("lang.enter-tax"), input -> {
                    int amount = NumberUtils.isDigits(input) ? Integer.parseInt(input) : 0;
                    if (amount <= 0) {
                        Utils.sendMessage(player, Configuration.getConfig().getString("lang.invalid-number"));
                        return false;
                    }
                    this.townyAddon.execute(player, "set taxes " + amount);
                    this.menuManager.getMenu(EcoMenu.class).open(player);
                    return true;
                });
                return true;
            }
        }
        return true;
    }
}
