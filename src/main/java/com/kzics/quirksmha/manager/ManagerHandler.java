package com.kzics.quirksmha.manager;

public class ManagerHandler {

    private final PlayerDataManager playerDataManager;
    private final CacheManager cacheManager;
    public ManagerHandler() {
        this.playerDataManager = new PlayerDataManager();
        this.cacheManager = new CacheManager();
    }

    public PlayerDataManager playerDataManager() {
        return playerDataManager;
    }

    public CacheManager cacheManager() {
        return cacheManager;
    }
}
