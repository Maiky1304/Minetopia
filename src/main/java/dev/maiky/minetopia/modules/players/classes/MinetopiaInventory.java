package dev.maiky.minetopia.modules.players.classes;

import dev.maiky.minetopia.util.SerializationUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Objects;

/**
 * This project is owned by Maiky Perlee - © 2021
 */

public class MinetopiaInventory {

    private @Getter @Setter String inventory;
    private @Getter @Setter String armor;
    private @Getter @Setter String offhand;
    private @Getter @Setter int lastHeldItemSlot;

    public MinetopiaInventory(String inventory, String armor, String offhand, int lastHeldItemSlot) {
        this.inventory = inventory;
        this.armor = armor;
        this.offhand = offhand;
        this.lastHeldItemSlot = lastHeldItemSlot;
    }

    public static void restore(Player player, MinetopiaInventory inventory) {
        ItemStack[] array = SerializationUtils.itemStackArrayFromBase64(inventory.getInventory());
        ItemStack[] armor = SerializationUtils.itemStackArrayFromBase64(inventory.getArmor());
        ItemStack offhand = Objects.requireNonNull(SerializationUtils.itemStackArrayFromBase64(inventory.getOffhand()))[0];
        int heldSlot = inventory.getLastHeldItemSlot();
        player.getInventory().setContents(array);
        player.getInventory().setArmorContents(armor);
        player.getInventory().setItemInOffHand(offhand);
        player.getInventory().setHeldItemSlot(heldSlot);
        player.updateInventory();
    }

    public static MinetopiaInventory empty() {
        return null;
    }

    public static MinetopiaInventory of(PlayerInventory inventory) {
        String inv = SerializationUtils.toBase64(inventory);
        String armor = SerializationUtils.itemStackArrayToBase64(inventory.getArmorContents());
        String offhand = SerializationUtils.itemStackArrayToBase64(new ItemStack[]{inventory.getItemInOffHand()});
        int lastHeld = inventory.getHeldItemSlot();
        return new MinetopiaInventory(inv, armor, offhand, lastHeld);
    }

}
