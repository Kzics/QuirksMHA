package com.kzics.quirksmha.abilities.stardust;

import com.kzics.quirksmha.abilities.QuirkAbility;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Random;

public class StardustFallAbility extends QuirkAbility {

    private double range;
    private long cooldown;

    public StardustFallAbility() {
        this.range = 20.0;
        this.cooldown = 15 * 1000L;
    }

    @Override
    public String name() {
        return "Stardust Fall";
    }

    @Override
    public void activate(Player player) {
        generateLasers(player);
        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.0f, 1.0f);
        player.sendMessage("§bStardust Fall activé !");
    }

    @Override
    public void deactivate(Player player) {
        // Rien à désactiver
    }

    @Override
    public void adjustAttributes(int quirkLevel) {
        this.range = 20.0 + (quirkLevel - 1) * 2.0;
        this.cooldown = Math.max(5 * 1000L, 15 * 1000L - (quirkLevel - 1) * 1000L);
    }

    private void generateLasers(Player player) {
        Random random = new Random();
        Location playerLocation = player.getEyeLocation();

        for (int i = 0; i < 15; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 2;
            double offsetY = (random.nextDouble() - 0.5) * 1;
            double offsetZ = (random.nextDouble() - 0.5) * 2;

            Location startLocation = playerLocation.clone().add(offsetX, offsetY, offsetZ);
            Vector targetDirection = player.getLocation().getDirection().clone().add(new Vector(
                    (random.nextDouble() - 0.5) * 0.2,
                    (random.nextDouble() - 0.5) * 0.2,
                    (random.nextDouble() - 0.5) * 0.2
            )).normalize();

            Location endLocation = startLocation.clone().add(targetDirection.multiply(range));
            createInstantLaser(player, startLocation, endLocation);
        }
    }

    private void createInstantLaser(Player player, Location start, Location end) {
        Vector direction = end.toVector().subtract(start.toVector()).normalize();
        double distance = start.distance(end);

        for (double i = 0; i <= distance; i += 0.5) {
            Location currentLocation = start.clone().add(direction.clone().multiply(i));
            player.getWorld().spawnParticle(
                    Particle.DUST,
                    currentLocation,
                    5,
                    0.1,
                    0.1,
                    0.1,
                    new Particle.DustOptions(Color.BLUE, 1.0f)
            );
        }
    }
}
