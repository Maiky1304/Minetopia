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

import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.data.managers.mongo.MongoPlayerManager;
import dev.maiky.minetopia.modules.levels.manager.LevelCheck;
import dev.maiky.minetopia.modules.players.classes.MinetopiaInventory;
import dev.maiky.minetopia.modules.players.classes.MinetopiaScoreboard;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import dev.maiky.minetopia.modules.transportation.portal.ILocation;
import dev.maiky.minetopia.modules.transportation.portal.LocalPortalData;
import dev.maiky.minetopia.util.Items;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class JoinListener implements TerminableModule {

	private final MongoPlayerManager playerManager;

	public JoinListener(MongoPlayerManager playerManager) {
		this.playerManager = playerManager;
	}

	@Override
	public void setup(@NotNull TerminableConsumer consumer) {
		Events.subscribe(PlayerJoinEvent.class)
				.filter(e -> !playerManager.find(user -> user.getUuid() == e.getPlayer().getUniqueId()).findFirst().isPresent())
				.handler(e -> {
					e.getPlayer().sendMessage("??6Je data wordt ingeladen...");

					Schedulers.sync().runLater(() ->
					{
						Player player = e.getPlayer();
						MinetopiaUser user = playerManager.create(player.getUniqueId(), player.getName());

						LevelCheck check = new LevelCheck(user);
						int points = check.calculatePoints();
						user.setLevelPoints(points);
						MongoPlayerManager.getCache().put(user.getUuid(), user);

						MinetopiaScoreboard minetopiaScoreboard = new MinetopiaScoreboard(player);
						minetopiaScoreboard.initialize();
						MongoPlayerManager.getScoreboard().put(player.getUniqueId(), minetopiaScoreboard);

						done(e);

						e.getPlayer().sendMessage("??6Je data is succesvol ingeladen!");
					}, 200, TimeUnit.MILLISECONDS);
				}).bindWith(consumer);
		Events.subscribe(PlayerJoinEvent.class)
				.filter(e -> playerManager.find(user -> user.getUuid() == e.getPlayer().getUniqueId()).findFirst().isPresent())
				.handler(e -> {
					e.getPlayer().sendMessage("??6Je data wordt ingeladen...");

					Schedulers.sync().runLater(() -> {
						Player player = e.getPlayer();
						MinetopiaUser user = playerManager.find(u -> u.getUuid() == player.getUniqueId()).findFirst()
								.orElse(null);
						if (user == null) {
							player.kickPlayer("??cMinetopia: Oops! Something went wrong please contact a developer.");
							return;
						}
						LevelCheck check = new LevelCheck(user);
						int points = check.calculatePoints();
						user.setLevelPoints(points);
						MongoPlayerManager.getCache().put(player.getUniqueId(), user);

						MinetopiaInventory.restore(player, user.getMinetopiaData().getInventory());
						player.setHealth(user.getMinetopiaData().getHp());
						player.setFoodLevel(user.getMinetopiaData().getSaturation());

						Economy economy = Minetopia.getEconomy();
						economy.withdrawPlayer(player, economy.getBalance(player));
						economy.depositPlayer(player, user.getMinetopiaData().getBalance());

						MinetopiaScoreboard minetopiaScoreboard = new MinetopiaScoreboard(player);
						minetopiaScoreboard.initialize();
						MongoPlayerManager.getScoreboard().put(player.getUniqueId(), minetopiaScoreboard);

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

						done(e);

						e.getPlayer().sendMessage("??6Je data is succesvol ingeladen!");
					}, 200, TimeUnit.MILLISECONDS);
				}).bindWith(consumer);
	}

	public void done(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		ItemStack itemStack = ItemStackBuilder
				.of(Material.GOLD_HOE)
				.name("&bPortemonnee")
				.lore("","&7Rechtermuisknop om iemand &bgeld &7te geven.",
						"&7Rechtermuisknop om je portemonnee te &bopenen&7.")
				.build();
		itemStack = Items.editNBT(itemStack, "mtcustom", "portemonnee");
		player.getInventory().setItem(8, itemStack);
	}

}
