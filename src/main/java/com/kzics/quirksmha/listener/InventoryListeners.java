package com.kzics.quirksmha.listener;

import com.kzics.quirksmha.menu.PluginMenu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class InventoryListeners implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory topInventory = event.getView().getTopInventory();

        if(topInventory.getHolder() instanceof PluginMenu menu) {
            menu.handle(event);
        }
    }
}
