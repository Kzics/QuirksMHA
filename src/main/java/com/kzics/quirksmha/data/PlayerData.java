package com.kzics.quirksmha.data;

import com.kzics.quirksmha.abilities.Quirk;

import java.util.EnumMap;
import java.util.Map;

public class PlayerData {
    public enum Stat {
        STRENGTH, SPEED, HEALTH, DURABILITY
    }

    private final Map<Stat, Integer> stats = new EnumMap<>(Stat.class);
    private int tokensUsed;
    private final int maxTokens = 15;
    private Quirk quirk;
    private int level;

    public PlayerData(Quirk quirk, int level) {
        this.quirk = quirk;
        for (Stat stat : Stat.values()) {
            stats.put(stat, 0);
        }
        this.tokensUsed = 0;
        this.level = 1;
    }

    public int level() {
        return level;
    }

    public boolean levelUpStat(Stat stat) {
        if (tokensUsed >= maxTokens || stats.get(stat) >= 8) return false;
        stats.put(stat, stats.get(stat) + 1);
        tokensUsed++;
        return true;
    }

    public void resetStats() {
        for (Stat stat : Stat.values()) {
            stats.put(stat, 0);
        }
        tokensUsed = 0;
    }

    public boolean isMaxedOut(Stat stat) {
        return stats.get(stat) == 8;
    }

    public int getStatLevel(Stat stat) {
        return stats.get(stat);
    }

    public int getTokensRemaining() {
        return maxTokens - tokensUsed;
    }

    // Quirk
    public Quirk getQuirk() {
        return quirk;
    }

    public void setQuirk(Quirk quirk) {
        this.quirk = quirk;
    }
}
