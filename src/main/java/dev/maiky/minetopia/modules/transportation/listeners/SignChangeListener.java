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

package dev.maiky.minetopia.modules.transportation.listeners;

import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.PortalManager;
import dev.maiky.minetopia.modules.transportation.portal.Portal;
import dev.maiky.minetopia.util.Configuration;
import dev.maiky.minetopia.util.Message;
import dev.maiky.minetopia.util.Options;
import dev.maiky.minetopia.util.SerializationUtils;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.block.SignChangeEvent;
import org.jetbrains.annotations.NotNull;

public class SignChangeListener implements TerminableModule {

	private Configuration configuration;

	public SignChangeListener(Configuration configuration) {
		this.configuration = configuration;
	}

	@Override
	public void setup(@NotNull TerminableConsumer consumer) {
		Events.subscribe(SignChangeEvent.class)
				.filter(e -> e.getLine(0).equalsIgnoreCase("[Portal]"))
				.handler(e -> {
					Portal type;
					try {
						type = Portal.valueOf(e.getLine(1).toUpperCase());
					} catch (IllegalArgumentException exception) {
						e.getBlock().breakNaturally();
						e.getPlayer().sendMessage(Message.PORTALS_ERROR_PORTALTYPE.raw());
						return;
					}

					PortalManager manager = PortalManager.with(DataModule.getInstance().getSqlHelper());
					ConfigurationSection section = this.configuration.get().getConfigurationSection(type.toString());
					String name = e.getLine(2);

					if (type == Portal.BUKKIT && !section.contains(name)) {
						e.getBlock().breakNaturally();
						e.getPlayer().sendMessage(Message.PORTALS_ERROR_PORTALTYPE.raw());
						return;
					}

					if (type == Portal.BUNGEECORD && !manager.getPortals().containsKey(name)) {
						e.getBlock().breakNaturally();
						e.getPlayer().sendMessage(Message.PORTALS_ERROR_PORTALTYPE.raw());
						return;
					}

					Location location = type == Portal.BUNGEECORD ? manager.getPortalData(name).getLocation().toBukkit() : (Location) section.get(name + ".location");

					String line = String.format("%.0f;%.0f;%.0f", location.getX(), location.getY(), location.getZ());
					String line2 = String.format("%.0f;%.0f", location.getYaw(), location.getPitch());

					e.setLine(0, Options.PORTALS_SIGNTAG.asString().get());
					e.setLine(1, name);
					e.setLine(2, line);
					e.setLine(3, line2);

					e.getPlayer().sendMessage(Message.PORTALS_SUCCESS_SIGNCREATED.raw());
				}).bindWith(consumer);
	}

}
