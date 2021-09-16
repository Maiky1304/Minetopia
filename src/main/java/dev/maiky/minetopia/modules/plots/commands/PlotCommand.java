package dev.maiky.minetopia.modules.plots.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.plots.classes.Selection;
import dev.maiky.minetopia.modules.plots.listener.PlotWandListener;
import dev.maiky.minetopia.util.Message;
import dev.maiky.minetopia.util.Options;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.WorldGuardWrapper;
import org.codemc.worldguardwrapper.flag.IWrappedFlag;
import org.codemc.worldguardwrapper.region.IWrappedDomain;
import org.codemc.worldguardwrapper.region.IWrappedRegion;
import org.codemc.worldguardwrapper.selection.ICuboidSelection;

import java.util.Optional;
import java.util.*;

/**
 * Door: Maiky
 * Info: Minetopia - 23 May 2021
 * Package: dev.maiky.minetopia.modules.plots.commands
 */

@CommandAlias("plot")
@CommandPermission("minetopia.common.plot")
@Description("Essential plot command for managing WG as a player")
public class PlotCommand extends BaseCommand {

	private final WorldGuardWrapper wrapper;

	public PlotCommand(WorldGuardWrapper wrapper) {
		this.wrapper = wrapper;
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

	@Subcommand("tp")
	@Syntax("<plotid>")
	@CommandPermission("minetopia.common.plot.tp")
	@Description("Teleporteer naar een plot")
	public void onTeleport(Player player, String id) {

	}

	@Subcommand("create")
	@Syntax("<plotid> [^:true/false]")
	@CommandPermission("minetopia.common.plot.create")
	@Description("Maak een plot aan op basis van je selectie met /plotwand")
	public void onCreate(Player player, String id, @Default(value = "false") boolean expandVert) {
		if (!PlotWandListener.getHashMap().containsKey(player))
			throw new ConditionFailedException(Message.PLOTS_ERROR_NOACTIVESELECTION.raw());

		Selection selection = PlotWandListener.getHashMap().get(player);
		if (selection.getPos1() == null || selection.getPos2() == null)
			throw new ConditionFailedException(Message.PLOTS_ERROR_TWOPOINTS.raw());

		Location pos1 = selection.getPos1().toBukkit();
		Location pos2 = selection.getPos2().toBukkit();

		if (expandVert) {
			pos1.setY(0);
			pos2.setY(pos2.getWorld().getMaxHeight());
		}

		WorldGuardWrapper wrapper = WorldGuardWrapper.getInstance();
		if (wrapper.getRegion(pos1.getWorld(), id).isPresent())
			throw new ConditionFailedException(Message.PLOTS_ERROR_ALREADYEXISTS.format(id));
		Optional<IWrappedRegion> optional = wrapper.addRegion(id, new ICuboidSelection() {
			@Override
			public Location getMinimumPoint() {
				return pos1;
			}

			@Override
			public Location getMaximumPoint() {
				return pos2;
			}
		});
		if (!optional.isPresent())
			throw new ConditionFailedException(Message.PLOTS_ERROR_GENERIC.raw());

		IWrappedRegion region = optional.get();
		List<String> commands = Arrays.asList("rg flag " + region.getId() + " -w " + pos1.getWorld().getName() + " interact -g NON_MEMBERS DENY",
				"rg flag " + region.getId() + " -w " + pos1.getWorld().getName() + " chest-access -g NON_MEMBERS DENY",
				"rg flag " + region.getId() + " -w " + pos1.getWorld().getName() + " USE -g MEMBERS allow",
				"rg flag " + region.getId() + " -w " + pos1.getWorld().getName() + " INTERACT -g MEMBERS allow",
				"rg flag " + region.getId() + " -w " + pos1.getWorld().getName() + " PVP allow");
		commands.forEach(cmd -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd));

		player.sendMessage(Message.PLOTS_SUCCESS_CREATED.format(id));
	}

	@Conditions("MTUser|Plot")
	@Subcommand("info")
	@CommandAlias("plotinfo|pi|ploti|pinfo")
	@Description("Bekijk de informatie van een plot.")
	public void onInfo(Player player) {
		Set<IWrappedRegion> regions = wrapper.getRegions(player.getLocation());
		List<IWrappedRegion> filtered = new ArrayList<>();
		for (IWrappedRegion wrappedRegion : regions) {
			if ( wrappedRegion.getPriority() >= Options.PLOTS_MINIMUMPRIORITY.asInt().get() ) filtered.add(wrappedRegion);
		}

		IWrappedRegion region = filtered.get(0);
		IWrappedDomain owners = region.getOwners(), members = region.getMembers();

		StringBuilder ownersBuilder = new StringBuilder();
		for (UUID uuid : owners.getPlayers()) {
			ownersBuilder.append(Bukkit.getOfflinePlayer(uuid).getName()).append(Message.PLOTS_INFO_DIVIDER.raw()).append(" ");
		}

		StringBuilder membersBuilder = new StringBuilder();
		for (UUID uuid : members.getPlayers()) {
			membersBuilder.append(Bukkit.getOfflinePlayer(uuid).getName()).append(Message.PLOTS_INFO_DIVIDER.raw()).append(" ");
		}

		String ownersString = owners.getPlayers().size() == 0 ? "Geen" : ownersBuilder.substring(0, ownersBuilder.length()-2);
		String membersString = members.getPlayers().size() == 0 ? "Geen" : membersBuilder.substring(0, membersBuilder.length()-2);

		if (player.hasPermission("minetopia.moderation.plot")) {
			StringBuilder flagsBuilder = new StringBuilder();
			for (IWrappedFlag<?> flag : region.getFlags().keySet()) {
				flagsBuilder.append(flag.getName()).append("§6=§c");
				Optional<?> optional = region.getFlag(flag);
				if (optional.isPresent()) {
					Optional<?> optionalO = (Optional<?>) optional.get();
					flagsBuilder.append(optionalO.get()).append("§6").append(Message.PLOTS_INFO_DIVIDER.raw());
				} else {
					flagsBuilder.append(Message.PLOTS_INFO_UNKNOWN.raw()).append("§6").append(Message.PLOTS_INFO_DIVIDER.raw());
				}
			}

			String flagsString = region.getFlags().size() == 0 ? Message.PLOTS_INFO_EMPTY.raw() : flagsBuilder.substring(0, flagsBuilder.length()-3);

			Message.PLOTS_INFO_STAFF.formatAsList(ownersString, membersString, flagsString, region.getId()).forEach(player::sendMessage);
		} else {
			Message.PLOTS_INFO_PLAYER.formatAsList(ownersString, membersString).forEach(player::sendMessage);
		}
	}

	private List<IWrappedRegion> filterRegions(Set<IWrappedRegion> set, int n) {
		List<IWrappedRegion> filtered = new ArrayList<>();
		for (IWrappedRegion wrappedRegion : set) {
			if ( wrappedRegion.getPriority() >= n ) filtered.add(wrappedRegion);
		}
		return filtered;
	}

	@Conditions("MTUser|Plot")
	@Subcommand("addowner")
	@Syntax("<player>")
	@Description("Add een speler op een plot als eigenaar.")
	@CommandPermission("minetopia.moderation.plot")
	public void addOwner(Player player, @Conditions("database") String target) {
		Set<IWrappedRegion> regions = wrapper.getRegions(player.getLocation());
		List<IWrappedRegion> filtered = filterRegions(regions, Options.PLOTS_MINIMUMPRIORITY.asInt().get());

		OfflinePlayer offlinePlayer;
		if (target.length() == 32)
			offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(target));
		else offlinePlayer = Bukkit.getOfflinePlayer(target);

		IWrappedRegion region = filtered.get(0);
		IWrappedDomain domain = region.getOwners();

		if (domain.getPlayers().contains(offlinePlayer.getUniqueId())) {
			throw new ConditionFailedException(Message.PLOTS_ERROR_ALREADYOWNER.raw());
		}

		region.getOwners().addPlayer(offlinePlayer.getUniqueId());

		player.sendMessage(Message.PLOTS_SUCCESS_ADDOWNER.format(offlinePlayer.getName(), region.getId()));
	}

	@Conditions("MTUser|Plot")
	@Subcommand("removeowner")
	@Syntax("<player>")
	@Description("Verwijder een speler van een plot als eigenaar.")
	@CommandPermission("minetopia.moderation.plot")
	public void removeOwner(Player player, @Conditions("database") String target) {
		Set<IWrappedRegion> regions = wrapper.getRegions(player.getLocation());
		List<IWrappedRegion> filtered = filterRegions(regions, Options.PLOTS_MINIMUMPRIORITY.asInt().get());

		OfflinePlayer offlinePlayer;
		if (target.length() == 32)
			offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(target));
		else offlinePlayer = Bukkit.getOfflinePlayer(target);

		IWrappedRegion region = filtered.get(0);
		IWrappedDomain domain = region.getOwners();

		if (!domain.getPlayers().contains(offlinePlayer.getUniqueId())) {
			throw new ConditionFailedException(Message.PLOTS_ERROR_NOTOWNER_OTHER.raw());
		}

		region.getOwners().removePlayer(offlinePlayer.getUniqueId());

		player.sendMessage(Message.PLOTS_SUCCESS_REMOVEOWNER.format(offlinePlayer.getName(), region.getId()));
	}

	@Conditions("MTUser|Plot")
	@Subcommand("addmember")
	@Syntax("<player>")
	@Description("Add een speler op een plot als member.")
	public void addMember(Player player, @Conditions("database") String target) {
		Set<IWrappedRegion> regions = wrapper.getRegions(player.getLocation());
		List<IWrappedRegion> filtered = new ArrayList<>();
		for (IWrappedRegion wrappedRegion : regions) {
			if ( wrappedRegion.getPriority() >= Options.PLOTS_MINIMUMPRIORITY.asInt().get() ) filtered.add(wrappedRegion);
		}

		OfflinePlayer offlinePlayer;
		if (target.length() == 32)
			offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(target));
		else offlinePlayer = Bukkit.getOfflinePlayer(target);

		IWrappedRegion region = filtered.get(0);
		IWrappedDomain domain = region.getOwners();

		if (!domain.getPlayers().contains(player.getUniqueId()) && !player.hasPermission("minetopia.moderation.plot")) {
			throw new ConditionFailedException(Message.PLOTS_ERROR_NOTOWNER_SELF.raw());
		}

		IWrappedDomain domain2 = region.getMembers();
		if (domain2.getPlayers().contains(offlinePlayer.getUniqueId())) {
			throw new ConditionFailedException(Message.PLOTS_ERROR_ALREADYMEMBER.raw());
		}

		region.getMembers().addPlayer(offlinePlayer.getUniqueId());
		player.sendMessage(Message.PLOTS_SUCCESS_ADDMEMBER.format(offlinePlayer.getName(), region.getId()));
	}

	@Conditions("MTUser|Plot")
	@Subcommand("removemember")
	@Syntax("<player>")
	@Description("Verwijder een speler van een plot als member.")
	public void removeMember(Player player, @Conditions("database") String target) {
		Set<IWrappedRegion> regions = wrapper.getRegions(player.getLocation());
		List<IWrappedRegion> filtered = new ArrayList<>();
		for (IWrappedRegion wrappedRegion : regions) {
			if ( wrappedRegion.getPriority() >= Options.PLOTS_MINIMUMPRIORITY.asInt().get() ) filtered.add(wrappedRegion);
		}

		OfflinePlayer offlinePlayer;
		if (target.length() == 32)
			offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(target));
		else offlinePlayer = Bukkit.getOfflinePlayer(target);

		IWrappedRegion region = filtered.get(0);
		IWrappedDomain domain = region.getOwners();

		if (!domain.getPlayers().contains(player.getUniqueId()) && !player.hasPermission("minetopia.moderation.plot")) {
			throw new ConditionFailedException(Message.PLOTS_ERROR_NOTOWNER_SELF.raw());
		}

		IWrappedDomain domain2 = region.getMembers();
		if (!domain2.getPlayers().contains(offlinePlayer.getUniqueId())) {
			throw new ConditionFailedException(Message.PLOTS_ERROR_NOTMEMBER.raw());
		}

		region.getMembers().removePlayer(offlinePlayer.getUniqueId());

		player.sendMessage(Message.PLOTS_SUCCESS_REMOVEMEMBER.format(offlinePlayer.getName(), region.getId()));
	}

	@Conditions("MTUser|Plot")
	@Subcommand("clear")
	@Description("Clear een plot volledig.")
	@CommandPermission("minetopia.moderation.plot")
	public void onClear(Player player) {
		Set<IWrappedRegion> regions = wrapper.getRegions(player.getLocation());
		List<IWrappedRegion> filtered = new ArrayList<>();
		for (IWrappedRegion wrappedRegion : regions) {
			if ( wrappedRegion.getPriority() >= Options.PLOTS_MINIMUMPRIORITY.asInt().get() ) filtered.add(wrappedRegion);
		}

		IWrappedRegion region = filtered.get(0);
		this.clearDomain(region.getOwners());
		this.clearDomain(region.getMembers());

		player.sendMessage(Message.PLOTS_SUCCESS_CLEAR.format(region.getId()));
	}

	private void clearDomain(IWrappedDomain domain) {
		for (UUID u : domain.getPlayers()) {
			domain.removePlayer(u);
		}
	}

}
