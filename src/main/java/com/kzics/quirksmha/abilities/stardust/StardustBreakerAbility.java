package com.kzics.quirksmha.abilities.stardust;

import com.kzics.quirksmha.Main;
import com.kzics.quirksmha.abilities.QuirkAbility;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class StardustBreakerAbility extends QuirkAbility {

    private double range;
    private long cooldown;
    private double breakerRadius;

    public StardustBreakerAbility() {
        this.range = 30.0;
        this.cooldown = 20 * 1000L;
        this.breakerRadius = 3.0;
    }

    @Override
    public String name() {
        return "Stardust Breaker";
    }

    @Override
    public void activate(Player player) {
        player.sendMessage("§bStardust Breaker activé !");
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_SHOOT, 1.0f, 1.0f);
        shootOrb(player);
    }

    @Override
    public void deactivate(Player player) {
        // Pas d'action à la désactivation
    }

    @Override
    public void adjustAttributes(int quirkLevel) {
        this.range = 30.0 + (quirkLevel - 1) * 3.0;
        this.cooldown = Math.max(10 * 1000L, 20 * 1000L - (quirkLevel - 1) * 1000L);
        this.breakerRadius = 3.0 + (quirkLevel - 1) * 0.5;
    }

    private void shootOrb(Player player) {
        Location startLocation = player.getEyeLocation();
        Vector direction = player.getLocation().getDirection().normalize();

        new BukkitRunnable() {
            double traveledDistance = 0;

            @Override
            public void run() {
                if (traveledDistance >= range) {
                    explodeOrb(startLocation.clone().add(direction.clone().multiply(traveledDistance)), null, player);
                    cancel();
                    return;
                }

                Location currentLocation = startLocation.clone().add(direction.clone().multiply(traveledDistance));

                for (Entity entity : player.getWorld().getNearbyEntities(currentLocation, breakerRadius, breakerRadius, breakerRadius)) {
                    if (entity instanceof LivingEntity target && !target.equals(player)) {
                        explodeOrb(currentLocation, target, player);
                        cancel();
                        return;
                    }
                }

                if (currentLocation.getBlock().getType().isSolid()) {
                    explodeOrb(currentLocation, null, player);
                    cancel();
                    return;
                }

                spawnOrbParticles(currentLocation);
                traveledDistance += 0.5;
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }

    private void spawnOrbParticles(Location location) {
        for (double theta = 0; theta < Math.PI * 2; theta += Math.PI / 16) {
            for (double phi = 0; phi < Math.PI; phi += Math.PI / 16) {
                double x = breakerRadius * Math.sin(phi) * Math.cos(theta);
                double y = breakerRadius * Math.cos(phi);
                double z = breakerRadius * Math.sin(phi) * Math.sin(theta);

                Location particleLocation = location.clone().add(x, y, z);
                location.getWorld().spawnParticle(
                        Particle.DUST,
                        particleLocation,
                        1,
                        new Particle.DustOptions(Color.BLUE, 1.0f)
                );
            }
        }
    }

    private void explodeOrb(Location location, LivingEntity target, Player caster) {
        location.getWorld().spawnParticle(Particle.EXPLOSION, location, 1);
        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);

        if (target != null) {
            target.damage(5.0, caster); // Appliquer des dégâts faibles
            caster.sendMessage("§a" + target.getName() + " marqué par Stardust Breaker !");
        }
    }
}
