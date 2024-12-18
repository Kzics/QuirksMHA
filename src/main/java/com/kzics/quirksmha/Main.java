package com.kzics.quirksmha;

import com.kzics.quirksmha.abilities.*;
import com.kzics.quirksmha.abilities.stardust.MeteorExplosionAbility;
import com.kzics.quirksmha.abilities.stardust.StardustBreakerAbility;
import com.kzics.quirksmha.abilities.stardust.StardustFallAbility;
import com.kzics.quirksmha.commands.abilities.QuirkAbilitiesCommand;
import com.kzics.quirksmha.commands.stats.StatsCommand;
import com.kzics.quirksmha.listener.InventoryListeners;
import com.kzics.quirksmha.listener.PlayerListeners;
import com.kzics.quirksmha.manager.ManagerHandler;
import com.kzics.quirksmha.quirks.ForcePushQuirk;
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
        managerHandler = new ManagerHandler(this);
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new InventoryListeners(), this);
        getServer().getPluginManager().registerEvents(new PlayerListeners(managerHandler), this);

        managerHandler.quirkManager().addAbility(new ForcePushAbility());
        managerHandler.quirkManager().addAbility(new FajinAbility(managerHandler.cacheManager()));
        managerHandler.quirkManager().addAbility(new BurstAbility(this));
        managerHandler.quirkManager().addAbility(new InfraredAbility());
        managerHandler.quirkManager().addAbility(new ImpureBeamAbility());
        managerHandler.quirkManager().addAbility(new DarkBallAbility());
        managerHandler.quirkManager().addAbility(new DangerSenseAbility());
        managerHandler.quirkManager().addAbility(new BarrierAbility());
        managerHandler.quirkManager().addAbility(new BulletLaserAbility());
        managerHandler.quirkManager().addAbility(new BlackWhipAbility());
        managerHandler.quirkManager().addAbility(new AirCannonAbility());
        managerHandler.quirkManager().addAbility(new PhaseAbility());
        managerHandler.quirkManager().addAbility(new ErasureAbility());
        managerHandler.quirkManager().addAbility(new SixEyesAbility(managerHandler.quirkManager()));
        managerHandler.quirkManager().addAbility(new WeakSpotAbility());
        managerHandler.quirkManager().addAbility(new SuperRegenAbility());
        managerHandler.quirkManager().addAbility(new BloodManipulationAbility());
        managerHandler.quirkManager().addAbility(new CloneAbility());
        managerHandler.quirkManager().addAbility(new SpaceTimeAbility());
        managerHandler.quirkManager().addAbility(new GalePalmAbility(managerHandler));
        managerHandler.quirkManager().addAbility(new SealingAbility());
        managerHandler.quirkManager().addAbility(new MeteorExplosionAbility());
        managerHandler.quirkManager().addAbility(new StardustFallAbility());
        managerHandler.quirkManager().addAbility(new StardustBreakerAbility());

        getCommand("stats").setExecutor(new StatsCommand(managerHandler));
        getCommand("quirks").setExecutor(new QuirkAbilitiesCommand(managerHandler));

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
        } else if(event.getItem().getType().equals(Material.IRON_SHOVEL)) {
            new PhaseAbility().activate(event.getPlayer());
        } else if(event.getItem().getType().equals(Material.DIAMOND_SHOVEL)) {
            ForcePushQuirk quirk = (ForcePushQuirk) managerHandler.quirkManager().getQuirk(event.getPlayer().getUniqueId());
            quirk.getAbilities().forEach(ability -> ability.onInteractAt(event));
        }
    }
}
