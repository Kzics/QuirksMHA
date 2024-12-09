package com.kzics.quirksmha.abilities;

import com.kzics.quirksmha.Main;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class ImpureBeamAbility implements QuirkAbility {

    private double baseChargeTime = 5.0; // Temps de chargement en secondes
    private double fullDamage = 14.0; // Dégâts du rayon complètement chargé (7 cœurs)
    private double reducedDamage = 6.0; // Dégâts anticipés (3 cœurs)
    private boolean isCharging = false;
    private boolean canPreemptiveFire = false;

    @Override
    public void adjustAttributes(int quirkLevel) {
        this.baseChargeTime = Math.max(2.0, 5.0 - (quirkLevel - 1) * 0.1); // Réduction du temps de chargement par niveau
        this.fullDamage = 14.0 + (quirkLevel - 1) * 1.0; // Augmentation des dégâts par niveau

    }

    private void chargeBeam(Player player) {
        isCharging = true;
        canPreemptiveFire = false;

        player.sendMessage("Chargement du rayon en cours...");
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 0.5f);

        new BukkitRunnable() {
            int ticks = 0;
            int chargeTicks = (int) (baseChargeTime * 20); // Temps de chargement en ticks

            @Override
            public void run() {
                if (ticks >= chargeTicks) {
                    fireBeam(player, fullDamage);
                    isCharging = false;
                    this.cancel();
                } else if (ticks >= 40) { // Après 2 secondes, permettre la frappe anticipée
                    canPreemptiveFire = true;
                }

                // Afficher des particules de chargement jaunes
                Location loc = player.getEyeLocation().add(player.getLocation().getDirection().multiply(2));
                player.getWorld().spawnParticle(Particle.DUST, loc, 10,
                        new Particle.DustOptions(Color.YELLOW, 1.0f));

                ticks += 5; // Exécution toutes les 5 ticks
            }
        }.runTaskTimer(Main.getInstance(), 0, 5); // Toutes les 5 ticks
    }

    public void preemptiveFire(Player player) {
        if (!isCharging || !canPreemptiveFire) {
            player.sendMessage("Vous ne pouvez pas tirer maintenant !");
            return;
        }
        fireBeam(player, reducedDamage);
        isCharging = false;
    }

    private void fireBeam(Player player, double damage) {
        player.sendMessage("Rayon tiré avec " + damage + " dégâts !");
        player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);

        Location startLoc = player.getEyeLocation();
        Vector direction = player.getLocation().getDirection().normalize();
        Random random = new Random();

        Location currentPoint = startLoc.clone();
        double maxDistance = 25.0; // Portée maximale du rayon

        // Générer un chemin irrégulier pour le rayon
        for (double i = 0; i <= maxDistance; i += 0.5) {
            // Ajout de décalages aléatoires pour l'effet "jagged"
            double offsetX = (random.nextDouble() - 0.5) * 0.5; // Décalage horizontal aléatoire
            double offsetY = (random.nextDouble() - 0.5) * 0.5; // Décalage vertical aléatoire
            double offsetZ = (random.nextDouble() - 0.5) * 0.5; // Décalage en profondeur aléatoire

            Location nextPoint = currentPoint.clone().add(direction.clone().multiply(0.5))
                    .add(offsetX, offsetY, offsetZ);

            // Connecter chaque point pour un rendu continu
            drawLine(player, currentPoint, nextPoint);

            // Définir le point actuel comme le prochain pour la prochaine itération
            currentPoint = nextPoint.clone();
        }
    }

    private void drawLine(Player player, Location start, Location end) {
        Vector difference = end.toVector().subtract(start.toVector());
        double distance = difference.length();
        Vector step = difference.normalize().multiply(0.1); // Pas pour la ligne

        Location current = start.clone();
        for (double i = 0; i < distance; i += 0.1) {
            player.getWorld().spawnParticle(Particle.DUST, current, 1,
                    new Particle.DustOptions(Color.YELLOW, 1.2f)); // Particules jaunes lumineuses
            current.add(step);
        }
    }

    @Override
    public void activate(Player player) {
        if (isCharging) {
            player.sendMessage("Déjà en cours de chargement !");
            return;
        }
        chargeBeam(player);
    }

    @Override
    public void deactivate(Player player) {
        // Non utilisé ici
    }
}
