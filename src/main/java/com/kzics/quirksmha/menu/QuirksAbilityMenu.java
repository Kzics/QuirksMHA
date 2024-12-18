package com.kzics.quirksmha.menu;

import com.kzics.quirksmha.Main;
import com.kzics.quirksmha.abilities.Quirk;
import com.kzics.quirksmha.abilities.QuirkAbility;
import com.kzics.quirksmha.abilities.QuirkManager;
import com.kzics.quirksmha.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class QuirksAbilityMenu extends PluginMenu {
    private final static NamespacedKey ACTIVE_ABILITY_KEY = new NamespacedKey(Main.getInstance(), "active_ability");
    private final QuirkManager quirkManager;

    public QuirksAbilityMenu(QuirkManager quirkManager) {
        super(Component.text("Quirks Ability Menu"), 54);
        this.quirkManager = quirkManager;
    }

    @Override
    public void open(Player player) {
        inventory.clear();
        for (QuirkAbility ability : quirkManager.getAbilities()) {
            ItemStack item;
            if (quirkManager.getActiveAbility(player.getUniqueId()).equals(ability)) {
                item = new ItemBuilder(Material.LIME_DYE)
                        .addPersistentData(ACTIVE_ABILITY_KEY, ability.name())
                        .setName(Component.text(ability.name()))
                        .build();
            } else {
                item = new ItemBuilder(Material.GUNPOWDER)
                        .setName(Component.text(ability.name()))
                        .addPersistentData(ACTIVE_ABILITY_KEY, ability.name())
                        .build();
            }
            inventory.addItem(item);
        }
        player.openInventory(inventory);
    }

    @Override
    public void handle(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        event.setCancelled(true);


        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        String abilityName = clickedItem.getItemMeta().getPersistentDataContainer().get(ACTIVE_ABILITY_KEY, PersistentDataType.STRING);
        if (abilityName == null) return;

        Player player = (Player) event.getWhoClicked();
        QuirkAbility selectedAbility = quirkManager.getAbility(abilityName);
        if (selectedAbility != null) {
            quirkManager.setActiveAbility(player.getUniqueId(), selectedAbility);
            player.sendMessage(Component.text("Ability " + abilityName + " selected!"));
            open(player); // Refresh the inventory to show the selected ability
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}