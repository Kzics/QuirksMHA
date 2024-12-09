package com.kzics.quirksmha.abilities;

import com.kzics.quirksmha.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class DangerSenseAbility implements QuirkAbility {
    private double baseRange = 20.0;
    private BossBar dangerBar;
    private boolean isTracking = false;
    private final int bossBarSegments = 40;

    private void activateDangerSense(Player player) {
        isTracking = true;

        if (dangerBar == null) {
            dangerBar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SEGMENTED_20);
            dangerBar.addPlayer(player);
        }

        player.sendMessage("Danger Sense activé !");
        player.playSound(player.getLocation(), "entity.enderman.teleport", 1.0f, 1.0f);

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= 20 * 20) {
                    deactivateDangerSense(player);
                    this.cancel();
                    return;
                }

                updateDangerBar(player);
                ticks += 5;
            }
        }.runTaskTimer(Main.getInstance(), 0, 5);
    }

    private void updateDangerBar(Player player) {
        Location playerLocation = player.getLocation();
        Vector playerDirection = playerLocation.getDirection();

        List<LivingEntity> nearbyEntities = new ArrayList<>();
        for (Entity entity : player.getWorld().getNearbyEntities(playerLocation, baseRange, baseRange, baseRange)) {
            if (entity instanceof LivingEntity livingEntity && !livingEntity.equals(player)) {
                nearbyEntities.add(livingEntity);
            }
        }

        nearbyEntities.sort(Comparator.comparingDouble(entity -> getHorizontalAngle(playerDirection, playerLocation, entity)));

        List<LivingEntity> trackedEntities = nearbyEntities.stream().limit(3).toList();

        StringBuilder barContent = new StringBuilder("-".repeat(bossBarSegments));
        Set<Integer> usedPositions = new HashSet<>();

        for (LivingEntity entity : trackedEntities) {
            int position = getBarPosition(playerDirection, playerLocation, entity);
            position = resolveConflict(position, usedPositions);

            if (position >= 0 && position < bossBarSegments) {
                barContent.setCharAt(position, 'X');
                usedPositions.add(position);
            }
        }

        dangerBar.setTitle(barContent.toString());
    }

    private int getBarPosition(Vector playerDirection, Location playerLocation, LivingEntity entity) {
        Vector toEntity = entity.getLocation().toVector().subtract(playerLocation.toVector()).normalize();
        double angle = Math.toDegrees(Math.atan2(toEntity.getZ(), toEntity.getX()) - Math.atan2(playerDirection.getZ(), playerDirection.getX()));

        if (angle < -180.0) angle += 360.0;
        if (angle > 180.0) angle -= 360.0;

        double normalized = (angle + 90) / 180;
        return (int) (normalized * bossBarSegments);
    }

    private double getHorizontalAngle(Vector playerDirection, Location playerLocation, LivingEntity entity) {
        Vector toEntity = entity.getLocation().toVector().subtract(playerLocation.toVector()).normalize();
        return Math.abs(Math.toDegrees(Math.atan2(toEntity.getZ(), toEntity.getX()) - Math.atan2(playerDirection.getZ(), playerDirection.getX())));
    }

    private int resolveConflict(int position, Set<Integer> usedPositions) {
        if (!usedPositions.contains(position)) {
            return position;
        }

        int left = position - 1;
        int right = position + 1;

        while (left >= 0 || right < bossBarSegments) {
            if (left >= 0 && !usedPositions.contains(left)) {
                return left;
            }
            if (right < bossBarSegments && !usedPositions.contains(right)) {
                return right;
            }
            left--;
            right++;
        }

        return position;
    }

    private void deactivateDangerSense(Player player) {
        if (dangerBar != null) {
            dangerBar.removeAll();
            dangerBar = null;
        }

        isTracking = false;
        player.sendMessage("Danger Sense désactivé !");
        player.playSound(player.getLocation(), "entity.enderman.death", 1.0f, 1.0f);
    }

    @Override
    public void activate(Player player) {
        if (isTracking) {
            player.sendMessage("Danger Sense est déjà actif !");
            return;
        }

        activateDangerSense(player);    }

    @Override
    public void deactivate(Player player) {
        deactivateDangerSense(player);
    }

    @Override
    public void adjustAttributes(int quirkLevel) {
        baseRange = 20.0 + (quirkLevel - 1) * 2.0;
    }
}
