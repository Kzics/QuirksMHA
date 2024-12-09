package com.kzics.quirksmha.abilities;

import com.kzics.quirksmha.Main;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class AirCannonAbility implements QuirkAbility {

    private double damage;
    private double cooldown;
    private double range;

    public AirCannonAbility() {
        this.damage = 4.0; // 2 coeurs (4 PV)
        this.cooldown = 15.0; // Cooldown de base en secondes
        this.range = 30.0; // Portée maximale
    }

    @Override
    public void activate(Player player) {
        fireAirCannon(player);
    }

    @Override
    public void deactivate(Player player) {
    }

    @Override
    public void adjustAttributes(int quirkLevel) {
        this.damage = 4.0 + (quirkLevel - 1) * 0.2;
        this.cooldown = Math.max(5.0, 15.0 - (quirkLevel - 1) * 0.5);
        this.range = 30.0 + (quirkLevel - 1) * 2.0;
    }

    private void fireAirCannon(Player player) {
        player.sendMessage("Air Cannon activé !");
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_SHOOT, 1.0f, 1.2f);

        Location startLoc = player.getEyeLocation();
        Vector direction = player.getLocation().getDirection().normalize();
        double maxDistance = range;

        new BukkitRunnable() {
            double distance = 0;

            @Override
            public void run() {
                if (distance >= maxDistance) {
                    this.cancel();
                    return;
                }

                Location currentPoint = startLoc.clone().add(direction.clone().multiply(distance));

                // Générer une sphère d'air
                generateFilledSphere(currentPoint);

                // Appliquer les effets sur les entités touchées
                applyEffects(currentPoint, player);

                distance += 2.0; // Avance rapide du rayon
            }
        }.runTaskTimer(Main.getInstance(), 0, 1); // Tick toutes les 1 tick pour un effet fluide
    }

    private void generateFilledSphere(Location loc) {
        double radius = 2.5; // Rayon de la sphère
        int particleDensity = 100; // Nombre de particules

        for (int i = 0; i < particleDensity; i++) {
            double x = (Math.random() * 2 - 1) * radius;
            double y = (Math.random() * 2 - 1) * radius;
            double z = (Math.random() * 2 - 1) * radius;

            if (x * x + y * y + z * z <= radius * radius) { // Vérifie si le point est dans la sphère
                Location particleLoc = loc.clone().add(x, y, z);
                loc.getWorld().spawnParticle(Particle.DUST, particleLoc, 1,
                        new Particle.DustOptions(Color.WHITE, 1.0f));
            }
        }
    }

    private void applyEffects(Location loc, Player caster) {
        double impactRadius = 3.0; // Rayon de l'impact

        for (Entity entity : loc.getWorld().getNearbyEntities(loc, impactRadius, impactRadius, impactRadius)) {
            if (entity instanceof LivingEntity target && !target.equals(caster)) {
                // Appliquer les dégâts
                target.damage(damage);

                // Appliquer le knockback
                Vector knockback = target.getLocation().toVector()
                        .subtract(caster.getLocation().toVector())
                        .normalize()
                        .multiply(1.5);
                target.setVelocity(knockback);

                // Jouer un son d'impact
                loc.getWorld().playSound(loc, Sound.ENTITY_BLAZE_HURT, 1.0f, 1.0f);
            }
        }
    }
}
