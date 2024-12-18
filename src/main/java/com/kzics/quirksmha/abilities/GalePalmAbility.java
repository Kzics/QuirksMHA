package com.kzics.quirksmha.abilities;

import com.kzics.quirksmha.Main;
import com.kzics.quirksmha.manager.ManagerHandler;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GalePalmAbility extends QuirkAbility {

    private final ManagerHandler managerHandler;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private long cooldown;
    private int levelDifferenceThreshold;

    public GalePalmAbility(ManagerHandler managerHandler) {
        this.managerHandler = managerHandler;
        this.cooldown = 50 * 1000L;
        this.levelDifferenceThreshold = 10;
    }

    @Override
    public String name() {
        return "Gale Palm";
    }

    @Override
    public void activate(Player player) {
        if (isOnCooldown(player)) {
            player.sendMessage("§cGale Palm est en cooldown !");
            return;
        }

        Entity target = getLookedAtEntity(player);
        if (!(target instanceof LivingEntity victim)) {
            player.sendMessage("§cAucune cible trouvée !");
            return;
        }

        int playerLevel = managerHandler.playerDataManager().getPlayerData(player.getUniqueId()).level();
        int victimLevel = managerHandler.playerDataManager().getPlayerData(victim.getUniqueId()).level();

        if (playerLevel >= victimLevel + levelDifferenceThreshold) {
            knockOut(player, victim);
        } else {
            knockBack(player, victim);
        }

        startCooldown(player);
    }

    @Override
    public void deactivate(Player player) {
    }

    @Override
    public void adjustAttributes(int quirkLevel) {
        this.cooldown = Math.max(10 * 1000L, 50 * 1000L - (quirkLevel - 1) * 1000L); // Reduce cooldown with level
        this.levelDifferenceThreshold = 10; // Fixed threshold for knockout
    }

    private void knockOut(Player player, LivingEntity victim) {
        victim.setPose(Pose.SWIMMING);
        player.sendMessage("§a" + victim.getName() + " a été assommé !");
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.0f, 1.0f);
    }

    private void knockBack(Player player, LivingEntity victim) {
        Vector direction = victim.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
        victim.setVelocity(direction.multiply(0.5).setY(0.5));
        player.sendMessage("§a" + victim.getName() + " a été repoussé !");
        player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1.0f, 1.2f);
    }

    private Entity getLookedAtEntity(Player player) {
        return player.getWorld().rayTraceEntities(
                player.getEyeLocation(),
                player.getLocation().getDirection(),
                10, // Range
                entity -> entity instanceof LivingEntity && !entity.equals(player)
        ) != null ? player.getWorld().rayTraceEntities(
                player.getEyeLocation(),
                player.getLocation().getDirection(),
                10,
                entity -> entity instanceof LivingEntity && !entity.equals(player)
        ).getHitEntity() : null;
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
