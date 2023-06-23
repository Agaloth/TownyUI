package com.olziedev.townymenu.managers;

import com.olziedev.olziemenu.OlzieMenu;
import com.olziedev.olziemenu.framework.menu.MenuAdapter;
import com.olziedev.townymenu.TownyMenuPlugin;
import com.olziedev.townymenu.menus.JoinTownMenu;
import com.olziedev.townymenu.menus.TownyMainMenu;
import com.olziedev.townymenu.menus.nation.*;
import com.olziedev.townymenu.menus.town.*;
import com.olziedev.townymenu.menus.town.plot.*;
import com.olziedev.townymenu.utils.Configuration;

import java.util.ArrayList;
import java.util.List;

public class MenuManager extends Manager {

    private final List<MenuAdapter> menus;
    private final OlzieMenu olzieMenu;

    public MenuManager(TownyMenuPlugin plugin) {
        super(plugin);
        menus = new ArrayList<>();
        this.olzieMenu = new OlzieMenu(plugin);
    }

    @Override
    public void setup() {
        menus.add(new TownyMainMenu(Configuration.getGuiMenu("menu.yml"), olzieMenu));
        menus.add(new JoinTownMenu(Configuration.getGuiMenu("join.yml"), olzieMenu));
        menus.add(new TownyMenu(Configuration.getGuiMenu("town.yml"), olzieMenu));
        menus.add(new ToggleSettingsMenu(Configuration.getGuiMenu("togglesettings.yml"), olzieMenu));
        menus.add(new ResidentMenu(Configuration.getGuiMenu("resident.yml"), olzieMenu));
        menus.add(new PermissionsMenu(Configuration.getGuiMenu("permissions.yml"), olzieMenu));
        menus.add(new OtherSettingsMenu(Configuration.getGuiMenu("othersettings.yml"), olzieMenu));
        menus.add(new EcoMenu(Configuration.getGuiMenu("eco.yml"), olzieMenu));
        menus.add(new InvitePlayerMenu(Configuration.getGuiMenu("inviteplayer.yml"), olzieMenu));
        menus.add(new ExtraTownMenu(Configuration.getGuiMenu("extratown.yml"), olzieMenu));
        menus.add(new ResidentEditMenu(Configuration.getGuiMenu("residentedit.yml"), olzieMenu));

        menus.add(new PlotMenu(Configuration.getGuiMenu("plot/plot.yml"), olzieMenu));
        menus.add(new FriendMenu(Configuration.getGuiMenu("plot/friend.yml"), olzieMenu));
        menus.add(new PlotPermissionsMenu(Configuration.getGuiMenu("plot/plotpermissions.yml"), olzieMenu));
        menus.add(new PlotAdminMenu(Configuration.getGuiMenu("plot/plotadmin.yml"), olzieMenu));
        menus.add(new PlotToggleSettingsMenu(Configuration.getGuiMenu("plot/plottogglesettings.yml"), olzieMenu));

        menus.add(new ExtraNationInfoMenu(Configuration.getGuiMenu("nation/extranationinfo.yml"), olzieMenu));
        menus.add(new ToggleNationSettingsMenu(Configuration.getGuiMenu("nation/togglenationsettings.yml"), olzieMenu));
        menus.add(new NationMenu(Configuration.getGuiMenu("nation/nation.yml"), olzieMenu));
        menus.add(new InviteTownMenu(Configuration.getGuiMenu("nation/invitetown.yml"), olzieMenu));
        menus.add(new NationMainMenu(Configuration.getGuiMenu("nation/nationmenu.yml"), olzieMenu));
        menus.add(new NationEcoMenu(Configuration.getGuiMenu("nation/nationeco.yml"), olzieMenu));
        menus.add(new NationSettingsMenu(Configuration.getGuiMenu("nation/nationsettings.yml"), olzieMenu));
        menus.add(new NationResidentMenu(Configuration.getGuiMenu("nation/nationresident.yml"), olzieMenu));
        menus.add(new TownListMenu(Configuration.getGuiMenu("nation/townlist.yml"), olzieMenu));
        menus.add(new NationJoinMenu(Configuration.getGuiMenu("nation/nationjoin.yml"), olzieMenu));
        menus.add(new NationResidentEditMenu(Configuration.getGuiMenu("nation/nationresidentedit.yml"), olzieMenu));
        menus.add(new TownListEditMenu(Configuration.getGuiMenu("nation/townlistedit.yml"), olzieMenu));
    }

    @Override
    public void load() {
        menus.forEach(MenuAdapter::load);
    }

    @SuppressWarnings("unchecked")
    public <T extends MenuAdapter> T getMenu(Class<T> clazz) {
        return menus.stream().filter(x -> x.getClass().equals(clazz)).map(x -> (T) x).findFirst().orElse(null);
    }

    @Override
    public void close() {
        menus.clear();
    }

    public OlzieMenu getOlzieMenu() {
        return this.olzieMenu;
    }
}
