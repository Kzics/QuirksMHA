package com.kzics.quirksmha.abilities;

import com.kzics.quirksmha.Main;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DarkBallAbility implements QuirkAbility {

    private double explosionRadius = 3.0; // Rayon de l'explosion
    private int expandTime = 80; // Temps en ticks pour l'expansion (4 secondes)



    private void createDarkBall(Player player) {
        Location startLoc = player.getEyeLocation().add(player.getLocation().getDirection().multiply(5));
        World world = player.getWorld();

        player.sendMessage("Dark Ball activée !");
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0f, 0.5f);

        new BukkitRunnable() {
            double radius = 0.5; // Taille initiale
            final double maxRadius = 8.0; // Taille maximale atteinte
            int ticks = 0;
            final int expandTime = 60; // Temps total pour l'expansion (3 secondes)

            @Override
            public void run() {
                if (ticks >= expandTime) {
                    explode(player, startLoc, radius);
                    this.cancel();
                    return;
                }

                // Générer des particules noires pour la sphère
                for (double phi = 0; phi < Math.PI * 2; phi += Math.PI / 10) { // Plus dense horizontalement
                    for (double theta = 0; theta < Math.PI; theta += Math.PI / 10) { // Plus dense verticalement
                        double x = radius * Math.sin(theta) * Math.cos(phi);
                        double y = radius * Math.sin(theta) * Math.sin(phi);
                        double z = radius * Math.cos(theta);

                        Location particleLoc = startLoc.clone().add(x, y, z);
                        world.spawnParticle(Particle.DUST, particleLoc, 1,
                                new Particle.DustOptions(Color.BLACK, 1.2f)); // Taille des particules ajustée
                    }
                }

                radius += (maxRadius - radius) / expandTime * 5; // Expansion plus rapide
                ticks += 5; // Intervalle
            }
        }.runTaskTimer(Main.getInstance(), 0, 5); // Toutes les 5 ticks
    }

    private void explode(Player player, Location loc, double radius) {
        player.sendMessage("La Dark Ball explose !");
        player.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);

        loc.getWorld().createExplosion(loc, (float) radius, false, true);
    }

    @Override
    public void activate(Player player) {
        createDarkBall(player);
    }

    @Override
    public void deactivate(Player player) {
        // Non utilisé ici
    }

    @Override
    public void adjustAttributes(int quirkLevel) {
        this.explosionRadius = 3.0 + (quirkLevel - 1) * 0.5; // Augmente le ray
        this.expandTime = 80 - (quirkLevel - 1) * 5; // Diminue le temps d'expansion
    }
}
