package com.kzics.quirksmha.listener;

import com.kzics.quirksmha.Main;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class KnockOutMechanic implements Listener {

    private final Set<UUID> knockedOutPlayers = new HashSet<>(); // Joueurs en état Knock-out
    private final int knockOutDuration = 15; // Durée en secondes avant réveil naturel

    public void applyKnockOut(Player player) {
        player.setPose(Pose.SWIMMING, true);
        player.setSwimming(true);
        System.out.println(player.getPose());

        knockedOutPlayers.add(player.getUniqueId());
        player.sendMessage("Vous êtes KO !");
        //player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0, false, false, false));
        //player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 255, false, false, false));
        // Réveil naturel après une durée configurée
        new BukkitRunnable() {
            @Override
            public void run() {
                if (knockedOutPlayers.contains(player.getUniqueId())) {
                    wakeUpPlayer(player, false); // Réveil naturel
                }
            }
        }.runTaskLater(Main.getInstance(), knockOutDuration * 20L); // Convertir secondes en ticks
    }

    public void wakeUpPlayer(Player player, boolean byDamage) {
        if (!knockedOutPlayers.contains(player.getUniqueId())) return;

        knockedOutPlayers.remove(player.getUniqueId());
        player.setGliding(false);
        player.removePotionEffect(PotionEffectType.BLINDNESS);
        player.removePotionEffect(PotionEffectType.SLOWNESS);
        player.sendMessage(byDamage ? "Vous vous êtes réveillé suite à des dégâts !" : "Vous vous êtes réveillé naturellement.");
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        applyKnockOut((Player) event.getEntity());

        if (event.getEntity() instanceof Player player && knockedOutPlayers.contains(player.getUniqueId())) {
            if (event.getFinalDamage()  >= 1.0) {
                wakeUpPlayer(player, true);
            } else {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onSneak(PlayerItemConsumeEvent event) {
        applyKnockOut(event.getPlayer());
    }

    @EventHandler
    public void onGlide(EntityToggleGlideEvent event) {
        System.out.println("cancelled");
        event.setCancelled(true);
    }
}