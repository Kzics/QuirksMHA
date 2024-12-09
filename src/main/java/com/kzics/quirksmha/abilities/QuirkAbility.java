package com.kzics.quirksmha.abilities;

import org.bukkit.entity.Player;

public interface QuirkAbility {
    void activate(Player player); // Activer l'ability
    void deactivate(Player player); // Désactiver l'ability
    void adjustAttributes(int quirkLevel); // Ajuster les attributs selon le niveau du Quirk
}
