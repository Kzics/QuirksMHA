package com.kzics.quirksmha.abilities;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class Quirk {
    protected int level;
    protected final List<QuirkAbility> abilities;
    protected final String name;

    public Quirk(String name) {
        this.level = 1; // Niveau initial
        this.abilities = new ArrayList<>();
        initializeAttributes();
        initializeAbilities();
        this.name = name;
    }

    public String name() {
        return name;
    }

    // Initialisation des attributs généraux du Quirk
    protected abstract void initializeAttributes();

    // Initialisation des abilities spécifiques au Quirk
    protected abstract void initializeAbilities();

    public int getLevel() {
        return level;
    }

    public void levelUp() {
        if (level < 10) {
            level++;
            adjustAttributesByLevel();
            for (QuirkAbility ability : abilities) {
                ability.adjustAttributes(level);
            }
        }
    }

    protected abstract void adjustAttributesByLevel();

    // Utilisation des abilities
    public void useAbility(Player player, int abilityIndex) {
        if (abilityIndex >= 0 && abilityIndex < abilities.size()) {
            abilities.get(abilityIndex).activate(player);
        }
    }

    public List<QuirkAbility> getAbilities() {
        return abilities;
    }
}
