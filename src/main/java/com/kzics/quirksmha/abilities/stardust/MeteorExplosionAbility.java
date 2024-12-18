package com.kzics.quirksmha.abilities.stardust;

import com.kzics.quirksmha.Main;
import com.kzics.quirksmha.abilities.QuirkAbility;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MeteorExplosionAbility extends QuirkAbility {

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private double maxRange;
    private double cooldown;
    private double radius;
    private int particleDensity;

    public MeteorExplosionAbility() {
        this.maxRange = 100.0;
        this.cooldown = 15 * 1000L;
        this.radius = 3.0;
        this.particleDensity = 100; // Nombre de particules dans le cylindre
    }

    @Override
    public String name() {
        return "Meteor Explosion";
    }

    @Override
    public void activate(Player player) {
        if (isOnCooldown(player)) {
            player.sendMessage("§cMeteor Explosion est en cooldown !");
            return;
        }

        startSneaking(player);
    }

    @Override
    public void deactivate(Player player) {
        // Si nécessaire, implémenter une logique d'annulation
    }

    @Override
    public void adjustAttributes(int quirkLevel) {
        this.maxRange = 100.0 + (quirkLevel - 1) * 5.0;
        this.cooldown = Math.max(5 * 1000L, 15 * 1000L - (quirkLevel - 1) * 1000L);
        this.radius = 5.0 + (quirkLevel - 1) * 0.5;
        this.particleDensity = 100 + (quirkLevel - 1) * 10; // Augmente le nombre de particules avec le niveau
    }

    private void startSneaking(Player player) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (!player.isSneaking()) {
                    player.sendMessage("§cVous avez arrêté de sneaky !");
                    cancel();
                    return;
                }

                if (ticks >= 8 * 20) {
                    summonLaser(player);
                    startCooldown(player);
                    cancel();
                    return;
                }

                player.getWorld().spawnParticle(
                        Particle.DUST,
                        player.getLocation().add(0, 1, 0),
                        10,
                        0.5,
                        0.5,
                        0.5,
                        new Particle.DustOptions(Color.RED, 1.5f)
                );

                ticks += 1;
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }

    private void summonLaser(Player player) {
        Location targetLocation = getTargetLocation(player);

        player.sendMessage("§aMeteor Explosion activé !");
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 0.5f);

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= 6 * 20) {
                    player.getWorld().playSound(targetLocation, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.5f);
                    createExplosionEffect(targetLocation);
                    cancel();
                    return;
                }

                createLaserEffect(targetLocation);
                ticks += 2;
            }
        }.runTaskTimer(Main.getInstance(), 0, 2);
    }

    private Location getTargetLocation(Player player) {
        RayTraceResult result = player.getWorld().rayTraceBlocks(player.getEyeLocation(), player.getLocation().getDirection(), maxRange);
        if (result != null) {
            return result.getHitPosition().toLocation(player.getWorld());
        }

        Vector endPoint = player.getEyeLocation().toVector().add(player.getLocation().getDirection().multiply(maxRange));
        return endPoint.toLocation(player.getWorld());
    }

    private void createLaserEffect(Location target) {
        double height = 15; // Hauteur du cylindre
        double step = 0.5; // Distance entre chaque tranche verticale
        int angleStep = 10; // Résolution angulaire (en degrés)

        for (double y = 0; y <= height; y += step) {
            for (int angle = 0; angle < 360; angle += angleStep) {
                double radians = Math.toRadians(angle);
                double x = radius * Math.cos(radians);
                double z = radius * Math.sin(radians);

                Location particleLocation = target.clone().add(x, y, z);
                target.getWorld().spawnParticle(
                        Particle.DUST,
                        particleLocation,
                        1,
                        new Particle.DustOptions(Color.BLUE, 2.0f)
                );
            }
        }
    }

    private void createExplosionEffect(Location target) {
        target.getWorld().spawnParticle(
                Particle.EXPLOSION,
                target,
                1
        );
    }

    private boolean isOnCooldown(Player player) {
        UUID playerId = player.getUniqueId();
        if (!cooldowns.containsKey(playerId)) return false;

        long lastUseTime = cooldowns.get(playerId);
        return (System.currentTimeMillis() - lastUseTime) < cooldown;
    }

    private void startCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }
}
