package com.kzics.quirksmha.abilities;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.RayTraceResult;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpaceTimeAbility extends QuirkAbility {

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private double range;
    private long cooldown;

    public SpaceTimeAbility() {
        this.range = 50.0; // Default range in meters
        this.cooldown = 30 * 1000L; // Default cooldown in milliseconds
    }

    @Override
    public String name() {
        return "Spacetime";
    }

    @Override
    public void activate(Player player) {
        if (isOnCooldown(player)) {
            player.sendMessage("§cSpacetime est en cooldown !");
            return;
        }

        RayTraceResult result = player.getWorld().rayTraceEntities(
                player.getEyeLocation(),
                player.getLocation().getDirection(),
                range,
                entity -> entity instanceof Player && !entity.equals(player)
        );

        if (result != null && result.getHitEntity() instanceof Player target) {
            swapPlaces(player, target);
        } else {
            teleportWhereLooking(player);
        }

        startCooldown(player);
    }

    @Override
    public void deactivate(Player player) {
        // Rien à désactiver
    }

    @Override
    public void adjustAttributes(int quirkLevel) {
        this.range = 50.0 + (quirkLevel - 1) * 3.0; // Augmente la portée avec le niveau
        this.cooldown = Math.max(5 * 1000L, 30 * 1000L - (quirkLevel - 1) * 500L); // Réduit le cooldown avec le niveau
    }

    private void teleportWhereLooking(Player player) {
        Location targetLocation = player.getTargetBlockExact((int) range) != null
                ? player.getTargetBlockExact((int) range).getLocation().add(0.5, 1, 0.5)
                : player.getEyeLocation().add(player.getLocation().getDirection().multiply(range));

        if (targetLocation.getBlock().getType().isSolid()) {
            player.sendMessage("§cVous ne pouvez pas vous téléporter dans un bloc solide !");
            return;
        }

        player.teleport(targetLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
        player.playSound(targetLocation, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        player.getWorld().spawnParticle(Particle.PORTAL, targetLocation, 30, 0.5, 0.5, 0.5);
        player.sendMessage("§aVous vous êtes téléporté !");
    }

    private void swapPlaces(Player player, Player target) {
        Location playerLocation = player.getLocation();
        Location targetLocation = target.getLocation();

        player.teleport(targetLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
        target.teleport(playerLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);

        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        target.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);

        player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 30, 0.5, 0.5, 0.5);
        target.getWorld().spawnParticle(Particle.PORTAL, target.getLocation(), 30, 0.5, 0.5, 0.5);

        player.sendMessage("§aVous avez échangé votre position avec §e" + target.getName() + "§a !");
        target.sendMessage("§e" + player.getName() + "§a a échangé sa position avec vous !");
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
