package com.kzics.quirksmha.abilities;

import com.kzics.quirksmha.Main;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WeakSpotAbility extends QuirkAbility {

    private final Map<UUID, Long> markCooldowns = new HashMap<>();
    private final Map<UUID, UUID> markedTargets = new HashMap<>();
    private double markDuration;
    private double critDamageMultiplier;
    private long markCooldown;
    private boolean isMarking = false;

    public WeakSpotAbility() {
        this.markDuration = 10.0; // Base mark duration in seconds
        this.critDamageMultiplier = 1.75; // Base crit damage multiplier
        this.markCooldown = 40 * 1000L; // Cooldown in milliseconds
    }

    @Override
    public void activate(Player player) {
        // Activation par sneaking
    }

    @Override
    public void deactivate(Player player) {
        // Rien à désactiver pour cette capacité
    }

    @Override
    public void adjustAttributes(int quirkLevel) {
        this.markDuration = 10.0 + (quirkLevel - 1) * 0.3; // Durée augmente avec le niveau
        this.critDamageMultiplier = 1.75 + (quirkLevel - 1) * 0.03; // Multiplicateur augmente avec le niveau
        this.markCooldown = Math.max(20 * 1000L, 40 * 1000L - (quirkLevel - 1) * 1000L); // Réduction du cooldown avec le niveau
    }

    @Override
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        if (!event.isSneaking()) return; // Ne pas agir si le joueur arrête de sneaky
        if (isMarking || isOnCooldown(player)) {
            player.sendMessage("§cVous êtes déjà en train de marquer une cible ou en cooldown !");
            return;
        }

        LivingEntity target = getLookedAtEntity(player);
        if (target == null) {
            player.sendMessage("§cAucune cible en vue !");
            return;
        }

        startMarking(player, target);
    }

    private void startMarking(Player player, LivingEntity target) {
        isMarking = true;

        player.sendMessage("§aDébut du marquage sur " + target.getName() + "...");

        new BukkitRunnable() {
            int markTime = 20 * 20; // 20 secondes (en ticks)
            boolean targetLost = false;

            @Override
            public void run() {
                if (markTime <= 0 || !player.isSneaking() || !player.hasLineOfSight(target)) {
                    if (markTime > 0) targetLost = true;
                    cancel();
                }

                markTime -= 10;
            }

            @Override
            public void cancel() {
                super.cancel();
                isMarking = false;
                if (targetLost) {
                    player.sendMessage("§cMarquage annulé : cible perdue !");
                } else {
                    applyMark(player, target);
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 10); // 0.5s
    }

    private void applyMark(Player player, LivingEntity target) {
        markedTargets.put(target.getUniqueId(), player.getUniqueId());
        target.setGlowing(true);
        player.sendMessage("§a" + target.getName() + " est marqué !");
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);

        new BukkitRunnable() {
            @Override
            public void run() {
                target.setGlowing(false);
                markedTargets.remove(target.getUniqueId());
                player.sendMessage("§cMarquage expiré sur " + target.getName());
            }
        }.runTaskLater(Main.getInstance(), (long) (markDuration * 20)); // Convertir secondes en ticks

        markCooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }

    @Override
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        UUID attackerId = player.getUniqueId();
        if (!markedTargets.containsKey(target.getUniqueId())) return;
        if (!markedTargets.get(target.getUniqueId()).equals(attackerId)) return;

        // Augmenter les dégâts de l'attaque
        double baseDamage = event.getDamage();
        event.setDamage(baseDamage * critDamageMultiplier);

        player.sendMessage("§aVous avez infligé un coup critique à " + target.getName() + " !");
    }

    private boolean isOnCooldown(Player player) {
        UUID playerId = player.getUniqueId();
        if (!markCooldowns.containsKey(playerId)) return false;

        long lastMarkTime = markCooldowns.get(playerId);
        return (System.currentTimeMillis() - lastMarkTime) < markCooldown;
    }

    private LivingEntity getLookedAtEntity(Player player) {
        Vector playerDirection = player.getLocation().getDirection();
        Location playerLocation = player.getEyeLocation();

        for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
            if (entity instanceof LivingEntity livingEntity && !livingEntity.equals(player)) {
                Vector toEntity = livingEntity.getLocation().toVector().subtract(playerLocation.toVector()).normalize();
                if (playerDirection.dot(toEntity) > 0.99) { // Angle très proche
                    return livingEntity;
                }
            }
        }
        return null;
    }

    @Override
    public String name() {
        return "Weak Spot";
    }
}
