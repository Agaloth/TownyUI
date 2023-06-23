package com.olziedev.townymenu.addons;

import com.olziedev.townymenu.TownyMenuPlugin;
import org.bukkit.event.Listener;

public abstract class Addon implements Listener {

    protected final TownyMenuPlugin plugin;

    public Addon(TownyMenuPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract void load();

    public abstract boolean isEnabled();

    public void close() {} // default
}
