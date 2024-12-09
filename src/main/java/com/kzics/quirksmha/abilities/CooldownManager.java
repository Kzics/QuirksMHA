package com.kzics.quirksmha.abilities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    private final Map<UUID, Integer> cooldownMap = new HashMap<>();

    public void setCooldown(UUID playerID, int cooldown) {
        cooldownMap.put(playerID, cooldown);
    }

    public int getCooldown(UUID playerID) {
        return cooldownMap.getOrDefault(playerID, 0);
    }

    public void reduceCooldowns() {
        for (UUID playerID : cooldownMap.keySet()) {
            int timeLeft = cooldownMap.get(playerID);
            if (timeLeft > 0) cooldownMap.put(playerID, timeLeft - 1);
        }
    }
}

