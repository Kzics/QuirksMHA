package com.kzics.quirksmha.abilities;

import com.kzics.quirksmha.Main;
import com.kzics.quirksmha.manager.CacheManager;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class FajinAbility implements QuirkAbility {

    private final CacheManager cacheManager;
    private double maxChargeTime; // Durée maximale de charge
    private boolean isCharging = false;
    private boolean isSneakCharge = false;

    public FajinAbility(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        this.maxChargeTime = 10.0; // Par défaut
    }

    @Override
    public void activate(Player player) {
        if (isCharging) return;

        isCharging = true;
        cacheManager.addFajin(player.getUniqueId(), 0);

        // Lancer une tâche pour exécuter automatiquement l'action après un délai
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isCharging) {
                    this.cancel();
                    return;
                }

                int charge = cacheManager.getFajin(player.getUniqueId());
                if (isSneakCharge) {
                    performSuperJump(player, charge);
                } else {
                    performSuperPunch(player, charge);
                }

                resetChargeState(player);
                this.cancel();
            }
        }.runTaskLater(Main.getInstance(), (long) (maxChargeTime * 20)); // maxChargeTime secondes en ticks
    }

    @Override
    public void deactivate(Player player) {
        if (!isCharging) return;

        int charge = cacheManager.getFajin(player.getUniqueId());
        if (isSneakCharge) {
            performSuperJump(player, charge);
        } else {
            performSuperPunch(player, charge);
        }

        resetChargeState(player);
    }

    @Override
    public void adjustAttributes(int quirkLevel) {
        this.maxChargeTime = 20.0 + (quirkLevel - 1); // Augmenter la durée maximale de charge par niveau
    }

    private void performSuperJump(Player player, int charge) {
        double jumpStrength = 1.0 + (charge / maxChargeTime) * 2.5; // Force du saut proportionnelle à la charge
        Vector jumpVelocity = player.getVelocity().add(new Vector(0, jumpStrength, 0));
        player.setVelocity(jumpVelocity);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.2f);
        player.sendMessage("§cSuper Jump activé avec une charge de §6" + charge + " §cpoints !");
    }

    private void performSuperPunch(Player player, int charge) {
        double damageMultiplier = 1.0 + (charge / maxChargeTime) * 3.0; // Dégâts proportionnels à la charge
        Location punchLoc = player.getEyeLocation().add(player.getLocation().getDirection().multiply(2));

        // Effets visuels
        player.getWorld().spawnParticle(Particle.EXPLOSION, punchLoc, 1);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.5f);

        player.sendMessage("§cSuper Punch activé avec une charge de §6" + charge + " §cpoints !");
    }

    private void resetChargeState(Player player) {
        isCharging = false;
        isSneakCharge = false;
        cacheManager.removeFajin(player.getUniqueId());
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (isCharging) {
            isSneakCharge = true;
            cacheManager.addFajin(player.getUniqueId(), cacheManager.getFajin(player.getUniqueId()) + 1);
            generateParticles(player, true);
        }
    }

    @EventHandler
    public void onPunch(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (isCharging) {
            isSneakCharge = false;
            cacheManager.addFajin(player.getUniqueId(), cacheManager.getFajin(player.getUniqueId()) + 1);
            generateParticles(player, false);
        }
    }

    private void generateParticles(Player player, boolean isSneakCharge) {
        Location particleLocation = isSneakCharge
                ? player.getLocation().add(0, 0.2, 0)
                : player.getLocation().add(0, 1.2, 0);

        player.getWorld().spawnParticle(Particle.DUST, particleLocation, 10, 0.3, 0.2, 0.3,
                new Particle.DustOptions(Color.RED, 1.0f));
    }
}
