package com.kzics.quirksmha.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {
    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = itemStack.getItemMeta();
    }

    // Changer le nom de l'item
    public ItemBuilder setName(Component name) {
        itemMeta.displayName(name);
        return this;
    }

    // Ajouter des lignes de description
    public ItemBuilder setLore(Component... lore) {
        List<Component> loreList = new ArrayList<>();
        for (Component line : lore) {
            loreList.add(line);
        }
        itemMeta.lore(loreList);
        return this;
    }

    public ItemBuilder setLore(List<Component> component){
        itemMeta.lore(component);

        return this;
    }

    // Ajouter un enchantement
    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        itemStack.addEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder addItemFlags() {
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);

        return this;
    }

    public ItemBuilder addUnsafeEnchantment(Enchantment enchantment, int level) {
        itemMeta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder addAttributeModifier(Attribute attribute, AttributeModifier modifier) {
        itemMeta.addAttributeModifier(attribute, modifier);
        return this;
    }

    public  ItemBuilder addPersistentData(NamespacedKey key, String value) {
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        dataContainer.set(key, PersistentDataType.STRING, value);
        return this;
    }

    public ItemBuilder setDamage(int damage) {
        if (itemMeta instanceof Damageable) {
            ((Damageable) itemMeta).setDamage(damage);
        }
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        itemMeta.setUnbreakable(unbreakable);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder setCustomModelData(int data) {
        itemMeta.setCustomModelData(data);
        return this;
    }


    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack as(Material material) {
        return new ItemStack(material);
    }

    public static ItemStack as(Material material, int amount) {
        return new ItemStack(material, amount);
    }

    public static List<ItemStack> as(Material... material) {
        List<ItemStack> items = new ArrayList<>();
        for (Material mat : material) {
            items.add(as(mat));
        }
        return items;
    }

    public static List<ItemStack> as(int amount, Material... material) {
        List<ItemStack> items = new ArrayList<>();
        for (Material mat : material) {
            items.add(as(mat, amount));
        }
        return items;
    }

    public static ItemStack as(Material material, Enchantment enchantment, int level) {
        ItemStack item = new ItemStack(material);
        item.addEnchantment(enchantment, level);
        return item;
    }

}
