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

package dev.maiky.minetopia.modules.districts.listeners;

import dev.maiky.minetopia.modules.data.managers.PlayerManager;
import dev.maiky.minetopia.modules.districts.DistrictsModule;
import dev.maiky.minetopia.modules.players.PlayersModule;
import dev.maiky.minetopia.util.Text;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

public class MovementListener implements TerminableModule {

	@Override
	public void setup(@NotNull TerminableConsumer consumer) {
		Events.subscribe(PlayerMoveEvent.class)
				.filter(e -> e.getFrom().distanceSquared(e.getFrom()) >= 0)
				.handler(e -> {
					String current = DistrictsModule.getCurrentLocation(e.getPlayer());
					if (!current.equals(DistrictsModule.getLocationCache().get(e.getPlayer().getUniqueId()))) {
						DistrictsModule.getLocationCache().put(e.getPlayer().getUniqueId(), current);
						PlayerManager.getScoreboard().get(e.getPlayer().getUniqueId()).initialize();
						String color = DistrictsModule.getLocationCache().get(e.getPlayer().getUniqueId()) == null ? PlayersModule.getInstance().getCityColor()
								: DistrictsModule.getBlockCache().get(DistrictsModule.getLocationCache().get(e.getPlayer().getUniqueId()));
						e.getPlayer().sendTitle(Text.colors("&"
								+ color + "Welkom in"), Text.colors("&" + color
								+ current), 20, 50, 20);
					}
				}).bindWith(consumer);
	}

}
