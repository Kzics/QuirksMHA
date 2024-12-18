package com.kzics.quirksmha.abilities;

import com.kzics.quirksmha.Main;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class BulletLaserAbility extends QuirkAbility {

    private double cooldown = 10.0;

    @Override
    public void activate(Player player) {
        shootLasers(player);
    }

    @Override
    public void deactivate(Player player) {
        player.sendMessage("Bullet Laser désactivé !");
    }

    @Override
    public void adjustAttributes(int quirkLevel) {
        cooldown = Math.max(1.0, 10.0 - (quirkLevel - 1) * 0.2);
    }

    private void shootLasers(Player player) {
        player.sendMessage("Bullet Laser activé !");
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_SHOOT, 1.0f, 1.5f);

        int maxLasers = 5;
        for (int i = 0; i < maxLasers; i++) {
            final int delay = i * 20;
            Main.getInstance().getServer().getScheduler().runTaskLater(Main.getInstance(), () -> {
                shootSingleLaser(player);
            }, delay);
        }
    }

    private void shootSingleLaser(Player player) {
        Location startLoc = player.getEyeLocation();
        Vector direction = startLoc.getDirection();
        double maxDistance = 25.0;

        for (double i = 0; i <= maxDistance; i += 0.2) {
            Location currentPoint = startLoc.clone().add(direction.clone().multiply(i));
            player.getWorld().spawnParticle(Particle.DUST, currentPoint, 1,
                    new Particle.DustOptions(Color.PURPLE, 0.8f));
        }
    }

    @Override
    public String name() {
        return "Bullet Laser";
    }
}
