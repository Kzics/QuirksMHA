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

    public Quirk getQuirk(UUID player) {
        return playerQuirks.get(player);
    }


    public boolean hasQuirk(UUID player) {
        return playerQuirks.containsKey(player);
    }
}

