package com.kzics.quirksmha.abilities;

import com.kzics.quirksmha.Main;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class BlackWhipAbility implements QuirkAbility {

    private double rangeBlock;
    private double rangeGrab;
    private double cooldown;

    public BlackWhipAbility() {
        this.rangeBlock = 60.0; // Portée de base pour toucher un bloc
        this.rangeGrab = 25.0; // Portée de base pour attraper une entité
        this.cooldown = 1.0; // Cooldown de base
    }

    @Override
    public void activate(Player player) {
        fireWhip(player);
    }

    @Override
    public void deactivate(Player player) {
        // Pas de logique spécifique de désactivation ici
    }

    @Override
    public void adjustAttributes(int quirkLevel) {
        this.rangeBlock = 60.0 + (quirkLevel - 1) * 2.3; // Augmente la portée pour les blocs
        this.rangeGrab = 25.0 + (quirkLevel - 1) * 0.3; // Augmente la portée pour les entités
        this.cooldown = Math.max(1.0, 1.0 - (quirkLevel - 1) * 0.1); // Réduit le cooldown
    }

    private void fireWhip(Player player) {
        player.sendMessage("Black Whip activé !");
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_SHOOT, 1.0f, 0.5f);

        Location startLoc = player.getLocation().add(0, 0.5, 0);
        Vector direction = player.getLocation().getDirection().normalize();
        double maxDistance = rangeBlock;

        RayTraceResult rayTrace = player.getWorld().rayTrace(
                startLoc, direction, maxDistance, FluidCollisionMode.NEVER, true, 0.5, entity -> entity != player);

        double endDistance = rayTrace != null
                ? rayTrace.getHitPosition().distance(startLoc.toVector())
                : maxDistance;

        generateParticles(player, startLoc, direction, endDistance);

        // Gestion des entités ou blocs touchés
        if (rayTrace != null) {
            if (rayTrace.getHitEntity() != null) {
                Entity hitEntity = rayTrace.getHitEntity();
                grabEntity(player, hitEntity);
            } else if (rayTrace.getHitBlock() != null) {
                applyDynamicVelocity(player, rayTrace.getHitPosition());
            }
        }
    }

    private void generateParticles(Player player, Location startLoc, Vector direction, double endDistance) {
        double offset = 0.2; // Distance entre les bandes vertes
        int particleCount = 8; // Nombre de particules par position
        Vector perpendicular = direction.clone().crossProduct(new Vector(0, 1, 0)).normalize();

        for (double i = 0; i <= endDistance; i += 0.2) {
            Location currentPoint = startLoc.clone().add(direction.clone().multiply(i));

            // Particule noire (centre)
            player.getWorld().spawnParticle(Particle.DUST, currentPoint, particleCount,
                    new Particle.DustOptions(Color.BLACK, 0.8f));

            // Particules vertes (gauche et droite)
            Location left = currentPoint.clone().add(perpendicular.clone().multiply(-offset));
            player.getWorld().spawnParticle(Particle.DUST, left, particleCount,
                    new Particle.DustOptions(Color.fromRGB(73, 122, 104), 0.8f));

            Location right = currentPoint.clone().add(perpendicular.clone().multiply(offset));
            player.getWorld().spawnParticle(Particle.DUST, right, particleCount,
                    new Particle.DustOptions(Color.fromRGB(73, 122, 104), 0.8f));
        }
    }

    private void grabEntity(Player player, Entity entity) {
        Vector pullDirection = player.getLocation().toVector().subtract(entity.getLocation().toVector()).normalize();
        entity.setVelocity(pullDirection.multiply(1.5)); // Ramener l'entité vers le joueur
        player.sendMessage("Une entité a été attrapée !");
    }

    private void applyDynamicVelocity(Player player, Vector hitPosition) {
        Vector playerPosition = player.getLocation().toVector();
        Vector direction = hitPosition.clone().subtract(playerPosition).normalize();
        double baseSpeed = 0.8;
        double drag = 0.1;

        player.sendMessage("Vous êtes en mouvement grâce au Black Whip !");
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);

        new BukkitRunnable() {
            double distance = playerPosition.distance(hitPosition);
            double speed = baseSpeed;

            @Override
            public void run() {
                distance = player.getLocation().toVector().distance(hitPosition);

                if (distance <= 1.0) {
                    player.setVelocity(new Vector(0, 0, 0)); // Stopper le mouvement
                    this.cancel();
                    player.sendMessage("Black Whip terminé !");
                    return;
                }

                Vector velocity = direction.clone().multiply(speed);
                player.setVelocity(player.getVelocity().add(velocity));

                speed = Math.max(0, speed - drag);
            }
        }.runTaskTimer(Main.getInstance(), 0, 2); // Tick toutes les 2 ticks
    }
}
