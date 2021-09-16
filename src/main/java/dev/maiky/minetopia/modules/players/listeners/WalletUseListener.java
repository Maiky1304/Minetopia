package dev.maiky.minetopia.modules.players.listeners;

import dev.maiky.minetopia.modules.players.gui.WalletGUI;
import dev.maiky.minetopia.util.Items;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

/**
 * This project is owned by Maiky Perlee - © 2021
 */

public class WalletUseListener implements TerminableModule {

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {
        Events.subscribe(PlayerInteractEvent.class)
                .filter(PlayerInteractEvent::hasItem)
                .filter(e -> Items.hasNBT(e.getItem(), MakeSlotsUnusableListener.predicate))
                .filter(e -> e.getAction().toString().startsWith("RIGHT_CLICK"))
                .handler(e -> {
                    WalletGUI gui = new WalletGUI(e.getPlayer());
                    gui.open();
                }).bindWith(consumer);
    }

}
