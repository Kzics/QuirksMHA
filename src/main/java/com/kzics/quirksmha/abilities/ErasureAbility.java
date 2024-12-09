package com.kzics.quirksmha.abilities;

import com.kzics.quirksmha.Main;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;

import java.util.HashSet;
import java.util.Set;

public class ErasureAbility implements QuirkAbility {

    private int maxDuration; // Durée maximale d'efficacité
    private int cooldown; // Cooldown en secondes
    private final Set<LivingEntity> disabledQuirks = new HashSet<>(); // Liste des entités désactivées
    private boolean isActive = false; // État de la capacité

    public ErasureAbility() {
        this.maxDuration = 10; // Durée initiale
        this.cooldown = 15; // Cooldown initial
    }

    @Override
    public void activate(Player player) {
        if (isActive) {
            player.sendMessage("§cVotre compétence Erasure est déjà active !");
            return;
        }

        // Activer la capacité
        isActive = true;
        player.sendMessage("§aErasure activé !");
        playActivationEffect(player);

        // Lancer une tâche pour surveiller la cible pendant `maxDuration`
        new BukkitRunnable() {
            int elapsedTicks = 0;

            @Override
            public void run() {
                if (elapsedTicks >= maxDuration * 20 || !isActive) {
                    deactivate(player);
                    this.cancel();
                    return;
                }

                LivingEntity target = getTarget(player);
                if (target != null) {
                    applyGlowEffect(target, true);
                    disableQuirk(target, player);
                } else {
                    resetGlowEffects();
                }

                elapsedTicks += 10; // Rafraîchissement rapide toutes les 10 ticks (0,5 seconde)
            }
        }.runTaskTimer(Main.getInstance(), 0, 10);
    }

    @Override
    public void deactivate(Player player) {
        if (!isActive) return;

        isActive = false;
        resetGlowEffects();
        player.sendMessage("§aErasure désactivé !");
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_DEATH, 1.0f, 1.0f);
    }

    @Override
    public void adjustAttributes(int quirkLevel) {
        this.maxDuration = 10 + (quirkLevel - 1) * 3;
        this.cooldown = Math.max(5, 15 - (quirkLevel - 1));
    }

    private LivingEntity getTarget(Player player) {
        RayTraceResult result = player.getWorld().rayTraceEntities(
                player.getEyeLocation(),
                player.getEyeLocation().getDirection(),
                20.0,
                entity -> entity instanceof LivingEntity && !entity.equals(player)
        );

        return result != null ? (LivingEntity) result.getHitEntity() : null;
    }

    private void disableQuirk(LivingEntity target, Player player) {
        if (disabledQuirks.contains(target)) return;

        disabledQuirks.add(target);
        player.sendMessage("§cLe Quirk de §6" + target.getName() + " §ca été désactivé !");
        target.sendMessage("§cVotre Quirk a été désactivé par §6" + player.getName() + "§c !");
    }

    private void applyGlowEffect(LivingEntity target, boolean enable) {
        target.setGlowing(enable);
    }

    private void resetGlowEffects() {
        for (LivingEntity entity : disabledQuirks) {
            entity.setGlowing(false);
        }
        disabledQuirks.clear();
    }

    private void playActivationEffect(Player player) {
        Location location = player.getEyeLocation().add(player.getLocation().getDirection().multiply(1.5));

        for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 16) {
            double x = Math.cos(angle) * 1;
            double z = Math.sin(angle) * 1;
            Location particleLoc = location.clone().add(x, 0, z);
            player.getWorld().spawnParticle(Particle.DUST, particleLoc, 1,
                    new Particle.DustOptions(Color.RED, 1.5f));
        }

        player.getWorld().playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1.0f, 1.5f);
    }
}
