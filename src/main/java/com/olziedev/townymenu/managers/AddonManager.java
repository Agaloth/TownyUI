package com.olziedev.townymenu.managers;

import com.olziedev.townymenu.TownyMenuPlugin;
import com.olziedev.townymenu.addons.Addon;
import com.olziedev.townymenu.addons.TownyAddon;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class AddonManager extends Manager {

    private final List<Addon> addons;

    public AddonManager(TownyMenuPlugin plugin) {
        super(plugin);
        this.addons = new ArrayList<>();
    }

    @Override
    public void setup() {
        addons.add(new TownyAddon(plugin));
    }

    @Override
    public void load() {
        addons.stream().filter(Addon::isEnabled).forEach(x -> {
            x.load();
            Bukkit.getPluginManager().registerEvents(x, plugin);
        });
    }

    @Override
    public void close() {
        addons.forEach(Addon::close);
    }

    @SuppressWarnings("unchecked")
    public <T extends Addon> T getAddon(Class<T> clazz) {
        return addons.stream().filter(x -> x.getClass().equals(clazz)).map(x -> (T) x).findFirst().orElse(null);
    }
}
