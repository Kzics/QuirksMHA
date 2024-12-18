package com.kzics.quirksmha.abilities;

import com.kzics.quirksmha.Main;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ForcePushAbility extends QuirkAbility {

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private boolean isCharging = false;
    private double maxPushRange;
    private double chargeForce;
    private long cooldown;

    public ForcePushAbility() {
        this.maxPushRange = 10.0;
        this.chargeForce = 1.0;
        this.cooldown = 15 * 1000L;
    }

    @Override
    public void activate(Player player) {
    }

    @Override
    public void deactivate(Player player) {
    }

    @Override
    public void adjustAttributes(int quirkLevel) {
        this.maxPushRange = 10.0 + (quirkLevel - 1);
        this.cooldown = Math.max(5 * 1000L, 15 * 1000L - (quirkLevel - 1) * 500L);
    }


    @Override
    public void onInteractAt(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            onLeftClick(event.getPlayer());
        } else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            onRightClick(event.getPlayer(), event.getPlayer().isSneaking());
        }
    }

    public void onLeftClick(Player player) {
        if (isOnCooldown(player)) {
            player.sendMessage("§cForce Push est en cooldown !");
            return;
        }

        if (isCharging) {
            executeForcePush(player, chargeForce);
        } else {
            executeForcePush(player, 10);
        }

        startCooldown(player);
    }

    public void onRightClick(Player player, boolean isSneaking) {
        if (isOnCooldown(player)) {
            player.sendMessage("§cForce Hold/Field est en cooldown !");
            return;
        }

        if (isSneaking) {
            createForceField(player);
        } else {
            executeForceHold(player);
        }

        startCooldown(player);
    }

    public void onSneak(Player player, boolean startCharging) {
        if (startCharging) {
            startCharge(player);
        } else {
            stopCharge(player);
        }
    }

    private void executeForcePush(Player player, double force) {
        player.sendMessage("§aForce Push activé avec une force de " + force + " !");
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 1.0f);

        for (Entity entity : player.getNearbyEntities(maxPushRange, maxPushRange, maxPushRange)) {
            if (entity instanceof LivingEntity livingEntity && !livingEntity.equals(player)) {
                Vector pushDirection = entity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                pushDirection.setY(pushDirection.getY() + 0.5);
                livingEntity.setVelocity(pushDirection.multiply(force));
            }
        }

        player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation().add(0, 1, 0), 1);
    }

    private void executeForceHold(Player player) {
        player.sendMessage("§aForce Hold activé !");
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);

        LivingEntity target = getLookedAtEntity(player);
        if (target == null) {
            player.sendMessage("§cAucune cible trouvée pour Force Hold !");
            return;
        }

        player.sendMessage("\u00a7aVous maintenez " + target.getName() + " !");

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks > 200) {
                    player.sendMessage("\u00a7cForce Hold terminé !");
                    cancel();
                    return;
                }

                Location targetLocation = player.getEyeLocation().add(player.getLocation().getDirection().multiply(4));

                Vector toTarget = targetLocation.toVector().subtract(target.getLocation().toVector()).normalize();
                target.setVelocity(toTarget.multiply(0.5));

                player.getWorld().spawnParticle(Particle.DUST, target.getLocation().add(0, 1, 0), 5, 0.2, 0.2, 0.2,
                        new Particle.DustOptions(Color.PURPLE, 1.0f));

                ticks++;
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }

    private void createForceField(Player player) {
        player.sendMessage("§aForcefield activé !");
        player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1.0f, 1.0f);

        for (Entity entity : player.getNearbyEntities(maxPushRange, maxPushRange, maxPushRange)) {
            if (entity instanceof LivingEntity livingEntity && !livingEntity.equals(player)) {
                Vector pushDirection = livingEntity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                livingEntity.setVelocity(pushDirection.multiply(2.0)); // Force fixe pour le champ de force
            }
        }

        player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, player.getLocation(), 20, 2.0, 2.0, 2.0);
    }

    private void startCharge(Player player) {
        isCharging = true;
        chargeForce = 1.0;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isCharging) {
                    cancel();
                    return;
                }

                chargeForce = Math.min(3.0, chargeForce + 0.1);
                generateChargeParticles(player);
            }
        }.runTaskTimer(Main.getInstance(), 0, 10);
    }

    private void stopCharge(Player player) {
        isCharging = false;
        player.sendMessage("§aForce Push chargé à " + chargeForce + " !");
    }

    private void generateChargeParticles(Player player) {
        player.getWorld().spawnParticle(Particle.DUST, player.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5,
                new Particle.DustOptions(Color.BLUE, 1.0f));
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

    private LivingEntity getLookedAtEntity(Player player) {
        Location playerLocation = player.getEyeLocation();

        for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
            if (entity instanceof LivingEntity livingEntity && !livingEntity.equals(player)) {
                Vector toEntity = livingEntity.getLocation().toVector().subtract(playerLocation.toVector()).normalize();

                return livingEntity;
            }
        }
        return null;
    }

    @Override
    public String name() {
        return "Force Push";
    }
}
