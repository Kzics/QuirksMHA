package com.kzics.quirksmha.abilities;

import org.bukkit.entity.Player;

import java.util.*;

public class QuirkManager {
    private final Map<UUID, Quirk> playerQuirks = new HashMap<>();
    private final List<QuirkAbility> abilities = new ArrayList<>();
    private final HashMap<UUID, QuirkAbility> activeAbilities = new HashMap<>();
    public void addQuirk(Player player, Quirk quirk) {
        playerQuirks.put(player.getUniqueId(), quirk);
    }

    public Quirk getQuirk(UUID player) {
        return playerQuirks.get(player);
    }

    public void addActiveAbility(UUID player, QuirkAbility ability) {
        activeAbilities.put(player, ability);
    }

    public QuirkAbility getActiveAbility(UUID player) {
        return activeAbilities.get(player);
    }

    public QuirkAbility getAbility(String name) {
        for (QuirkAbility ability : abilities) {
            if (ability.name().equals(name)) {
                return ability;
            }
        }
        return null;
    }

    public void addAbility(QuirkAbility ability) {
        abilities.add(ability);
    }

    public void setActiveAbility(UUID player, QuirkAbility ability) {
        if (ability != null) {
            activeAbilities.put(player, ability);
        }
    }

    public List<QuirkAbility> getAbilities() {
        return abilities;
    }


    public boolean hasQuirk(UUID player) {
        return playerQuirks.containsKey(player);
    }
}