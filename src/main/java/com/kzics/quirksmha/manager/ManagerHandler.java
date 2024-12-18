package com.kzics.quirksmha.manager;

import com.kzics.quirksmha.Main;
import com.kzics.quirksmha.abilities.QuirkManager;
import fr.skytasul.glowingentities.GlowingEntities;

public class ManagerHandler {

    private final PlayerDataManager playerDataManager;
    private final CacheManager cacheManager;
    private final QuirkManager quirkManager;
    private final GlowingEntities glowingEntities;
    public ManagerHandler(Main main) {
        this.playerDataManager = new PlayerDataManager();
        this.cacheManager = new CacheManager();
        this.quirkManager = new QuirkManager();
        this.glowingEntities = new GlowingEntities(main);
    }

    public GlowingEntities glowingEntities() {
        return glowingEntities;
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
