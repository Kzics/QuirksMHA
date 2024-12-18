package com.kzics.quirksmha.abilities;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SuperRegenAbility extends QuirkAbility {

    private int hungerThreshold;

    public SuperRegenAbility() {
        this.hungerThreshold = 3; // Seuil par défaut
    }

    @Override
    public void activate(Player player) {
        applyRegeneration(player);
    }

    @Override
    public void deactivate(Player player) {
        removeRegeneration(player);
    }

    @Override
    public void adjustAttributes(int quirkLevel) {
        this.hungerThreshold = Math.max(1, 3 - (quirkLevel - 1)); // Réduit le seuil de faim avec le niveau
    }

    @Override
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            applyRegeneration(player);
        }
    }

    private void applyRegeneration(Player player) {
        if (player.getFoodLevel() > hungerThreshold) {
            if (!player.hasPotionEffect(PotionEffectType.REGENERATION)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0, true, false));
            }
        } else {
            removeRegeneration(player);
        }
    }

    private void removeRegeneration(Player player) {
        if (player.hasPotionEffect(PotionEffectType.REGENERATION)) {
            player.removePotionEffect(PotionEffectType.REGENERATION);
        }
    }

    @Override
    public String name() {
        return "Super Regen";
    }
}
