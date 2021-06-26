package dev.maiky.minetopia.modules.plots.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.util.Message;
import dev.maiky.minetopia.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.WorldGuardWrapper;
import org.codemc.worldguardwrapper.flag.IWrappedFlag;
import org.codemc.worldguardwrapper.region.IWrappedDomain;
import org.codemc.worldguardwrapper.region.IWrappedRegion;

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

	@Conditions("MTUser|Plot")
	@Subcommand("info")
	@CommandAlias("plotinfo|pi|ploti|pinfo")
	@Description("Bekijk de informatie van een plot.")
	public void onInfo(Player player) {
		Set<IWrappedRegion> regions = wrapper.getRegions(player.getLocation());
		List<IWrappedRegion> filtered = new ArrayList<>();
		for (IWrappedRegion wrappedRegion : regions) {
			if ( wrappedRegion.getPriority() >= 0 ) filtered.add(wrappedRegion);
		}

		IWrappedRegion region = filtered.get(0);
		IWrappedDomain owners = region.getOwners(), members = region.getMembers();

		StringBuilder ownersBuilder = new StringBuilder();
		for (UUID uuid : owners.getPlayers()) {
			ownersBuilder.append(Bukkit.getOfflinePlayer(uuid).getName()).append(", ");
		}

		StringBuilder membersBuilder = new StringBuilder();
		for (UUID uuid : members.getPlayers()) {
			membersBuilder.append(Bukkit.getOfflinePlayer(uuid).getName()).append(", ");
		}

		if (player.hasPermission("minetopia.moderation.plot")) {
			StringBuilder flagsBuilder = new StringBuilder();
			for (IWrappedFlag<?> flag : region.getFlags().keySet()) {
				flagsBuilder.append(flag.getName()).append("§6=§c");
				Optional<?> optional = region.getFlag(flag);
				if (optional.isPresent()) {
					Optional<?> optionalO = (Optional<?>) optional.get();
					flagsBuilder.append(optionalO.get()).append("§6,");
				} else {
					flagsBuilder.append("???").append("§6,");
				}
			}

			String divider = "§6§m----------------------------------------------------";
			String line0 = "&6Eigenaren: &c%s";
			String line1 = "&6Leden: &c%s";
			String line2 = "&6Flags: &c%s";
			String line3 = "&6ID: &c%s";

			player.sendMessage(divider);
			player.sendMessage(String.format(Text.colors(line0), owners.getPlayers().size() == 0 ? "Geen" : ownersBuilder.substring(0, ownersBuilder.length()-2)));
			player.sendMessage(String.format(Text.colors(line1), members.getPlayers().size() == 0 ? "Geen" : membersBuilder.substring(0, membersBuilder.length()-2)));
			player.sendMessage(String.format(Text.colors(line2), region.getFlags().size() == 0 ? "Geen" : flagsBuilder.substring(0, flagsBuilder.length()-3)));
			player.sendMessage(String.format(Text.colors(line3), region.getId()));
			player.sendMessage(divider);
		} else {
			String line0 = "&6Eigenaren: &c%s";
			String line1 = "&6Leden: &c%s";

			player.sendMessage(String.format(Text.colors(line0), owners.getPlayers().size() == 0 ? "Geen" : ownersBuilder.substring(0, ownersBuilder.length()-2)));
			player.sendMessage(String.format(Text.colors(line1), members.getPlayers().size() == 0 ? "Geen" : membersBuilder.substring(0, membersBuilder.length()-2)));
		}
	}

	@Conditions("MTUser|Plot")
	@Subcommand("addowner")
	@Syntax("<player>")
	@Description("Add een speler op een plot als eigenaar.")
	@CommandPermission("minetopia.moderation.plot")
	public void addOwner(Player player, @Conditions("database") String target) {
		Set<IWrappedRegion> regions = wrapper.getRegions(player.getLocation());
		List<IWrappedRegion> filtered = new ArrayList<>();
		for (IWrappedRegion wrappedRegion : regions) {
			if ( wrappedRegion.getPriority() >= 0 ) filtered.add(wrappedRegion);
		}

		OfflinePlayer offlinePlayer;
		if (target.length() == 32)
			offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(target));
		else offlinePlayer = Bukkit.getOfflinePlayer(target);

		IWrappedRegion region = filtered.get(0);
		IWrappedDomain domain = region.getOwners();

		if (domain.getPlayers().contains(offlinePlayer.getUniqueId())) {
			throw new ConditionFailedException("Deze speler is al eigenaar van dit plot!");
		}

		region.getOwners().addPlayer(offlinePlayer.getUniqueId());
		String message = "&6Je hebt &c%s &6toegevoegd als eigenaar aan het plot &c%s&6.";
		player.sendMessage(Text.colors(String.format(message, offlinePlayer.getName(), region.getId())));
	}

	@Conditions("MTUser|Plot")
	@Subcommand("removeowner")
	@Syntax("<player>")
	@Description("Verwijder een speler van een plot als eigenaar.")
	@CommandPermission("minetopia.moderation.plot")
	public void removeOwner(Player player, @Conditions("database") String target) {
		Set<IWrappedRegion> regions = wrapper.getRegions(player.getLocation());
		List<IWrappedRegion> filtered = new ArrayList<>();
		for (IWrappedRegion wrappedRegion : regions) {
			if ( wrappedRegion.getPriority() >= 0 ) filtered.add(wrappedRegion);
		}

		OfflinePlayer offlinePlayer;
		if (target.length() == 32)
			offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(target));
		else offlinePlayer = Bukkit.getOfflinePlayer(target);

		IWrappedRegion region = filtered.get(0);
		IWrappedDomain domain = region.getOwners();

		if (!domain.getPlayers().contains(offlinePlayer.getUniqueId())) {
			throw new ConditionFailedException("Deze speler is geen eigenaar van dit plot!");
		}

		region.getOwners().removePlayer(offlinePlayer.getUniqueId());
		String message = "&6Je hebt &c%s &6verwijderd als eigenaar van het plot &c%s&6.";
		player.sendMessage(Text.colors(String.format(message, offlinePlayer.getName(), region.getId())));
	}

	@Conditions("MTUser|Plot")
	@Subcommand("addmember")
	@Syntax("<player>")
	@Description("Add een speler op een plot als member.")
	public void addMember(Player player, @Conditions("database") String target) {
		Set<IWrappedRegion> regions = wrapper.getRegions(player.getLocation());
		List<IWrappedRegion> filtered = new ArrayList<>();
		for (IWrappedRegion wrappedRegion : regions) {
			if ( wrappedRegion.getPriority() >= 0 ) filtered.add(wrappedRegion);
		}

		OfflinePlayer offlinePlayer;
		if (target.length() == 32)
			offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(target));
		else offlinePlayer = Bukkit.getOfflinePlayer(target);

		IWrappedRegion region = filtered.get(0);
		IWrappedDomain domain = region.getOwners();

		if (!domain.getPlayers().contains(player.getUniqueId()) && !player.hasPermission("minetopia.moderation.plot")) {
			throw new ConditionFailedException("Jij bent geen eigenaar van dit plot!");
		}

		IWrappedDomain domain2 = region.getMembers();
		if (domain2.getPlayers().contains(offlinePlayer.getUniqueId())) {
			throw new ConditionFailedException("Deze speler is al member van dit plot.");
		}

		region.getMembers().addPlayer(offlinePlayer.getUniqueId());
		String message = "&6Je hebt &c%s &6toegevoegd als member aan dit plot.";
		player.sendMessage(Text.colors(String.format(message, offlinePlayer.getName())));
	}

	@Conditions("MTUser|Plot")
	@Subcommand("removemember")
	@Syntax("<player>")
	@Description("Verwijder een speler van een plot als member.")
	public void removeMember(Player player, @Conditions("database") String target) {
		Set<IWrappedRegion> regions = wrapper.getRegions(player.getLocation());
		List<IWrappedRegion> filtered = new ArrayList<>();
		for (IWrappedRegion wrappedRegion : regions) {
			if ( wrappedRegion.getPriority() >= 0 ) filtered.add(wrappedRegion);
		}

		OfflinePlayer offlinePlayer;
		if (target.length() == 32)
			offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(target));
		else offlinePlayer = Bukkit.getOfflinePlayer(target);

		IWrappedRegion region = filtered.get(0);
		IWrappedDomain domain = region.getOwners();

		if (!domain.getPlayers().contains(player.getUniqueId()) && !player.hasPermission("minetopia.moderation.plot")) {
			throw new ConditionFailedException("Jij bent geen eigenaar van dit plot!");
		}

		IWrappedDomain domain2 = region.getMembers();
		if (!domain2.getPlayers().contains(offlinePlayer.getUniqueId())) {
			throw new ConditionFailedException("Deze speler is geen member van dit plot.");
		}

		region.getMembers().removePlayer(offlinePlayer.getUniqueId());
		String message = "&6Je hebt &c%s &6verwijderd als member van dit plot.";
		player.sendMessage(Text.colors(String.format(message, offlinePlayer.getName())));
	}

	@Conditions("MTUser|Plot")
	@Subcommand("clear")
	@Description("Clear een plot volledig.")
	@CommandPermission("minetopia.moderation.plot")
	public void onClear(Player player) {
		Set<IWrappedRegion> regions = wrapper.getRegions(player.getLocation());
		List<IWrappedRegion> filtered = new ArrayList<>();
		for (IWrappedRegion wrappedRegion : regions) {
			if ( wrappedRegion.getPriority() >= 0 ) filtered.add(wrappedRegion);
		}

		IWrappedRegion region = filtered.get(0);
		this.clearDomain(region.getOwners());
		this.clearDomain(region.getMembers());

		String message = "&6Je hebt het plot &c%s &6volledig gecleared.";
		player.sendMessage(String.format(Text.colors(message), region.getId()));
	}

	private boolean clearDomain(IWrappedDomain domain) {
		for (UUID u : domain.getPlayers()) {
			domain.removePlayer(u);
		}
		return true;
	}

}
