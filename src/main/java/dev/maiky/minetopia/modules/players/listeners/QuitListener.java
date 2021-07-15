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
import dev.maiky.minetopia.modules.data.managers.PlayerManager;
import dev.maiky.minetopia.modules.players.classes.MinetopiaInventory;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class QuitListener implements TerminableModule {

	private final PlayerManager playerManager;

	public QuitListener(PlayerManager playerManager) {
		this.playerManager = playerManager;
	}

	@Override
	public void setup(@NotNull TerminableConsumer consumer) {
		Events.subscribe(PlayerQuitEvent.class)
				.filter(e -> PlayerManager.getCache().containsKey(e.getPlayer().getUniqueId()))
				.handler(e -> {
					MinetopiaUser user = PlayerManager.getCache().get(e.getPlayer().getUniqueId());
					user.getMinetopiaData().setInventory(MinetopiaInventory.of(e.getPlayer().getInventory()));
					user.getMinetopiaData().setHp(e.getPlayer().getHealth());
					user.getMinetopiaData().setSaturation(e.getPlayer().getFoodLevel());
					user.getMinetopiaData().setBalance(Minetopia.getEconomy().getBalance(e.getPlayer()));

					playerManager.update(user);
					PlayerManager.getCache().remove(e.getPlayer().getUniqueId());
				}).bindWith(consumer);
		Events.subscribe(PlayerQuitEvent.class)
				.filter(e -> PlayerManager.getScoreboard().containsKey(e.getPlayer().getUniqueId()))
				.handler(e -> {
					PlayerManager.getScoreboard().get(e.getPlayer().getUniqueId()).getPlayerBoard().delete();
					PlayerManager.getScoreboard().remove(e.getPlayer().getUniqueId());
				}).bindWith(consumer);
	}

}
