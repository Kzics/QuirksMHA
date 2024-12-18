package com.kzics.quirksmha.abilities;

import com.kzics.quirksmha.Main;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SealingAbility extends QuirkAbility {

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private long cooldown;
    private double duration;

    public SealingAbility() {
        this.cooldown = 45 * 1000L; // Cooldown de base en millisecondes
        this.duration = 10.0; // Durée de base de l'immobilisation
    }

    @Override
    public String name() {
        return "Sealing";
    }

    @Override
    public void activate(Player player) {
        if (isOnCooldown(player)) {
            player.sendMessage("§cSealing est en cooldown !");
            return;
        }

        Entity target = getLookedAtPlayer(player);
        if (!(target instanceof Player victim)) {
            player.sendMessage("§cVous ne regardez aucun joueur !");
            return;
        }

        applySealing(player, victim);
        startCooldown(player);
    }

    @Override
    public void deactivate(Player player) {
        // Rien à désactiver
    }

    @Override
    public void adjustAttributes(int quirkLevel) {
        this.cooldown = Math.max(10 * 1000L, 45 * 1000L - (quirkLevel - 1) * 1000L); // Réduction du cooldown
        this.duration = 10.0 + (quirkLevel - 1); // Augmentation de la durée
    }

    private void applySealing(Player player, Player victim) {
        player.sendMessage("§aSealing activé sur " + victim.getName() + " !");
        victim.sendMessage("§cVous êtes scellé par " + player.getName() + " !");
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_EYE_DEATH, 1.0f, 1.0f);

        Vector originalVelocityPlayer = player.getVelocity();
        Vector originalVelocityVictim = victim.getVelocity();

        new BukkitRunnable() {
            double elapsed = 0;

            @Override
            public void run() {
                if (elapsed >= duration * 20 || player.isDead() || victim.isDead() || player.isSneaking()) {
                    resetMovement(player, originalVelocityPlayer);
                    resetMovement(victim, originalVelocityVictim);
                    cancel();
                    return;
                }

                if (player.getHealth() <= 0 || victim.getHealth() <= 0) {
                    resetMovement(player, originalVelocityPlayer);
                    resetMovement(victim, originalVelocityVictim);
                    cancel();
                    return;
                }

                player.setVelocity(new Vector(0, 0, 0));
                victim.setVelocity(new Vector(0, 0, 0));

                showParticleRing(victim.getLocation());
                elapsed += 1;
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }

    private void resetMovement(Player player, Vector originalVelocity) {
        player.setVelocity(originalVelocity);
        player.sendMessage("§cVous pouvez à nouveau bouger !");
    }

    private void showParticleRing(Location location) {
        int points = 30;
        double radius = 1.5;

        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;

            location.getWorld().spawnParticle(Particle.DUST, location.clone().add(x, 1.0, z), 1,
                    new Particle.DustOptions(Color.BLUE, 1.0f));
        }
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

    private Player getLookedAtPlayer(Player player) {
        return player.getWorld().rayTraceEntities(
                player.getEyeLocation(),
                player.getLocation().getDirection(),
                10,
                entity -> entity instanceof Player && !entity.equals(player)
        ) != null ? (Player) player.getWorld().rayTraceEntities(
                player.getEyeLocation(),
                player.getLocation().getDirection(),
                10,
                entity -> entity instanceof Player && !entity.equals(player)
        ).getHitEntity() : null;
    }
}
