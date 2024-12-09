package com.kzics.quirksmha.abilities;

import com.kzics.quirksmha.Main;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class BarrierAbility implements QuirkAbility {

    private boolean barrierActive = false;
    private double baseRadius = 2.5; // Rayon initial de la barrière
    private int baseCooldown = 10;  // Cooldown initial en secondes

    @Override
    public void activate(Player player) {
        if (!barrierActive) {
            createBarrier(player);
            barrierActive = true;
        }
    }

    @Override
    public void deactivate(Player player) {
        // Rien à faire ici pour la désactivation, elle est gérée par le timer
    }

    @Override
    public void adjustAttributes(int quirkLevel) {
        baseRadius = 2.5 + (quirkLevel - 1) * 0.5; // Augmenter le rayon avec le niveau
        baseCooldown = Math.max(1, 10 - (quirkLevel - 1) / 2); // Réduire le cooldown avec le niveau
    }

    // Création de la barrière
    private void createBarrier(Player player) {
        Location loc = player.getLocation().add(player.getLocation().getDirection().multiply(2));
        double radius = baseRadius;

        Vector forward = player.getLocation().getDirection().setY(0).normalize();
        Vector right = forward.clone().crossProduct(new Vector(0, 1, 0)).normalize();

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= 20) { // 20 ticks (1 seconde) x 3 = 3 secondes
                    createLaser(player, loc);
                    barrierActive = false;
                    this.cancel();
                    return;
                }

                // Dessiner la barrière
                for (double x = -radius; x <= radius; x += 0.2) {
                    for (double y = -radius; y <= radius; y += 0.2) {
                        if (x * x + y * y <= radius * radius) {
                            Location particleLoc = loc.clone()
                                    .add(right.clone().multiply(x))
                                    .add(0, y, 0);
                            player.getWorld().spawnParticle(Particle.DUST, particleLoc, 1,
                                    new Particle.DustOptions(Color.YELLOW, 1.2f));
                        }
                    }
                }

                ticks++;
            }
        }.runTaskTimer(Main.getInstance(), 0, 5); // Répéter toutes les 5 ticks

        player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1.0f, 1.0f);
        player.sendMessage("Barrière activée !");
    }

    // Création du laser
    private void createLaser(Player player, Location startLoc) {
        player.sendMessage("Laser activé !");
        player.getWorld().playSound(startLoc, Sound.ENTITY_ENDER_DRAGON_SHOOT, 1.0f, 1.0f);

        Vector direction = player.getLocation().getDirection();
        double maxDistance = 25.0;

        for (double i = 0; i <= maxDistance; i += 0.2) {
            Location laserLoc = startLoc.clone().add(direction.clone().multiply(i));

            for (double x = -1; x <= 1; x += 0.2) {
                for (double y = -1; y <= 1; y += 0.2) {
                    Location particleLoc = laserLoc.clone().add(x, y, 0);
                    player.getWorld().spawnParticle(Particle.DUST, particleLoc, 1,
                            new Particle.DustOptions(Color.WHITE, 1.5f));
                }
            }
        }
    }
}
