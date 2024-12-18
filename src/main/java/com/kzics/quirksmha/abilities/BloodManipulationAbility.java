package com.kzics.quirksmha.abilities;

import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class BloodManipulationAbility extends QuirkAbility {
    private double damage = 1.0;
    private int cooldown = 20;
    private double range = 15.0;
    private double radius = 5.0;

    @Override
    public void activate(Player player) {
        player.getWorld().spawnParticle(Particle.ASH, player.getLocation(), 20, 1, 1, 1);
        player.sendMessage("Blood Manipulation activé !");
    }

    @Override
    public void deactivate(Player player) {
        player.sendMessage("Blood Manipulation désactivé !");
    }

    @Override
    public void adjustAttributes(int quirkLevel) {
        damage += 0.3;
        cooldown = Math.max(1, cooldown - 1); // Réduction minimale à 1 seconde
        range += 0.5;
        radius += 0.2;
    }

    public void use(Player player) {
        if (cooldown <= 0) {
            activate(player);
            cooldown = getCooldown(quirkLevel(player));
        } else {
            player.sendMessage("En cooldown, temps restant : " + cooldown + " secondes");
        }
    }

    private int getCooldown(int level) {
        return Math.max(1, cooldown - level);
    }

    private int quirkLevel(Player player) {
        return 1; // Récupérer le niveau du joueur (logique à ajouter si besoin)
    }

    @Override
    public String name() {
        return "Blood Manipulation";
    }
}
