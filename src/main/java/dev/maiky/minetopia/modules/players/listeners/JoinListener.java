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

import co.aikar.commands.ConditionFailedException;
import dev.maiky.minetopia.modules.data.managers.PlayerManager;
import dev.maiky.minetopia.modules.levels.manager.LevelCheck;
import dev.maiky.minetopia.modules.players.classes.MinetopiaInventory;
import dev.maiky.minetopia.modules.players.classes.MinetopiaScoreboard;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import dev.maiky.minetopia.modules.transportation.portal.ILocation;
import dev.maiky.minetopia.modules.transportation.portal.LocalPortalData;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ConcurrentModificationException;
import java.util.concurrent.TimeUnit;

public class JoinListener implements TerminableModule {

	private final PlayerManager playerManager;

	public JoinListener(PlayerManager playerManager) {
		this.playerManager = playerManager;
	}

	@Override
	public void setup(@NotNull TerminableConsumer consumer) {
		Events.subscribe(PlayerJoinEvent.class)
				.filter(e -> !playerManager.exists(e.getPlayer().getUniqueId()))
				.handler(e -> {
					e.getPlayer().sendMessage("§6Je data wordt ingeladen...");

					Schedulers.sync().runLater(() ->
					{
						Player player = e.getPlayer();
						MinetopiaUser user = new MinetopiaUser(player.getUniqueId(), player.getName());
						playerManager.create(user);
						LevelCheck check = new LevelCheck(user);
						int points = check.calculatePoints();
						user.setLevelPoints(points);
						PlayerManager.getCache().put(user.getUuid(), user);

						MinetopiaScoreboard minetopiaScoreboard = new MinetopiaScoreboard(player);
						minetopiaScoreboard.initialize();
						PlayerManager.getScoreboard().put(player.getUniqueId(), minetopiaScoreboard);

						e.getPlayer().sendMessage("§6Je data is succesvol ingeladen!");
					}, 200, TimeUnit.MILLISECONDS);
				}).bindWith(consumer);
		Events.subscribe(PlayerJoinEvent.class)
				.filter(e -> playerManager.exists(e.getPlayer().getUniqueId()))
				.handler(e -> {
					e.getPlayer().sendMessage("§6Je data wordt ingeladen...");

					Schedulers.sync().runLater(() -> {
						Player player = e.getPlayer();
						MinetopiaUser user = playerManager.retrieve(player.getUniqueId());
						if (user == null) {
							player.kickPlayer("§cMinetopia: Oops! Something went wrong please contact a developer.");
							return;
						}
						LevelCheck check = new LevelCheck(user);
						int points = check.calculatePoints();
						user.setLevelPoints(points);
						PlayerManager.getCache().put(player.getUniqueId(), user);

						MinetopiaInventory.restore(player, user.getMinetopiaData().getInventory());
						player.setHealth(user.getMinetopiaData().getHp());
						player.setFoodLevel(user.getMinetopiaData().getSaturation());

						MinetopiaScoreboard minetopiaScoreboard = new MinetopiaScoreboard(player);
						minetopiaScoreboard.initialize();
						PlayerManager.getScoreboard().put(player.getUniqueId(), minetopiaScoreboard);

						LocalPortalData portalData = user.getPortalData();
						if (portalData != null) {
							try {
								ILocation iloc = portalData.getLocation();
								Location location = iloc.toBukkit();
								player.teleport(location);
							} catch (Exception ignored) {
							} finally {
								user.setPortalData(null);
							}
						}

						e.getPlayer().sendMessage("§6Je data is succesvol ingeladen!");
					}, 200, TimeUnit.MILLISECONDS);
				}).bindWith(consumer);
	}

}
