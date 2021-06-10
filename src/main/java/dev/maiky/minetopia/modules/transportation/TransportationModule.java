package dev.maiky.minetopia.modules.transportation;

import co.aikar.commands.BukkitCommandManager;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.MinetopiaModule;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.PortalManager;
import dev.maiky.minetopia.modules.transportation.commands.TransportationCommand;
import dev.maiky.minetopia.modules.transportation.portal.LocalPortalData;
import dev.maiky.minetopia.modules.transportation.portal.Portal;
import dev.maiky.minetopia.modules.transportation.portal.PortalData;
import dev.maiky.minetopia.util.Configuration;
import dev.maiky.minetopia.util.SerializationUtils;
import lombok.Getter;
import me.lucko.helper.Events;
import me.lucko.helper.cooldown.Cooldown;
import me.lucko.helper.cooldown.CooldownMap;
import me.lucko.helper.terminable.composite.CompositeClosingException;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.concurrent.TimeUnit;

/**
 * Door: Maiky
 * Info: Minetopia - 25 May 2021
 * Package: dev.maiky.minetopia.modules.transportation
 */

public class TransportationModule implements MinetopiaModule {

	@Getter
	private final PortalManager portalManager;

	private final CompositeTerminable composite = CompositeTerminable.create();
	private boolean enabled = false;

	@Getter
	private final Configuration configuration;

	public TransportationModule() {
		this.configuration = new Configuration(Minetopia.getPlugin(Minetopia.class), "modules/transportation.yml");
		this.configuration.load();

		this.portalManager = PortalManager.with(Minetopia.getPlugin(Minetopia.class).dataModule.getSqlHelper());
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	@Override
	public void reload() {
		this.disable();
		this.enable();
	}

	@Override
	public void enable() {
		this.enabled = true;

		// Register Events
		this.registerEvents();

		// Register Commands
		this.registerCommands();
	}

	private void registerCommands() {
		Minetopia minetopia = Minetopia.getPlugin(Minetopia.class);
		BukkitCommandManager commandManager = minetopia.getCommandManager();

		commandManager.registerCommand(new TransportationCommand(this.configuration));
	}

	private final CooldownMap<Player> cooldownMap = CooldownMap.create(Cooldown.of(5, TimeUnit.SECONDS));

	private void registerEvents() {
		Events.subscribe(PlayerInteractEvent.class)
				.filter(PlayerInteractEvent::hasBlock)
				.filter(e -> e.getAction() == Action.PHYSICAL)
				.filter(e -> e.getClickedBlock().getType().toString().endsWith("PLATE"))
				.filter(e -> e.getClickedBlock().getRelative(BlockFace.UP).getType().equals(Material.WALL_SIGN))
				.handler(e -> {
					Sign sign = (Sign) e.getClickedBlock().getRelative(BlockFace.UP).getState();
					String name = ChatColor.stripColor(sign.getLine(1));

					PortalManager manager = PortalManager.with(DataModule.getInstance().getSqlHelper());
					ConfigurationSection section = this.configuration.get().getConfigurationSection(Portal.BUKKIT.toString());

					LocalPortalData data;
					if (!section.contains(name)) {
						PortalData portalData = manager.getPortalData(name);
						data = new LocalPortalData(SerializationUtils.deserialize(portalData.getLocation()), portalData.getServer());
					} else {
						data = new LocalPortalData((Location) section.get(name + ".location"), null);
					}

					if (!cooldownMap.test(e.getPlayer()))
						return;

					if (data.getServer() == null) {
						e.getPlayer().teleport(data.getLocation());
					} else {
						ByteArrayDataOutput outputStream = ByteStreams.newDataOutput();
						outputStream.writeUTF("Connect");
						outputStream.writeUTF(data.getServer());
					}

					e.getPlayer().sendMessage("§6Je wordt nu geteleporteerd naar §c" + name + "§6.");
				}).bindWith(composite);

		Events.subscribe(SignChangeEvent.class)
				.filter(e -> e.getLine(0).equalsIgnoreCase("[Portal]"))
				.handler(e -> {
					Portal type;
					try {
						type = Portal.valueOf(e.getLine(1).toUpperCase());
					} catch (IllegalArgumentException exception) {
						e.getBlock().breakNaturally();
						e.getPlayer().sendMessage("§cIncorrecte portal type!");
						return;
					}

					PortalManager manager = PortalManager.with(DataModule.getInstance().getSqlHelper());
					ConfigurationSection section = this.configuration.get().getConfigurationSection(type.toString());
					String name = e.getLine(2);

					if (type == Portal.BUKKIT && !section.contains(name)) {
						e.getBlock().breakNaturally();
						e.getPlayer().sendMessage("§cIncorrecte portal naam type!");
						return;
					}

					if (type == Portal.BUNGEECORD && !manager.getPortals().containsKey(name)) {
						e.getBlock().breakNaturally();
						e.getPlayer().sendMessage("§cIncorrecte portal naam type!");
						return;
					}

					Location location = type == Portal.BUNGEECORD ? SerializationUtils.deserialize(manager.getPortalData(name).getLocation()) : (Location) section.get(name + ".location");

					String line = String.format("%.0f;%.0f;%.0f", location.getX(), location.getY(), location.getZ());
					String line2 = String.format("%.0f;%.0f", location.getYaw(), location.getPitch());

					e.setLine(0, "§f[§2T§aeleporter§f]");
					e.setLine(1, name);
					e.setLine(2, line);
					e.setLine(3, line2);

					e.getPlayer().sendMessage("§6Success! Portal created!");
				}).bindWith(composite);
	}

	@Override
	public void disable() {
		this.enabled = false;

		try {
			this.composite.close();
		} catch (CompositeClosingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return "Transportation";
	}
}
