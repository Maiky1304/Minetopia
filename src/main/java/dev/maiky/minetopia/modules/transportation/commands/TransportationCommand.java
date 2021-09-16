package dev.maiky.minetopia.modules.transportation.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.mongo.MongoPortalManager;
import dev.maiky.minetopia.modules.transportation.portal.ILocation;
import dev.maiky.minetopia.modules.transportation.portal.Portal;
import dev.maiky.minetopia.modules.transportation.portal.PortalData;
import dev.maiky.minetopia.util.Configuration;
import dev.maiky.minetopia.util.Message;
import dev.maiky.minetopia.util.Text;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Objects;

/**
 * Door: Maiky
 * Info: Minetopia - 25 May 2021
 * Package: dev.maiky.minetopia.modules.transportation.commands
 */

@CommandAlias("transportation|portal|portals|teleporter|teleporters")
@Description("Manage portals")
@CommandPermission("minetopia.admin.transportation")
public class TransportationCommand extends BaseCommand {

	@Getter
	private final Configuration configuration;
	private final MongoPortalManager manager;

	public TransportationCommand(Configuration configuration) {
		this.configuration = configuration;
		this.manager = DataModule.getInstance().getPortalManager();
	}

	@Default
	@HelpCommand
	public void onHelp(CommandSender sender) {
		Minetopia.showHelp(sender, this, getSubCommands());
	}

	@CatchUnknown
	public void onUnknown(CommandSender sender) {
		sender.sendMessage(Message.COMMON_COMMAND_UNKNOWNSUBCOMMAND.raw());
		this.onHelp(sender);
	}

	@Override
	public void showSyntax(CommandIssuer issuer, RegisteredCommand<?> cmd) {
		issuer.sendMessage(Message.COMMON_COMMAND_SYNTAX.format(getExecCommandLabel(), cmd.getPrefSubCommand(), cmd.getSyntaxText()));
	}

	@Subcommand("create")
	@Syntax("<portaltype> <name>")
	@Description("Create a portal")
	public void onCreate(Player player, Portal portal, String name) {
		ConfigurationSection section = this.configuration.get().getConfigurationSection(portal.toString());

		if (portal == Portal.BUKKIT && section.contains(name))
			throw new ConditionFailedException(Message.PORTALS_ERROR_ALREADYEXIST.raw());
		if (portal == Portal.BUNGEECORD && manager.getPortals().containsKey(name))
			throw new ConditionFailedException(Message.PORTALS_ERROR_ALREADYEXIST.raw());

		Location location = player.getLocation();

		if (portal == Portal.BUNGEECORD) {
			manager.createPortal(name, location, "unknown");
		} else {
			section.set(name + ".location", location);
			this.configuration.save();
		}

		player.sendMessage(Message.PORTALS_SUCCESS_CREATED.format(name, portal.toString()));
	}

	@Subcommand("delete")
	@Syntax("<portaltype> <name>")
	@Description("Delete a portal")
	public void onDelete(Player player, Portal portal, String name) {
		ConfigurationSection section = this.configuration.get().getConfigurationSection(portal.toString());
		PortalData data = manager.find(d -> d.name.equals(name)).findFirst().orElse(null);

		if (portal == Portal.BUKKIT && !section.contains(name))
			throw new ConditionFailedException(Message.PORTALS_ERROR_DOESNTEXIST.raw());
		if (portal == Portal.BUNGEECORD && data == null)
			throw new ConditionFailedException(Message.PORTALS_ERROR_DOESNTEXIST.raw());

		if (portal == Portal.BUKKIT) {
			section.set(name, null);
			this.configuration.save();
		} else {
			manager.delete(data);
		}

		player.sendMessage(Message.PORTALS_SUCCESS_DELETED.format(name, portal.toString()));
	}

	@Subcommand("list")
	@Syntax("<portaltype>")
	@Description("See all the portals")
	public void onList(Player player, Portal portal) {
		ConfigurationSection section = this.configuration.get().getConfigurationSection(portal.toString());

		String divider = Message.PORTALS_LIST_DIVIDER.raw();
		String message = Message.PORTALS_LIST_ENTRY.raw();

		player.sendMessage(divider);
		if (portal == Portal.BUKKIT) {
			for (String portalName : section.getKeys(false)) {
				Location location = (Location) section.get(portalName);
				String loc = String.format("%.1f, %.1f, %.1f, %s",
						location.getX(), location.getY(), location.getZ(), location.getWorld().getName());
				player.sendMessage(String.format(Text.colors(message), portalName, loc));
			}
		} else {
			HashMap<String, PortalData> portalDataHashMap = manager.getPortals();
			for (String portalName : portalDataHashMap.keySet()) {
				PortalData data = portalDataHashMap.get(portalName);
				Location location = data.getLocation().toBukkit();
				String loc = String.format("%.1f, %.1f, %.1f, %s (Server: %s)",
						location.getX(), location.getY(), location.getZ(), location.getWorld().getName(), data.getServer());
				player.sendMessage(String.format(Text.colors(message), portalName, loc));
			}
		}
		player.sendMessage(divider);
	}

	@Subcommand("setlocation")
	@Syntax("<portaltype> <name>")
	@Description("Change the location of a portal")
	public void onSetLocation(Player player, Portal portal, String name) {
		ConfigurationSection section = this.configuration.get().getConfigurationSection(Portal.BUKKIT.toString());

		PortalData data = manager.find(d -> d.name.equals(name)).findFirst().orElse(null);

		if (portal == Portal.BUKKIT && !section.contains(name))
			throw new ConditionFailedException(Message.PORTALS_ERROR_DOESNTEXIST.raw());
		if (portal == Portal.BUNGEECORD && data == null)
			throw new ConditionFailedException(Message.PORTALS_ERROR_DOESNTEXIST.raw());

		if (portal == Portal.BUKKIT) {
			section.set(name + ".location", player.getLocation());
			this.configuration.save();
		} else {
			Objects.requireNonNull(data).setLocation(ILocation.from(player.getLocation()));
			manager.save(data);
		}

		player.sendMessage(Message.PORTALS_SUCCESS_CHANGEDLOCATION.format(name, portal.toString()));
	}

	@Subcommand("teleport")
	@Syntax("<portaltype> <name>")
	@Description("Teleport to the location of a portal")
	public void onTeleport(Player player, Portal portal, String name) {
		ConfigurationSection section = this.configuration.get().getConfigurationSection(Portal.BUKKIT.toString());
		PortalData data = manager.find(d -> d.name.equals(name)).findFirst().orElse(null);

		if (portal == Portal.BUKKIT && !section.contains(name))
			throw new ConditionFailedException(Message.PORTALS_ERROR_DOESNTEXIST.raw());
		if (portal == Portal.BUNGEECORD && !manager.getPortals().containsKey(name))
			throw new ConditionFailedException(Message.PORTALS_ERROR_DOESNTEXIST.raw());

		ILocation location;
		if (portal == Portal.BUKKIT) {
			location = ILocation.from((Location) section.get(name + ".location"));
		} else {
			assert data != null;
			location = data.getLocation();
		}

		if (player.getWorld().getName().equals(location.getWorldName())) {
			player.teleport(location.toBukkit());
		} else {
			// TODO: Replicate use of standard way of transporting A to B
		}

		player.sendMessage(Message.PORTALS_SUCCESS_TELEPORT.format(name));
	}

	@Subcommand("setserver")
	@Syntax("<name> <server>")
	@Description("Change the location of a portal")
	public void onSetLocation(Player player, String name, String server) {
		PortalData data = manager.find(d -> d.name.equals(name)).findFirst().orElse(null);

		if (data == null)
			throw new ConditionFailedException(Message.PORTALS_ERROR_DOESNTEXIST.raw());

		data.setServer(server);
		manager.save(data);

		player.sendMessage(Message.PORTALS_SUCCESS_CHANGEDSERVER.format(name, server));
	}

}
