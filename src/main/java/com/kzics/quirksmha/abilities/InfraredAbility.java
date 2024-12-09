package com.kzics.quirksmha.abilities;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashSet;
import java.util.Set;

public class InfraredAbility implements QuirkAbility {

    private double baseRange = 10.0; // Rayon de base pour détecter les entités
    private boolean isActive = false;
    private Team infraredTeam; // Équipe pour les entités avec effet Glowing
    private final Set<LivingEntity> glowingEntities = new HashSet<>();

    @Override
    public void adjustAttributes(int quirkLevel) {
        this.baseRange = 10.0 + (quirkLevel - 1) * 0.5; // Augmentation de la portée par niveau

    }

    @Override
    public void activate(Player player) {
        if (isActive) {
            player.sendMessage("Infrared est déjà actif !");
            return;
        }

        isActive = true;

        // Créer ou récupérer l'équipe Infrared
        setupInfraredTeam(player.getScoreboard());

        // Appliquer l'effet de vision (Night Vision)
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        player.sendMessage("Infrared activé ! Toutes les entités proches brillent en orange.");

        // Détecter et appliquer l'effet Glowing aux entités proches
        applyGlowingToNearbyEntities(player);

        // Effet visuel autour du joueur
        Location loc = player.getLocation();
        player.getWorld().spawnParticle(Particle.DUST, loc, 20, 1, 1, 1,
                new Particle.DustOptions(Color.ORANGE, 1.5f));
    }

    private void setupInfraredTeam(Scoreboard scoreboard) {
        // Vérifier si l'équipe existe déjà
        if (infraredTeam == null) {
            infraredTeam = scoreboard.getTeam("Infrared");
            if (infraredTeam == null) {
                infraredTeam = scoreboard.registerNewTeam("Infrared");
                infraredTeam.color(NamedTextColor.GOLD); // Définir la couleur de l'équipe
                infraredTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
                infraredTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            }
        }
    }

    private void applyGlowingToNearbyEntities(Player player) {
        Location playerLocation = player.getLocation();

        for (Entity entity : player.getWorld().getNearbyEntities(playerLocation, baseRange, baseRange, baseRange)) {
            if (entity instanceof LivingEntity livingEntity && !livingEntity.equals(player)) {
                infraredTeam.addEntry(livingEntity.getUniqueId().toString()); // Ajouter l'entité à l'équipe
                livingEntity.setGlowing(true); // Activer l'effet Glowing
                glowingEntities.add(livingEntity);
            }
        }
    }

    @Override
    public void deactivate(Player player) {
        if (!isActive) {
            player.sendMessage("Infrared n'est pas actif !");
            return;
        }

        isActive = false;

        // Supprimer l'effet de vision
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_DEATH, 1.0f, 1.0f);
        player.sendMessage("Infrared désactivé. Les entités ne brillent plus en orange.");

        // Supprimer l'effet Glowing des entités et les retirer de l'équipe
        for (LivingEntity entity : glowingEntities) {
            entity.setGlowing(false);
            infraredTeam.removeEntry(entity.getUniqueId().toString());
        }
        glowingEntities.clear();
    }
}
