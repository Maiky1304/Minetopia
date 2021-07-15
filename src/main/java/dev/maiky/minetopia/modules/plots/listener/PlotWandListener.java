package dev.maiky.minetopia.modules.plots.listener;

import dev.maiky.minetopia.modules.plots.classes.Selection;
import dev.maiky.minetopia.modules.transportation.portal.ILocation;
import lombok.Getter;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.WeakHashMap;

/**
 * This project is owned by Maiky Perlee - © 2021
 */

public class PlotWandListener implements TerminableModule {

    private static final @Getter WeakHashMap<Player, Selection> hashMap = new WeakHashMap<>();

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {
        Events.subscribe(PlayerInteractEvent.class)
                .filter(e -> e.getAction().toString().endsWith("CLICK_BLOCK"))
                .filter(PlayerInteractEvent::hasItem)
                .filter(e -> e.getItem().getType() == Material.STICK)
                .handler(e -> {
                    e.setCancelled(true);

                   if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                       if (!hashMap.containsKey(e.getPlayer())) {
                           hashMap.put(e.getPlayer(), new Selection(ILocation.from(e.getClickedBlock().getLocation()), null));
                       } else {
                           Selection selection = hashMap.get(e.getPlayer());
                           selection.setPos1(ILocation.from(e.getClickedBlock().getLocation()));
                       }

                       e.getPlayer().sendMessage("§3Positie #1 gezet!");
                   } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                       if (!hashMap.containsKey(e.getPlayer())) {
                           hashMap.put(e.getPlayer(), new Selection(null, ILocation.from(e.getClickedBlock().getLocation())));
                       } else {
                           Selection selection = hashMap.get(e.getPlayer());
                           selection.setPos2(ILocation.from(e.getClickedBlock().getLocation()));
                       }

                       e.getPlayer().sendMessage("§3Positie #2 gezet!");
                   }
                }).bindWith(consumer);
    }

}
