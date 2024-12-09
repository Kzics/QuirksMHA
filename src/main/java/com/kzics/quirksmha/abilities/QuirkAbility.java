package com.kzics.quirksmha.abilities;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public abstract class QuirkAbility {
    abstract void activate(Player player);
    abstract void deactivate(Player player);
    abstract void adjustAttributes(int quirkLevel);
    public void onInteract(PlayerInteractEntityEvent event) { }
}
