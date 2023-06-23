package com.olziedev.townymenu.managers;

import com.olziedev.townymenu.TownyMenuPlugin;

public abstract class Manager {

    public final TownyMenuPlugin plugin;

    public Manager(TownyMenuPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract void load();

    public abstract void setup();

    public void close() {} // default
}