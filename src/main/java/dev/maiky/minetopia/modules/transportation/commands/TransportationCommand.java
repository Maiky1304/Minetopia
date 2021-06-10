package dev.maiky.minetopia.modules.transportation.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.PortalManager;
import dev.maiky.minetopia.modules.transportation.portal.Portal;
import dev.maiky.minetopia.modules.transportation.portal.PortalData;
import dev.maiky.minetopia.util.Configuration;
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

	@HelpCommand
	@Subcommand("main")
	@Description("Read the help message")
	public void onHelp(CommandSender sender) {
		Minetopia.showHelp(sender, this, getSubCommands());
	}

	@Override
	public void showSyntax(CommandIssuer issuer, RegisteredCommand<?> cmd) {
		issuer.sendMessage("§cGebruik: /" + this.getExecCommandLabel() + " " +
				cmd.getPrefSubCommand() + " " +
				cmd.getSyntaxText());
	}

	@CatchUnknown
	public void onUnknown(CommandSender sender) {
		sender.sendMessage("§cUnknown subcommand");
		this.onHelp(sender);
	}

	@Subcommand("create")
	@Syntax("<portaltype> <name>")
	@Description("Create a portal")
	public void onCreate(Player player, Portal portal, String name) {
		ConfigurationSection section = this.configuration.get().getConfigurationSection(portal.toString());
		PortalManager manager = PortalManager.with(DataModule.getInstance().getSqlHelper());

		if (portal == Portal.BUKKIT && section.contains(name))
			throw new ConditionFailedException("Er bestaat al een portal met deze naam onder deze categorie.");
		if (portal == Portal.BUNGEECORD && manager.getPortals().containsKey(name))
			throw new ConditionFailedException("Er bestaat al een portal met deze naam onder deze categorie.");

		Location location = player.getLocation();

		if (portal == Portal.BUNGEECORD) {
			PortalData data = new PortalData(location, "unknown");
			manager.insertPortal(name, data);
		} else {
			section.set(name + ".location", location);
			this.configuration.save();
		}

		String message = "&6Success! Created a portal with the name &c%s &6in the category &c%s &6on your &ccurrent &6location.";
		player.sendMessage(String.format(Text.colors(message), name, portal.toString()));
	}

	@Subcommand("delete")
	@Syntax("<portaltype> <name>")
	@Description("Delete a portal")
	public void onDelete(Player player, Portal portal, String name) {
		ConfigurationSection section = this.configuration.get().getConfigurationSection(portal.toString());
		PortalManager manager = PortalManager.with(DataModule.getInstance().getSqlHelper());

		if (portal == Portal.BUKKIT && !section.contains(name))
			throw new ConditionFailedException("Er bestaat geen portal met deze naam onder deze categorie.");
		if (portal == Portal.BUNGEECORD && !manager.getPortals().containsKey(name))
			throw new ConditionFailedException("Er bestaat geen portal met deze naam onder deze categorie.");

		if (portal == Portal.BUKKIT) {
			section.set(name, null);
			this.configuration.save();
		} else {
			manager.deletePortal(name);
		}

		String message = "&6Success! Deleted a portal with the name &c%s &6in the category &c%s&6.";
		player.sendMessage(String.format(Text.colors(message), name, portal.toString()));
	}

	@Subcommand("list")
	@Syntax("<portaltype>")
	@Description("See all the portals")
	public void onList(Player player, Portal portal) {
		ConfigurationSection section = this.configuration.get().getConfigurationSection(portal.toString());
		PortalManager manager = PortalManager.with(DataModule.getInstance().getSqlHelper());

		String divider = "§6§m----------------------------------------------------";
		String message = "&c- &6%s (&c%s&6)";

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
				Location location = SerializationUtils.deserialize(data.getLocation());
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
			throw new ConditionFailedException("Er bestaat geen portal met deze naam onder deze categorie.");
		if (portal == Portal.BUNGEECORD && !manager.getPortals().containsKey(name))
			throw new ConditionFailedException("Er bestaat geen portal met deze naam onder deze categorie.");

		if (portal == Portal.BUKKIT) {
			section.set(name + ".location", player.getLocation());
			this.configuration.save();
		} else {
			manager.updatePortal(name, new PortalData(player.getLocation(), manager.getPortalData(name).getServer()));
		}

		String message = "&6Success! Changed the location of the portal with the name &c%s &6in the category &c%s&6.";
		player.sendMessage(String.format(Text.colors(message), name, portal.toString()));
	}

	@Subcommand("teleport")
	@Syntax("<portaltype> <name>")
	@Description("Teleport to the location of a portal")
	public void onTeleport(Player player, Portal portal, String name) {
		ConfigurationSection section = this.configuration.get().getConfigurationSection(Portal.BUKKIT.toString());
		PortalManager manager = PortalManager.with(DataModule.getInstance().getSqlHelper());

		if (portal == Portal.BUKKIT && !section.contains(name))
			throw new ConditionFailedException("Er bestaat geen portal met deze naam onder deze categorie.");
		if (portal == Portal.BUNGEECORD && !manager.getPortals().containsKey(name))
			throw new ConditionFailedException("Er bestaat geen portal met deze naam onder deze categorie.");

		Location location;
		if (portal == Portal.BUKKIT) {
			location = (Location) section.get(name + ".location");
		} else {
			location = SerializationUtils.deserialize(manager.getPortalData(name).getLocation());
		}

		if (player.getWorld().getName().equals(location.getWorld().getName())) {
			player.teleport(location);
		} else {
			player.sendMessage("§cDeze portal is niet in deze server!");
			return;
		}

		String message = "&6Success! You have been teleported to the location of the portal with the name &c%s &6in the category &c%s&6.";
		player.sendMessage(String.format(Text.colors(message), name, portal.toString()));
	}

	@Subcommand("setserver")
	@Syntax("<name> <server>")
	@Description("Change the location of a portal")
	public void onSetLocation(Player player, String name, String server) {
		PortalManager manager = PortalManager.with(DataModule.getInstance().getSqlHelper());

		if (!manager.getPortals().containsKey(name))
			throw new ConditionFailedException("Er bestaat geen portal met deze naam onder deze categorie.");

		manager.updatePortal(name, new PortalData(SerializationUtils.deserialize(manager.getPortalData(name).getLocation()), server));

		String message = "&6Success! Changed the server of the portal with the name &c%s &6in the category &c%s&6 to &c%s&6.";
		player.sendMessage(String.format(Text.colors(message), name, Portal.BUNGEECORD.toString(), server));
	}

}
