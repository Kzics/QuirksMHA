package com.kzics.quirksmha.abilities;

import com.kzics.quirksmha.Main;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SixEyesAbility extends QuirkAbility {

    private double maxTime;
    private double cooldownReduction;
    private double sideEffectDuration;
    private boolean isActive = false;
    private final Set<UUID> glowingPlayers = new HashSet<>();
    private final QuirkManager quirkManager;

    public SixEyesAbility(QuirkManager quirkManager) {
        this.quirkManager = quirkManager;
        this.maxTime = 100.0;
        this.cooldownReduction = 55.0;
        this.sideEffectDuration = 30.0;
    }

    @Override
    public void activate(Player player) {
        if (isActive) {
            player.sendMessage("§cSix Eyes est déjà actif !");
            return;
        }

        isActive = true;
        player.sendMessage("§aSix Eyes activé !");
        applyEffects(player);

        new BukkitRunnable() {
            int elapsedTicks = 0;

            @Override
            public void run() {
                if (elapsedTicks >= maxTime * 20 || !isActive) {
                    deactivate(player);
                    this.cancel();
                    return;
                }

                applyNearbyGlow(player);
                elapsedTicks += 20;
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

    @Override
    public void deactivate(Player player) {
        if (!isActive) return;

        isActive = false;
        resetGlowEffects();
        removeEffects(player);

        player.sendMessage("§cSix Eyes désactivé !");
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_DEATH, 1.0f, 1.0f);

        new BukkitRunnable() {
            int elapsedTicks = 0;

            @Override
            public void run() {
                if (elapsedTicks >= sideEffectDuration * 20) {
                    player.sendMessage("§aLes effets secondaires de Six Eyes sont terminés !");
                    this.cancel();
                    return;
                }

                player.getWorld().spawnParticle(Particle.DUST, player.getLocation(), 10,
                        new Particle.DustOptions(Color.RED, 1.0f));
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_HURT, 0.5f, 0.8f);

                elapsedTicks += 20;
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

    @Override
    public void adjustAttributes(int quirkLevel) {
        this.maxTime = 100.0 + (quirkLevel - 1) * 30.0; // Augmentation du temps max par niveau
        this.cooldownReduction = 55.0 + (quirkLevel - 1); // Augmentation de la réduction de cooldown
        this.sideEffectDuration = Math.max(0, 30.0 - (quirkLevel - 1) * 2.0); // Réduction de l'effet secondaire
    }

    private void applyEffects(Player player) {
        player.setFoodLevel(20);
        player.setSaturation(10.0f);

        player.getWorld().playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1.0f, 1.5f);
        player.getWorld().spawnParticle(Particle.DUST, player.getLocation(), 30,
                new Particle.DustOptions(Color.AQUA, 1.5f));
    }

    private void removeEffects(Player player) {
        player.setFoodLevel(20);
        player.setSaturation(5.0f);
    }

    private void applyNearbyGlow(Player player) {
        player.getWorld().getNearbyEntities(player.getLocation(), 10, 10, 10).forEach(entity -> {
            if (entity instanceof Player nearbyPlayer && !nearbyPlayer.equals(player)) {
                nearbyPlayer.setGlowing(true);
                glowingPlayers.add(nearbyPlayer.getUniqueId());
            }
        });
    }

    private void resetGlowEffects() {
        for (UUID uuid : glowingPlayers) {
            Player glowingPlayer = Bukkit.getPlayer(uuid);
            if (glowingPlayer != null) {
                glowingPlayer.setGlowing(false);
            }
        }
        glowingPlayers.clear();
    }

    @Override
    public void onInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof LivingEntity target)) return;
        Player player = event.getPlayer();
        if (!isActive) return;
        if (quirkManager.hasQuirk(target.getUniqueId())) {
            Quirk targetQuirk = quirkManager.getQuirk(target.getUniqueId());
            player.sendMessage(Component.text("Player has Quirk: " + targetQuirk.name()));
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.5f);
        } else {
            player.sendMessage("§cPlayer dont have any Quirk !");
        }
    }
}
