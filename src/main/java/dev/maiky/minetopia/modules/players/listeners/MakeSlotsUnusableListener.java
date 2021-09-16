package dev.maiky.minetopia.modules.players.listeners;

import dev.maiky.minetopia.util.Items;
import me.lucko.helper.Events;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * This project is owned by Maiky Perlee - © 2021
 */

public class MakeSlotsUnusableListener implements TerminableModule {

    public static final Predicate<NBTTagCompound> predicate = nbt -> nbt.hasKey("mtcustom") && nbt.getString("mtcustom").equals("portemonnee");

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {
        Events.subscribe(InventoryClickEvent.class)
                .filter(e -> e.getClickedInventory().getType() == InventoryType.PLAYER)
                .filter(e -> e.getSlot() == 8 || e.getSlot() == 7)
                .handler(e -> e.setCancelled(true)).bindWith(consumer);
        Events.subscribe(PlayerDropItemEvent.class)
                .filter(e -> Items.hasNBT(e.getItemDrop().getItemStack(), predicate))
                .handler(e -> e.setCancelled(true)).bindWith(consumer);
        Events.subscribe(PlayerDeathEvent.class)
                .handler(e -> e.getDrops().removeIf(itemStack -> Items.hasNBT(itemStack, predicate)))
                .bindWith(consumer);
        Events.subscribe(PlayerRespawnEvent.class)
                .handler(e -> {
                    ItemStack itemStack = ItemStackBuilder
                            .of(Material.GOLD_HOE)
                            .name("&bPortemonnee")
                            .lore("","&7Rechtermuisknop om iemand &bgeld &7te geven.",
                                    "&7Rechtermuisknop om je portemonnee te &bopenen&7.")
                            .build();
                    itemStack = Items.editNBT(itemStack, "mtcustom", "portemonnee");
                    e.getPlayer().getInventory().setItem(8, itemStack);
                }).bindWith(consumer);
        Events.subscribe(PlayerSwapHandItemsEvent.class)
                .filter(e -> Items.hasNBT(e.getOffHandItem(), predicate) || Items.hasNBT(e.getMainHandItem(), predicate))
                .handler(e -> e.setCancelled(true))
                .bindWith(consumer);
    }

}
