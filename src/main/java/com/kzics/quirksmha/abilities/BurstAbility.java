package com.kzics.quirksmha.abilities;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BurstAbility extends QuirkAbility {

    private final JavaPlugin plugin;

    public BurstAbility(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void activate(Player player) {
        player.sendMessage("BurstQuirk activé !");
        burst(player);
    }

    @Override
    public void deactivate(Player player) {
        player.sendMessage("BurstQuirk désactivé !");
    }

    @Override
    public void adjustAttributes(int quirkLevel) {
        // Ajustement des attributs en fonction du niveau (si nécessaire)
    }

    private void burst(Player player) {
        Location location = player.getLocation();
        player.sendMessage("Explosion massive dans 5 secondes, préparez-vous !");

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            location.getWorld().createExplosion(location, 10F, true, true);
            player.setHealth(0);
        }, 100L);
    }

    @Override
    public String name() {
        return "Burst";
    }
}
