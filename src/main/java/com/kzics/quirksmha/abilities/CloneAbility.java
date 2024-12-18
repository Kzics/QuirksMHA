package com.kzics.quirksmha.abilities;

import com.kzics.quirksmha.Main;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class CloneAbility extends QuirkAbility {

    Map<UUID, List<NPC>> playerClones = new HashMap<>();
    private final Map<UUID, Long> cloneCooldowns = new HashMap<>();
    private final Map<UUID, Long> teleportCooldowns = new HashMap<>();
    private double maxClones;
    private double cloneStatsMultiplier;
    private long cloneCooldown;
    private long teleportCooldown;
    private final long cloneLifetime = 30 * 1000L; // 30 secondes par défaut

    public CloneAbility() {
        this.maxClones = 3.0;
        this.cloneStatsMultiplier = 0.5;
        this.cloneCooldown = 15 * 1000L;
        this.teleportCooldown = 30 * 1000L;
    }

    @Override
    public void activate(Player player) {
        if (isOnCooldown(player, cloneCooldowns, cloneCooldown)) {
            player.sendMessage("§cClone est en cooldown !");
            return;
        }

        if (!playerClones.containsKey(player.getUniqueId())) {
            playerClones.put(player.getUniqueId(), new ArrayList<>());
        }

        List<NPC> clones = playerClones.get(player.getUniqueId());
        if (clones.size() >= maxClones) {
            player.sendMessage("§cVous avez atteint la limite de clones.");
            return;
        }

        createClone(player);
        startCooldown(player, cloneCooldowns);
    }

    private void createClone(Player player) {
        NPCRegistry registry = CitizensAPI.getNPCRegistry();
        NPC clone = registry.createNPC(player.getType(), player.getName());

        clone.spawn(player.getLocation());
        clone.setProtected(false);
        clone.getEntity().customName(Component.text("Clone de " + player.getName()));
        clone.getEntity().setCustomNameVisible(true);
        player.getInventory().getItemInMainHand();
        clone.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, new ItemStack(player.getInventory().getItemInMainHand()));
        player.getInventory().getItemInOffHand();
        clone.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.OFF_HAND, new ItemStack(player.getInventory().getItemInOffHand()));
        if (player.getInventory().getBoots() != null) {
            clone.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.BOOTS, new ItemStack(player.getInventory().getBoots()));
        }
        if (player.getInventory().getLeggings() != null) {
            clone.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.LEGGINGS, new ItemStack(player.getInventory().getLeggings()));
        }
        if (player.getInventory().getChestplate() != null) {
            clone.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.BODY, new ItemStack(player.getInventory().getChestplate()));
        }
        if (player.getInventory().getHelmet() != null) {
            clone.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HELMET, new ItemStack(player.getInventory().getHelmet()));
        }
        player.sendMessage("§aClone créé avec succès !");
        List<NPC> playerCloneList = playerClones.get(player.getUniqueId());
        playerCloneList.add(clone);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!clone.isSpawned()) {
                    cancel();
                    return;
                }

                findAndAttackNearestMonster(clone, player);
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (clone.isSpawned()) {
                    clone.despawn();
                    playerCloneList.remove(clone);
                }
            }
        }.runTaskLater(Main.getInstance(), cloneLifetime / 50); // Convertir ms en ticks
    }

    @Override
    public void onSneak(PlayerToggleSneakEvent event) {
        swapPositionWithClone(event.getPlayer());
    }

    private void findAndAttackNearestMonster(NPC clone, Player owner) {
        if (!clone.isSpawned()) return;

        LivingEntity cloneEntity = (LivingEntity) clone.getEntity();
        Monster nearestMonster = findNearestMonster(cloneEntity);

        if (nearestMonster != null) {
            // Déplacer le clone vers le monstre le plus proche
            clone.getNavigator().setTarget(nearestMonster, true);

            // Vérifier si le clone est assez proche pour attaquer
            if (cloneEntity.getLocation().distance(nearestMonster.getLocation()) <= 2.0) {
                Vector direction = nearestMonster.getLocation().toVector().subtract(cloneEntity.getLocation().toVector()).normalize();
                nearestMonster.damage(2 * cloneStatsMultiplier);
                nearestMonster.setVelocity(direction.multiply(0.5));
                cloneEntity.getWorld().playSound(cloneEntity.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
            }
        }
    }

    private Monster findNearestMonster(LivingEntity cloneEntity) {
        Monster nearestMonster = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Entity nearby : cloneEntity.getNearbyEntities(20, 20, 20)) {
            if (nearby instanceof Monster monster) {
                double distance = cloneEntity.getLocation().distance(monster.getLocation());
                if (distance < nearestDistance) {
                    nearestMonster = monster;
                    nearestDistance = distance;
                }
            }
        }

        return nearestMonster;
    }

    @Override
    public void deactivate(Player player) {
        if (!playerClones.containsKey(player.getUniqueId()) || playerClones.get(player.getUniqueId()).isEmpty()) {
            player.sendMessage("§cAucun clone à désactiver.");
            return;
        }

        for (NPC clone : playerClones.get(player.getUniqueId())) {
            if (clone.isSpawned()) {
                clone.despawn();
            }
        }

        playerClones.get(player.getUniqueId()).clear();
        player.sendMessage("§cTous les clones ont été désactivés.");
    }

    public void teleportToClone(Player player) {
        if (isOnCooldown(player, teleportCooldowns, teleportCooldown)) {
            player.sendMessage("§cTéléportation vers un clone en cooldown !");
            return;
        }

        List<NPC> clones = playerClones.get(player.getUniqueId());
        if (clones == null || clones.isEmpty()) {
            player.sendMessage("§cAucun clone disponible pour la téléportation.");
            return;
        }

        NPC nearestClone = getLookedAtClone(player, clones);
        if (nearestClone == null) {
            player.sendMessage("§cVous ne regardez aucun clone !");
            return;
        }

        Location cloneLocation = nearestClone.getStoredLocation();
        player.teleport(cloneLocation);
        player.getWorld().playSound(cloneLocation, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        startCooldown(player, teleportCooldowns);
    }

    public void swapPositionWithClone(Player player) {
        if (!player.isSneaking()) return;

        List<NPC> clones = playerClones.get(player.getUniqueId());
        if (clones == null || clones.isEmpty()) return;

        NPC lookedClone = getLookedAtClone(player, clones);
        if (lookedClone == null) return;

        Location playerLocation = player.getLocation();
        Location cloneLocation = lookedClone.getStoredLocation();

        // Téléporter le joueur à la position du clone
        player.teleport(cloneLocation);

        // Téléporter le clone à la position du joueur
        lookedClone.getEntity().teleport(playerLocation);

        // Effet sonore pour l'échange
        player.getWorld().playSound(playerLocation, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
    }

    private NPC getLookedAtClone(Player player, List<NPC> clones) {
        for (NPC clone : clones) {
            if (clone.isSpawned() && player.hasLineOfSight(clone.getEntity())) {
                return clone;
            }
        }
        return null;
    }

    @Override
    public void adjustAttributes(int quirkLevel) {
        this.maxClones = 3.0 + (quirkLevel - 1) * 0.75;
        this.cloneStatsMultiplier = 0.5 + (quirkLevel - 1) * 0.01;
        this.cloneCooldown = Math.max(5 * 1000L, 15 * 1000L - (quirkLevel - 1) * 500L);
        this.teleportCooldown = Math.max(5 * 1000L, 30 * 1000L - (quirkLevel - 1) * 300L);
    }

    private boolean isOnCooldown(Player player, Map<UUID, Long> cooldownMap, long cooldownTime) {
        UUID playerId = player.getUniqueId();
        if (!cooldownMap.containsKey(playerId)) return false;

        long lastUseTime = cooldownMap.get(playerId);
        return (System.currentTimeMillis() - lastUseTime) < cooldownTime;
    }

    private void startCooldown(Player player, Map<UUID, Long> cooldownMap) {
        cooldownMap.put(player.getUniqueId(), System.currentTimeMillis());
    }

    @Override
    public String name() {
        return "Clone";
    }
}
