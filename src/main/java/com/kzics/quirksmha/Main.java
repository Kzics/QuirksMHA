package com.kzics.quirksmha;

import com.kzics.quirksmha.abilities.*;
import com.kzics.quirksmha.commands.stats.StatsCommand;
import com.kzics.quirksmha.listener.InventoryListeners;
import com.kzics.quirksmha.listener.PlayerListeners;
import com.kzics.quirksmha.manager.ManagerHandler;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {


    private static Main instance;
    private ManagerHandler managerHandler;
    @Override
    public void onEnable() {
        instance = this;
        managerHandler = new ManagerHandler();
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new InventoryListeners(), this);
        getServer().getPluginManager().registerEvents(new PlayerListeners(managerHandler), this);

        getCommand("stats").setExecutor(new StatsCommand(managerHandler));
    }

    public ManagerHandler managerHandler() {
        return managerHandler;
    }

    public static Main getInstance() {
        return instance;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if(event.getItem().getType().equals(Material.WOODEN_SWORD)) {
            new AirCannonAbility().activate(event.getPlayer());
        }else if(event.getItem().getType().equals(Material.STONE_SWORD)) {
            new BlackWhipAbility().activate(event.getPlayer());
            }else if(event.getItem().getType().equals(Material.IRON_SWORD)) {
            new BulletLaserAbility().activate(event.getPlayer());
        }else if(event.getItem().getType().equals(Material.DIAMOND_SWORD)) {
            new BarrierAbility().activate(event.getPlayer());
        } else if(event.getItem().getType().equals(Material.GOLDEN_SWORD)) {
            new DangerSenseAbility().activate(event.getPlayer());
        } else if(event.getItem().getType().equals(Material.NETHERITE_SWORD)) {
            new DarkBallAbility().activate(event.getPlayer());
        } else if(event.getItem().getType().equals(Material.WOODEN_AXE)) {
            new ImpureBeamAbility().activate(event.getPlayer());
        } else if( event.getItem().getType().equals(Material.STONE_AXE)) {
            new InfraredAbility().activate(event.getPlayer());
        } else if (event.getItem().getType().equals(Material.GOLDEN_AXE)) {
            new BurstAbility(this).activate(event.getPlayer());
        } else if(event.getItem().getType().equals(Material.DIAMOND_AXE)) {
            new FajinAbility(managerHandler.cacheManager()).activate(event.getPlayer());
        } else if (event.getItem().getType().equals(Material.NETHERITE_AXE)) {
            new ErasureAbility().activate(event.getPlayer());
        }
    }
}
