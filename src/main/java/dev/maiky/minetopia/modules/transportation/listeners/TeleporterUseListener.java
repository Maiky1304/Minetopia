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

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.PlayerManager;
import dev.maiky.minetopia.modules.data.managers.PortalManager;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import dev.maiky.minetopia.modules.transportation.TransportationModule;
import dev.maiky.minetopia.modules.transportation.portal.ILocation;
import dev.maiky.minetopia.modules.transportation.portal.LocalPortalData;
import dev.maiky.minetopia.modules.transportation.portal.Portal;
import dev.maiky.minetopia.modules.transportation.portal.PortalData;
import dev.maiky.minetopia.util.Configuration;
import dev.maiky.minetopia.util.Message;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public class TeleporterUseListener implements TerminableModule {

	private Configuration configuration;

	public TeleporterUseListener(Configuration configuration) {
		this.configuration = configuration;
	}

	@Override
	public void setup(@NotNull TerminableConsumer consumer) {
		Events.subscribe(PlayerInteractEvent.class)
				.filter(PlayerInteractEvent::hasBlock)
				.filter(e -> e.getAction() == Action.PHYSICAL)
				.filter(e -> e.getClickedBlock().getType().toString().endsWith("PLATE"))
				.filter(e -> e.getClickedBlock().getRelative(BlockFace.UP).getType().equals(Material.WALL_SIGN))
				.filter(e -> PlayerManager.getCache().containsKey(e.getPlayer().getUniqueId()))
				.handler(e -> {
					Sign sign = (Sign) e.getClickedBlock().getRelative(BlockFace.UP).getState();
					String name = ChatColor.stripColor(sign.getLine(1));

					PortalManager manager = PortalManager.with(DataModule.getInstance().getSqlHelper());
					ConfigurationSection section = this.configuration.get().getConfigurationSection(Portal.BUKKIT.toString());

					LocalPortalData data;
					if (!section.contains(name)) {
						PortalData portalData = manager.getPortalData(name);
						data = new LocalPortalData(portalData.getLocation(), portalData.getServer());
					} else {
						data = new LocalPortalData(ILocation.from((Location) section.get(name + ".location")), null);
					}

					if (!TransportationModule.getCooldownMap().test(e.getPlayer()))
						return;

					if (data.getServer() == null) {
						e.getPlayer().teleport(data.getLocation().toBukkit());
					} else {
						MinetopiaUser user = PlayerManager.getCache().get(e.getPlayer().getUniqueId());
						user.setPortalData(data);

						ByteArrayDataOutput outputStream = ByteStreams.newDataOutput();
						outputStream.writeUTF("Connect");
						outputStream.writeUTF(data.getServer());
						e.getPlayer().sendPluginMessage(Minetopia.getInstance(), "BungeeCord", outputStream.toByteArray());
					}

					if (data.getServer() != null) {
						e.getPlayer().sendMessage(Message.PORTALS_SUCCESS_USEPORTAL_BUNGEE.format(name));
					} else {
						e.getPlayer().sendMessage(Message.PORTALS_SUCCESS_USEPORTAL_LOCAL.format(name));
					}
				}).bindWith(consumer);
	}

}
