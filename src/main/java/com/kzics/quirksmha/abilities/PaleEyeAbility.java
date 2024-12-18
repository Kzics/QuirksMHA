package com.kzics.quirksmha.abilities;

import com.kzics.quirksmha.Main;
import com.kzics.quirksmha.manager.ManagerHandler;
import fr.skytasul.glowingentities.GlowingEntities;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PaleEyeAbility extends QuirkAbility {

    private final ManagerHandler managerHandler;
    private final Set<UUID> glowingEntities = new HashSet<>();

    private double range;

    public PaleEyeAbility(ManagerHandler managerHandler) {
        this.managerHandler = managerHandler;
        this.range = 100.0;
    }

    @Override
    public String name() {
        return "Pale Eye";
    }

    @Override
    public void activate(Player player) {
        player.sendMessage("§aPale Eye activé !");
        makeEntitiesGlow(player);
    }

    @Override
    public void deactivate(Player player) {
        player.sendMessage("§cPale Eye désactivé !");
        stopEntityGlowing(player);
    }

    @Override
    public void adjustAttributes(int quirkLevel) {
        this.range = 100.0 + (quirkLevel - 1) * 10.0;
    }

    private void makeEntitiesGlow(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            for (Entity entity : player.getNearbyEntities(range, range, range)) {
                if (!glowingEntities.contains(entity.getUniqueId())) {
                    glowEntity(entity, player);
                    glowingEntities.add(entity.getUniqueId());
                }
            }
        });
    }

    private void stopEntityGlowing(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            for (UUID entityId : glowingEntities) {
                Entity entity = Bukkit.getEntity(entityId);
                if (entity != null) {
                    stopGlowing(entity, player);
                }
            }
            glowingEntities.clear();
        });
    }

    private void glowEntity(Entity entity, Player receiver) {
        try {
            managerHandler.glowingEntities().setGlowing(entity, receiver);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private void stopGlowing(Entity entity, Player receiver) {
        try {
            managerHandler.glowingEntities().unsetGlowing(entity, receiver);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
