package dev.maiky.minetopia.modules.transportation.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import com.google.gson.Gson;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.PortalManager;
import dev.maiky.minetopia.modules.transportation.portal.ILocation;
import dev.maiky.minetopia.modules.transportation.portal.Portal;
import dev.maiky.minetopia.modules.transportation.portal.PortalData;
import dev.maiky.minetopia.util.Configuration;
import dev.maiky.minetopia.util.Message;
import dev.maiky.minetopia.util.SerializationUtils;
import dev.maiky.minetopia.util.Text;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;

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

	public TransportationCommand(Configuration configuration) {
		this.configuration = configuration;
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
		PortalManager manager = PortalManager.with(DataModule.getInstance().getSqlHelper());

		if (portal == Portal.BUKKIT && section.contains(name))
			throw new ConditionFailedException(Message.PORTALS_ERROR_ALREADYEXIST.raw());
		if (portal == Portal.BUNGEECORD && manager.getPortals().containsKey(name))
			throw new ConditionFailedException(Message.PORTALS_ERROR_ALREADYEXIST.raw());

		Location location = player.getLocation();

		if (portal == Portal.BUNGEECORD) {
			PortalData data = new PortalData(ILocation.from(location), "unknown");
			manager.insertPortal(name, data);
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
		PortalManager manager = PortalManager.with(DataModule.getInstance().getSqlHelper());

		if (portal == Portal.BUKKIT && !section.contains(name))
			throw new ConditionFailedException(Message.PORTALS_ERROR_DOESNTEXIST.raw());
		if (portal == Portal.BUNGEECORD && !manager.getPortals().containsKey(name))
			throw new ConditionFailedException(Message.PORTALS_ERROR_DOESNTEXIST.raw());

		if (portal == Portal.BUKKIT) {
			section.set(name, null);
			this.configuration.save();
		} else {
			manager.deletePortal(name);
		}

		player.sendMessage(Message.PORTALS_SUCCESS_DELETED.format(name, portal.toString()));
	}

	@Subcommand("list")
	@Syntax("<portaltype>")
	@Description("See all the portals")
	public void onList(Player player, Portal portal) {
		ConfigurationSection section = this.configuration.get().getConfigurationSection(portal.toString());
		PortalManager manager = PortalManager.with(DataModule.getInstance().getSqlHelper());

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
		PortalManager manager = PortalManager.with(DataModule.getInstance().getSqlHelper());

		if (portal == Portal.BUKKIT && !section.contains(name))
			throw new ConditionFailedException(Message.PORTALS_ERROR_DOESNTEXIST.raw());
		if (portal == Portal.BUNGEECORD && !manager.getPortals().containsKey(name))
			throw new ConditionFailedException(Message.PORTALS_ERROR_DOESNTEXIST.raw());

		if (portal == Portal.BUKKIT) {
			section.set(name + ".location", player.getLocation());
			this.configuration.save();
		} else {
			manager.updatePortal(name, new PortalData(ILocation.from(player.getLocation()), manager.getPortalData(name).getServer()));
		}

		player.sendMessage(Message.PORTALS_SUCCESS_CHANGEDLOCATION.format(name, portal.toString()));
	}

	@Subcommand("teleport")
	@Syntax("<portaltype> <name>")
	@Description("Teleport to the location of a portal")
	public void onTeleport(Player player, Portal portal, String name) {
		ConfigurationSection section = this.configuration.get().getConfigurationSection(Portal.BUKKIT.toString());
		PortalManager manager = PortalManager.with(DataModule.getInstance().getSqlHelper());

		if (portal == Portal.BUKKIT && !section.contains(name))
			throw new ConditionFailedException(Message.PORTALS_ERROR_DOESNTEXIST.raw());
		if (portal == Portal.BUNGEECORD && !manager.getPortals().containsKey(name))
			throw new ConditionFailedException(Message.PORTALS_ERROR_DOESNTEXIST.raw());

		ILocation location;
		if (portal == Portal.BUKKIT) {
			location = ILocation.from((Location) section.get(name + ".location"));
		} else {
			location = manager.getPortalData(name).getLocation();
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
		PortalManager manager = PortalManager.with(DataModule.getInstance().getSqlHelper());

		if (!manager.getPortals().containsKey(name))
			throw new ConditionFailedException(Message.PORTALS_ERROR_DOESNTEXIST.raw());

		PortalData data = new PortalData(ILocation.from(player.getLocation()), server);
		manager.updatePortal(name, data);

		player.sendMessage(Message.PORTALS_SUCCESS_CHANGEDSERVER.format(name, server));
	}

}
