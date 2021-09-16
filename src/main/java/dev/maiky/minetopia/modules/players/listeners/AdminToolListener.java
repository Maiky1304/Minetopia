/*
 * This file is part of Minetopia.
 *
 *  Copyright (c) Maiky1304 (Maiky) <maiky@blackmt.nl>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package dev.maiky.minetopia.modules.players.listeners;

import dev.maiky.minetopia.modules.data.managers.mongo.MongoPlayerManager;
import dev.maiky.minetopia.modules.players.ui.AdminToolUI;
import dev.maiky.minetopia.util.Message;
import me.lucko.helper.Events;
import me.lucko.helper.cooldown.Cooldown;
import me.lucko.helper.cooldown.CooldownMap;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class AdminToolListener implements TerminableModule {

	private final CooldownMap<Player> cooldownMap;

	public AdminToolListener() {
		this.cooldownMap = CooldownMap.create(Cooldown.of(500, TimeUnit.MILLISECONDS));
	}

	@Override
	public void setup(@NotNull TerminableConsumer consumer) {
		Events.subscribe(PlayerInteractAtEntityEvent.class)
				.filter(e -> MongoPlayerManager.getCache().containsKey(e.getPlayer().getUniqueId()))
				.filter(e -> e.getHand().equals(EquipmentSlot.HAND))
				.filter(e -> e.getPlayer().getInventory().getItemInMainHand().getType() == Material.NETHER_STAR)
				.filter(e -> cooldownMap.test(e.getPlayer()))
				.handler(e -> {
					e.getPlayer().sendMessage(Message.PLAYER_ADMINTOOL_OTHER.format(e.getRightClicked().getName()));
					AdminToolUI adminToolUI = new AdminToolUI(e.getPlayer(), Bukkit.getOfflinePlayer(e.getRightClicked().getUniqueId()));
					adminToolUI.open();
				}).bindWith(consumer);
		Events.subscribe(PlayerInteractEvent.class)
				.filter(e -> MongoPlayerManager.getCache().containsKey(e.getPlayer().getUniqueId()))
				.filter(PlayerInteractEvent::hasItem)
				.filter(e -> e.getHand().equals(EquipmentSlot.HAND))
				.filter(e -> e.getItem().getType() == Material.NETHER_STAR)
				.filter(e -> e.getAction().toString().startsWith("RIGHT"))
				.filter(e -> cooldownMap.test(e.getPlayer()))
				.handler(e -> {
					e.getPlayer().sendMessage(Message.PLAYER_ADMINTOOL_SELF.raw());
					AdminToolUI adminToolUI = new AdminToolUI(e.getPlayer(), Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()));
					adminToolUI.open();
				}).bindWith(consumer);
	}

}
