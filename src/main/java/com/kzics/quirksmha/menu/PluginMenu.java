package com.kzics.quirksmha.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class PluginMenu implements InventoryHolder {

    public final Inventory inventory;

    public PluginMenu(Component name, int slot) {
        this.inventory = Bukkit.createInventory(this, slot, name);
    }

    public abstract void open(Player player);
    public abstract void handle(InventoryClickEvent event);
}
