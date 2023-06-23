package com.olziedev.townymenu.addons;

import com.olziedev.townymenu.TownyMenuPlugin;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.permissions.TownyPerms;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class TownyAddon extends Addon {
    public TownyAddon(TownyMenuPlugin plugin) {
        super(plugin);
    }

    @Override
    public void load() {

    }

    @Override
    public boolean isEnabled() {
        return Bukkit.getPluginManager().getPlugin("Towny") != null;
    }

    public Town getTown(Player player) {
        return TownyAPI.getInstance().getTown(player);
    }

    public void execute(Player player, String sub) {
        if (!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(this.plugin, () -> this.execute(player, sub));
            return;
        }
        player.performCommand("town " + sub);
    }

    public void executeNation(Player player, String sub) {
        if (!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(this.plugin, () -> this.executeNation(player, sub));
            return;
        }
        player.performCommand("nation " + sub);
    }

    public void executePlot(Player player, String sub) {
        if (!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(this.plugin, () -> this.executePlot(player, sub));
            return;
        }
        player.performCommand("plot " + sub);
    }

    public List<Town> getJoinableTowns() {
        return TownyAPI.getInstance().getTowns().stream().filter(Town::isOpen).toList();
    }

    public List<Nation> getJoinableNations() {
        return TownyAPI.getInstance().getNations().stream().filter(Nation::isOpen).toList();
    }

    public List<String> getTownRanks() {
        return TownyPerms.getTownRanks();
    }

    public TownBlock getTownBlock(Location location) {
        return TownyAPI.getInstance().getTownBlock(location);
    }

    public Resident getResident(Player player) {
        return TownyUniverse.getInstance().getResident(player.getUniqueId());
    }

    public Nation getNation(Player player) {
        Town town = this.getTown(player);
        if (town == null) {
            return null;
        }
        return town.getNationOrNull();
    }

    public List<Town> getTowns() {
        return TownyAPI.getInstance().getTowns();
    }
}
