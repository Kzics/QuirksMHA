package com.kzics.quirksmha.abilities;

import com.kzics.quirksmha.Main;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;

import java.util.*;

public class PhaseAbility extends QuirkAbility {

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final List<Monster> spawnedMonsters = new ArrayList<>();
    private double duration;
    private long cooldown;

    public PhaseAbility() {
        this.duration = 15.0;
        this.cooldown = 30 * 1000L;
    }

    @Override
    public void activate(Player player) {
        if (isOnCooldown(player)) {
            player.sendMessage("§cPhase Shift est en cooldown !");
            return;
        }

        Player target = getLookedAtPlayer(player);
        if (target == null) {
            player.sendMessage("§cVous ne regardez aucun joueur !");
            return;
        }

        player.sendMessage("§aPhase Shift activé sur " + target.getName() + " !");
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        startPhaseShift(target);
        startCooldown(player);
    }

    @Override
    public void deactivate(Player player) {
        player.sendMessage("§cPhase Shift terminé !");
        resetVisibility(player);
        killAllSpawnedMonsters();
    }

    @Override
    public void adjustAttributes(int quirkLevel) {
        this.duration = 15.0 + (quirkLevel - 1) * 2.0;
        this.cooldown = Math.max(5 * 1000L, 30 * 1000L - (quirkLevel - 1) * 3000L);
    }

    private void startPhaseShift(Player target) {
        target.setGameMode(GameMode.ADVENTURE);
        hidePlayersAndBlocks(target);
        for (int i = 0; i < 5; i++) {
            spawnMonsters(target);
        }

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= duration * 20) {
                    deactivate(target);
                    cancel();
                    return;
                }
                ticks += 5;
            }
        }.runTaskTimer(Main.getInstance(), 0, 5);
    }

    private void hidePlayersAndBlocks(Player player) {
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (!other.equals(player)) {
                player.hidePlayer(Main.getInstance(), other);
            }
        }

        for (Entity entity : player.getWorld().getEntities()) {
            if (entity instanceof Monster) {
                player.showEntity(Main.getInstance(), entity);
            } else if (entity instanceof Player && !entity.equals(player)) {
                player.hideEntity(Main.getInstance(), entity);
            }
        }
    }

    private void resetVisibility(Player player) {
        for (Player other : Bukkit.getOnlinePlayers()) {
            player.showPlayer(Main.getInstance(), other);
        }
    }

    private void spawnMonsters(Player player) {
        Random random = new Random();
        Location playerLocation = player.getLocation();

        double offsetX = -10 + (10 - (-10)) * random.nextDouble();
        double offsetZ = -10 + (10 - (-10)) * random.nextDouble();

        Location spawnLocation = playerLocation.clone().add(offsetX, 0, offsetZ);
        Zombie zombie = player.getWorld().spawn(spawnLocation, Zombie.class);
        zombie.setVisibleByDefault(false);
        zombie.setTarget(player);
        player.showEntity(Main.getInstance(), zombie);

        spawnedMonsters.add(zombie);
    }

    private void killAllSpawnedMonsters() {
        for (Monster monster : spawnedMonsters) {
            if (!monster.isDead()) {
                monster.setHealth(0);
            }
        }
        spawnedMonsters.clear();
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
        RayTraceResult result = player.getWorld().rayTraceEntities(player.getEyeLocation(), player.getLocation().getDirection(), 20, entity -> entity instanceof Player && !entity.equals(player));
        if (result != null && result.getHitEntity() instanceof Player target) {
            return target;
        }
        return null;
    }

    @Override
    public String name() {
        return "Phase Shift";
    }
}
