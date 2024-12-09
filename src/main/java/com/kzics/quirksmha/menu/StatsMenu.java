package com.kzics.quirksmha.menu;

import com.kzics.quirksmha.data.PlayerData;
import com.kzics.quirksmha.manager.ManagerHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StatsMenu extends PluginMenu{
    private final ManagerHandler managerHandler;
    public StatsMenu(ManagerHandler managerHandler) {
        super(Component.text("Stats"), 27);
        this.managerHandler = managerHandler;
    }

    @Override
    public void open(Player player) {
        PlayerData playerData = managerHandler.playerDataManager().getPlayerData(player.getUniqueId());
        int c = 1;
        for (PlayerData.Stat stat : PlayerData.Stat.values()) {
            inventory.setItem(11 + c,createItem(playerData, stat));
            c += 1;
        }
        player.openInventory(inventory);
    }

    @Override
    public void handle(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        PlayerData playerData = managerHandler.playerDataManager().getPlayerData(player.getUniqueId());
        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();
        PlayerData.Stat stat = PlayerData.Stat.valueOf(serializer.serialize(item.getItemMeta().displayName()));
        playerData.levelUpStat(stat);
        new StatsMenu(managerHandler).open(player);
    }

    private ItemStack createItem(PlayerData playerData, PlayerData.Stat stat) {
        ItemStack item = new ItemStack(material(stat));
        item.editMeta(meta -> {
            meta.displayName(Component.text(stat.name()));
            meta.lore(List.of(
                    Component.text("Level: " + playerData.getStatLevel(stat) + "/8")
            ));
        });

        return item;
    }

    private Material material(PlayerData.Stat stat) {
        switch (stat) {
            case STRENGTH:
                return Material.IRON_SWORD;
            case SPEED:
                return Material.FEATHER;
            case HEALTH:
                return Material.REDSTONE;
            case DURABILITY:
                return Material.DIAMOND_CHESTPLATE;
            default:
                return Material.BARRIER;
        }
    }
    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}
