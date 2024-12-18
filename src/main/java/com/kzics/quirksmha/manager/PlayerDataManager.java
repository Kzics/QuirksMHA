package com.kzics.quirksmha.manager;

import com.kzics.quirksmha.data.PlayerData;
import com.kzics.quirksmha.abilities.Quirk;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class PlayerDataManager {
    private final Map<UUID, PlayerData> playerDataMap = new HashMap<>();

    public PlayerData getPlayerData(UUID playerId) {
        return playerDataMap.get(playerId);
    }

    public void initializePlayer(UUID playerId, Quirk quirk) {
        playerDataMap.put(playerId, new PlayerData(quirk, new Random().nextInt(100) + 1));
    }
}
