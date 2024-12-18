package com.kzics.quirksmha.listener;

import com.kzics.quirksmha.abilities.BlackWhipAbility;
import com.kzics.quirksmha.abilities.Quirk;
import com.kzics.quirksmha.abilities.QuirkAbility;
import com.kzics.quirksmha.abilities.SixEyesAbility;
import com.kzics.quirksmha.manager.ManagerHandler;
import com.kzics.quirksmha.quirks.BulletLaserQuirk;
import com.kzics.quirksmha.quirks.ForcePushQuirk;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class PlayerListeners implements Listener {

    private final ManagerHandler managerHandler;

    public PlayerListeners(ManagerHandler managerHandler) {
        this.managerHandler = managerHandler;
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
       // Quirk quirk = managerHandler.quirkManager().getQuirk(event.getPlayer());
        //quirk.getAbilities().forEach(ability -> ability.onInteract(event));
        QuirkAbility quirkAbility = new SixEyesAbility(managerHandler.quirkManager());
        quirkAbility.onInteract(event);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        QuirkAbility activeAbility = managerHandler.quirkManager().getActiveAbility(event.getPlayer().getUniqueId());
        if(activeAbility != null) {
            activeAbility.onSneak(event);
        }
    }

    private void generateParticles(Player player, boolean isSneakCharge) {
        Location particleLocation;
        if (isSneakCharge) {
            particleLocation = player.getLocation().add(0, 0.2, 0);
            player.getWorld().spawnParticle(Particle.DUST, particleLocation, 10, 0.3, 0.2, 0.3,
                    new Particle.DustOptions(Color.RED, 1.0f));
        } else {
            particleLocation = player.getLocation().add(0, 1.2, 0);
            player.getWorld().spawnParticle(Particle.DUST, particleLocation, 10, 0.3, 0.2, 0.3,
                    new Particle.DustOptions(Color.RED, 1.0f));
        }
    }

    @EventHandler
    public void onPunch(PlayerInteractEvent event) {
        QuirkAbility activeAbility = managerHandler.quirkManager().getActiveAbility(event.getPlayer().getUniqueId());
        if(activeAbility != null) {
            activeAbility.activate(event.getPlayer());
        }

        if(managerHandler.cacheManager().isFajin(event.getPlayer().getUniqueId())) {
            managerHandler.cacheManager().addFajin(event.getPlayer().getUniqueId(), 1);
            generateParticles(event.getPlayer(), false);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        //managerHandler.quirkManager().addQuirk(event.getPlayer(), new BulletLaserQuirk());
        managerHandler.quirkManager().addQuirk(event.getPlayer(), new ForcePushQuirk());
        managerHandler.playerDataManager().initializePlayer(event.getPlayer().getUniqueId(), managerHandler.quirkManager().getQuirk(event.getPlayer().getUniqueId()));
        managerHandler.quirkManager().setActiveAbility(event.getPlayer().getUniqueId(), new BlackWhipAbility());
    }
}
