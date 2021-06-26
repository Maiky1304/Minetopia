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

package dev.maiky.minetopia.modules.chat.listeners;

import dev.maiky.minetopia.modules.colors.fonts.FontSet;
import dev.maiky.minetopia.modules.data.managers.PlayerManager;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import dev.maiky.minetopia.util.Message;
import dev.maiky.minetopia.util.Text;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

public class MainChatListener implements TerminableModule {

	@Override
	public void setup(@NotNull TerminableConsumer consumer) {
		Events.subscribe(AsyncPlayerChatEvent.class, EventPriority.HIGH)
				.filter(e -> PlayerManager.getCache().containsKey(e.getPlayer().getUniqueId()))
				.filter(e -> !PlayerManager.getCache().get(e.getPlayer().getUniqueId()).isPoliceChat())
				.filter(e -> !e.isCancelled())
				.handler(e -> {
					e.setMessage(e.getMessage().replaceAll("%", "%%"));

					e.getRecipients().clear();
					for (Player p : Bukkit.getOnlinePlayers()) {
						Location pLoc = p.getLocation();
						double distance = pLoc.distance(e.getPlayer().getLocation());
						if (distance <= 16)
							e.getRecipients().add(p);
					}

					MinetopiaUser user = PlayerManager.getCache().get(e.getPlayer().getUniqueId());
					e.setFormat(Message.CHAT_FORMAT.format(Text.colors("&" + user.getCurrentLevelColor().getColor()
									+ (user.getCurrentLevelColor().font ? FontSet.process("Level " +  user.getLevel()) : "Level " +  user.getLevel())),
							Text.colors("&" + user.getCurrentPrefixColor().getColor()),
							user.getCurrentPrefixColor().font ? FontSet.process(user.getCurrentPrefix()) : user.getCurrentPrefix(), String.format("§%s", user.getCityColor()),
							e.getPlayer().getName(), Text.colors("&" + user.getCurrentChatColor().getColor()), user.getCurrentChatColor()
									.font ? FontSet.process(e.getMessage()) : e.getMessage()));
				}).bindWith(consumer);
	}

}
