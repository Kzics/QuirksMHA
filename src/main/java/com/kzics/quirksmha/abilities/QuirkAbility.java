package com.kzics.quirksmha.abilities;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public abstract class QuirkAbility {
    public abstract String name();
    public abstract void activate(Player player);
    public abstract void deactivate(Player player);
    public abstract void adjustAttributes(int quirkLevel);
    public void onInteract(PlayerInteractEntityEvent event) { }
    public void onFoodLevelChange(FoodLevelChangeEvent event) { }
    public void onDamage(EntityDamageByEntityEvent event) { }
    public void onSneak(PlayerToggleSneakEvent event) { }
    public void onInteractAt(PlayerInteractEvent event) { }
}
