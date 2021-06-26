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

package dev.maiky.minetopia.modules.notifications.listeners.impl;

import dev.maiky.minetopia.modules.notifications.notifications.Notification;
import dev.maiky.minetopia.modules.notifications.notifications.NotificationQueue;
import dev.maiky.minetopia.util.Numbers;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import net.ess3.api.events.UserBalanceUpdateEvent;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

public class BalanceUpdateListener implements TerminableModule {

	@Override
	public void setup(@NotNull TerminableConsumer consumer) {
		Events.subscribe(UserBalanceUpdateEvent.class)
				.handler(e -> {
					String amount = Numbers.convert(Numbers.Type.MONEY, e.getNewBalance().doubleValue() - e.getOldBalance().doubleValue());
					ChatColor color = amount.contains("-") ? ChatColor.RED : ChatColor.GREEN;
					NotificationQueue queue = NotificationQueue.getQueueCache().get(e.getPlayer().getUniqueId());
					Notification notification = new Notification(e.getPlayer(), color + (amount.contains("-") ? "-" : "+") +
							Numbers.convert(Numbers.Type.MONEY, Math.abs(e.getNewBalance().doubleValue() - e.getOldBalance().doubleValue())), 2);
					queue.queue.add(notification);
				}).bindWith(consumer);
	}

}
