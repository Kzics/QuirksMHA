package com.kzics.quirksmha.abilities;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QuirkManager {
    private final Map<UUID, Quirk> playerQuirks = new HashMap<>();

    public void addQuirk(Player player, Quirk quirk) {
        playerQuirks.put(player.getUniqueId(), quirk);
    }

    public Quirk getQuirk(Player player) {
        return playerQuirks.get(player.getUniqueId());
    }

    public void activateQuirk(Player player) {
        Quirk quirk = playerQuirks.get(player.getUniqueId());
        if (quirk instanceof QuirkAbility) {
            ((QuirkAbility) quirk).activate(player);
        }
    }

    public void deactivateQuirk(Player player) {
        Quirk quirk = playerQuirks.get(player.getUniqueId());
        if (quirk instanceof QuirkAbility) {
            ((QuirkAbility) quirk).deactivate(player);
        }
    }

    public boolean hasQuirk(Player player) {
        return playerQuirks.containsKey(player.getUniqueId());
    }
}

