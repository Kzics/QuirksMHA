package com.kzics.quirksmha.manager;

import com.kzics.quirksmha.abilities.QuirkManager;

public class ManagerHandler {

    private final PlayerDataManager playerDataManager;
    private final CacheManager cacheManager;
    private final QuirkManager quirkManager;
    public ManagerHandler() {
        this.playerDataManager = new PlayerDataManager();
        this.cacheManager = new CacheManager();
        this.quirkManager = new QuirkManager();
    }

    public PlayerDataManager playerDataManager() {
        return playerDataManager;
    }

    public CacheManager cacheManager() {
        return cacheManager;
    }

    public QuirkManager quirkManager() {
        return quirkManager;
    }
}
