package com.kzics.quirksmha.manager;

import java.util.HashMap;
import java.util.UUID;

public class CacheManager {

    private final HashMap<UUID, Integer> fajinCache = new HashMap<>();

    public CacheManager() {

    }

    public void addFajin(UUID playerUUID, int fajin) {
        fajinCache.put(playerUUID, fajin);
    }

    public int getFajin(UUID playerUUID) {
        return fajinCache.get(playerUUID);
    }

    public boolean isFajin(UUID playerUUID) {
        return fajinCache.containsKey(playerUUID);
    }

    public void removeFajin(UUID playerUUID) {
        fajinCache.remove(playerUUID);
    }
}
