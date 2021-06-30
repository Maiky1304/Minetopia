package dev.maiky.minetopia.modules.players.listeners.impl;

import dev.maiky.minetopia.modules.data.managers.PlayerManager;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

/**
 * This project is owned by Maiky Perlee - © 2021
 */

public class LoadingListener implements TerminableModule {

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {
        Events.subscribe(PlayerDropItemEvent.class)
                .filter(e -> !PlayerManager.getCache().containsKey(e.getPlayer().getUniqueId()))
                .handler(e -> e.setCancelled(true)).bindWith(consumer);
        Events.subscribe(PlayerMoveEvent.class)
                .filter(e -> !PlayerManager.getCache().containsKey(e.getPlayer().getUniqueId()))
                .handler(e -> {
                    if (e.getFrom().distanceSquared(e.getTo()) > 0.0D) {
                        e.getPlayer().teleport(e.getFrom());
                    }
                }).bindWith(consumer);
        Events.subscribe(InventoryClickEvent.class)
                .filter(e -> !PlayerManager.getCache().containsKey(e.getWhoClicked().getUniqueId()))
                .handler(e -> e.setCancelled(true)).bindWith(consumer);
        Events.subscribe(AsyncPlayerChatEvent.class)
                .filter(e -> !PlayerManager.getCache().containsKey(e.getPlayer().getUniqueId()))
                .handler(e -> e.setCancelled(true)).bindWith(consumer);
    }

}
