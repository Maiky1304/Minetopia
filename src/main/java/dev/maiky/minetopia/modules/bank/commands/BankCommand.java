package dev.maiky.minetopia.modules.bank.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.bank.bank.Account;
import dev.maiky.minetopia.modules.bank.bank.Bank;
import dev.maiky.minetopia.modules.bank.bank.Console;
import dev.maiky.minetopia.modules.bank.bank.Permission;
import dev.maiky.minetopia.modules.bank.manager.PinManager;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.BankManager;
import dev.maiky.minetopia.util.Configuration;
import dev.maiky.minetopia.util.Numbers;
import dev.maiky.minetopia.util.Text;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Door: Maiky
 * Info: Minetopia - 05 Jun 2021
 * Package: dev.maiky.minetopia.modules.bank.commands
 */

@CommandAlias("bank|bankieren|rekening|banking")
@CommandPermission("minetopia.moderation.banking")
public class BankCommand extends BaseCommand {

	private final PinManager pinManager;
	private final BankManager manager;

	public BankCommand(PinManager pinManager) {
		manager = BankManager.with(DataModule.getInstance().getSqlHelper());
		this.pinManager = pinManager;
	}

	@HelpCommand
	@Default
	@Subcommand("main")
	@Description("Shows the subcommands")
	public void onHelp(CommandSender sender) {
		Minetopia.showHelp(sender, this, getSubCommands());
	}

	@CatchUnknown
	public void onUnknown(CommandSender sender) {
		sender.sendMessage("§cUnknown subcommand");
		this.onHelp(sender);
	}

	@Override
	public void showSyntax(CommandIssuer issuer, RegisteredCommand<?> cmd) {
		issuer.sendMessage("§cGebruik: /" + this.getExecCommandLabel() + " " +
				cmd.getPrefSubCommand() + " " +
				cmd.getSyntaxText());
	}

	@Subcommand("deletepinconsole")
	@Description("Delete the pin console you're looking at from the database")
	@CommandPermission("minetopia.moderation.banking.delete")
	public void onDelete(Player player) {
		Block targetBlock = player.getTargetBlock(null, 15);
		if (targetBlock.getType() != Material.PURPUR_STAIRS) throw new ConditionFailedException("Je kijkt niet naar een pin console blok!");

		Console console = this.pinManager.find(targetBlock.getLocation());
		if (console == null) throw new ConditionFailedException("Geen registratie gevonden!");
		this.pinManager.delete(console);
		player.sendMessage("§6You have succesfully deleted the pin console you were looking at.");
	}

	@Subcommand("setupconsole")
	@Description("Setup a pin console")
	@CommandPermission("minetopia.moderation.banking.setuppin")
	@Syntax("<soort> <id>")
	@CommandCompletion("@bankTypes @nothing")
	public void onSetup(Player player, @Conditions("noPrivate") Bank bank, int id) {
		if (this.manager.getAccount(bank, id) == null) throw new ConditionFailedException("There was no bank account found in the category "
				+ bank.toString() + " with the ID " + id + ".");

		Block targetBlock = player.getTargetBlock(null, 15);
		if (targetBlock.getType() != Material.PURPUR_STAIRS) throw new ConditionFailedException("Je kijkt niet naar een pin console blok!");

		Console console = new Console(targetBlock.getLocation(), id, bank);
		this.pinManager.insert(console);

		player.sendMessage("§6You have succesfully setup a pin console with the ID §c" + id + " §6in the category §c" + bank.toString().toLowerCase() + "§6.");
	}

	@Subcommand("create")
	@Description("Create a bank account")
	@CommandPermission("minetopia.moderation.banking.create")
	@Syntax("<soort>")
	@CommandCompletion("@bankTypes")
	public void createAccount(Player player, @Conditions("noPrivate") Bank bank) {
		String message = "§6You have succesfully created a bank account with the ID §c%s§6 in the category §c%s§6.";
		Account account = this.manager.createAccount(bank);

		player.sendMessage(String.format(Text.colors(message), account.getId(), account.getBank().toString()));
	}

	@Subcommand("delete")
	@Description("Delete a bank account")
	@CommandPermission("minetopia.moderation.banking.delete")
	@Syntax("<soort> <id>")
	@CommandCompletion("@bankTypes @nothing")
	public void deleteAccount(Player player, @Conditions("noPrivate") Bank bank, int id) {
		String message = "§6You have succesfully deleted a bank account with the ID §c%s§6 in the category §c%s§6.";
		if (this.manager.getAccount(bank, id) == null) throw new ConditionFailedException("There was no bank account found in the category "
		+ bank.toString() + " with the ID " + id + ".");

		this.manager.deleteAccount(bank, id);
		player.sendMessage(String.format(Text.colors(message), id, bank.toString()));
	}

	@Subcommand("grant")
	@Description("Grant a player permission to an account")
	@Syntax("<soort> <id> <speler> <permission>")
	@CommandCompletion("@bankTypes @nothing @players @bankPermissions")
	@CommandPermission("minetopia.moderation.banking.permissions.grant")
	public void createOverride(Player player, @Conditions("noPrivate") Bank bank, int id, OfflinePlayer target, Permission permission) {
		String message = "&6You have succesfully created the override &c%s &6for the player &c%s &6on the bank account with the ID &c%s &6in the" +
				" category &c%s&6.";

		Account account = this.manager.getAccount(bank, id);
		if (account == null) throw new ConditionFailedException("There was no bank account found in the category "
				+ bank.toString() + " with the ID " + id + ".");

		if (account.getPermissions().containsKey(target.getUniqueId()) && account.getPermissions().get(target.getUniqueId()).contains(permission)) throw new ConditionFailedException("The player " + target.getName()
		+ " already has the permission override " + permission.toString() + " set to §4true §con the bank account with the ID " + id + " in the" +
				" category " + bank.toString() + ".");

		List<Permission> overrides = account.getPermissions().containsKey(target.getUniqueId()) ? account.getPermissions().get(target.getUniqueId())
				: new ArrayList<>();
		if (permission == Permission.ALL && (overrides.contains(Permission.DEPOSIT) && overrides.contains(Permission.WITHDRAW)))
			throw new ConditionFailedException("The player " + target.getName()
					+ " already has all the permission overrides set to §4true §con the bank account with the ID " + id + " in the" +
					" category " + bank.toString() + ".");

		if (permission == Permission.ALL) {
			if (overrides.contains(Permission.ALL))
				throw new ConditionFailedException("The player " + target.getName()
						+ " already has all the permission overrides set to §4true §con the bank account with the ID " + id + " in the" +
						" category " + bank.toString() + ".");
			overrides.clear();
			overrides.add(Permission.ALL);
		} else {
			if (overrides.contains(Permission.opposite(permission))) {
				overrides.remove(Permission.opposite(permission));
				overrides.add(Permission.ALL);
			} else {
				overrides.add(permission);
			}
		}

		account.getPermissions().put(target.getUniqueId(), overrides);
		manager.saveAccount(account);

		player.sendMessage(String.format(Text.colors(message), permission.toString(), target.getName(), id, bank.toString()));
	}

	@Subcommand("revoke")
	@Description("Revoke a player's permission to an account")
	@Syntax("<soort> <id> <speler> [permission]")
	@CommandCompletion("@bankTypes @nothing @players @bankPermissions")
	@CommandPermission("minetopia.moderation.banking.permissions.revoke")
	public void revokeOverride(Player player, @Conditions("noPrivate") Bank bank, int id, OfflinePlayer offlinePlayer, Permission permission) {
		String message = "&6You have succesfully revoked the override &c%s &6for the player &c%s &6on the bank account with the ID &c%s &6in the" +
				" category &c%s&6.";

		Account account = this.manager.getAccount(bank, id);
		if ( account == null ) throw new ConditionFailedException("There was no bank account found in the category "
				+ bank.toString() + " with the ID " + id + ".");

		if ( !account.getPermissions().containsKey(offlinePlayer.getUniqueId()) ||
				(account.getPermissions().containsKey(offlinePlayer.getUniqueId()) && (!account.getPermissions()
						.get(offlinePlayer.getUniqueId()).contains(Permission.ALL) && !account.getPermissions()
						.get(offlinePlayer.getUniqueId()).contains(permission))) )
			throw new ConditionFailedException("The player " + offlinePlayer.getName()
					+ " doesn't have the permission override " + permission.toString() + " set to §4true §con the bank account with the ID " + id + " in the" +
					" category " + bank.toString() + ".");

		List<Permission> overrides = account.getPermissions().containsKey(offlinePlayer.getUniqueId()) ? account.getPermissions().get(offlinePlayer.getUniqueId())
				: new ArrayList<>();

		if (permission == Permission.ALL && overrides.contains(permission)) {
			overrides.clear();
		} else {
			if ( overrides.contains(Permission.ALL) ) {
				overrides.remove(Permission.ALL);
				overrides.add(Permission.opposite(permission));
			} else {
				overrides.remove(permission);
			}
		}

		if (overrides.isEmpty()) account.getPermissions().remove(offlinePlayer.getUniqueId());
		else account.getPermissions().put(offlinePlayer.getUniqueId(), overrides);
		manager.saveAccount(account);

		player.sendMessage(String.format(Text.colors(message), permission.toString(), offlinePlayer.getName(), id, bank.toString()));
	}

	@Subcommand("list")
	@Syntax("<soort>")
	@Description("Get a list of all accounts")
	@CommandPermission("minetopia.moderation.banking.list")
	@CommandCompletion("@bankTypes @nothing")
	public void listAccounts(Player player, @Conditions("noPrivate") Bank bank, @Default("1") int page) {
		int perPage = 10;

		String header = "&6------ &cPagina %s/%s &6------";

		List<Account> accounts = this.manager.allAccounts(bank);
		int pages = accounts.size() / perPage;

		if (page > (pages + 1)) throw new ConditionFailedException("Er bestaat geen pagina met dit nummer.");
		int index = (page - 1) * perPage;
		int max = index + perPage;

		player.sendMessage(Text.colors(String.format(header, page, (pages + 1))));
		if (accounts.size() == 0) {
			player.sendMessage("§cEr zijn nog geen rekeningen in deze categorie.");
		} else {
			for (int j = index; j < max; j++) {
				try {
					Account account = accounts.get(j);
					TextComponent component = new TextComponent(" §c- §6" + account.getId() + " ");
					TextComponent component1 = new TextComponent("§c(Permissions§c)");
					TextComponent component2 = new TextComponent(" §6(§a" + Numbers.convert(Numbers.Type.MONEY, account.getBalance()) + "§6) §6(§a" + account.getCustomName() + "§6)" +
							" §6(§a" + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(account.getCreatedOn())) + "§6)");

					StringBuilder builder = new StringBuilder();
					for (UUID uuid : account.getPermissions().keySet()) {
						builder.append("§a").append(Bukkit.getOfflinePlayer(uuid).getName()).append(": ").append(account.getPermissions().get(uuid).toString()).append("§r\n");
					}
					String string = "§cGeen permissions :(";
					if (account.getPermissions().size() != 0) {
						string = builder.substring(0, builder.length()-1);
					}

					component1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(string)));
					player.spigot().sendMessage(component, component1, component2);
				} catch (IndexOutOfBoundsException ignored) { break; };
			}
		}
	}

	@Subcommand("rename")
	@Syntax("<soort> <id> <naam>")
	@Description("Rename a bank account")
	@CommandPermission("minetopia.moderation.banking.rename")
	@CommandCompletion("@bankTypes @nothing @nothing")
	public void rename(Player player, @Conditions("noPrivate") Bank bank, int id, String[] name) {
		String message = "&6You have succesfully renamed the the bank account with the ID &c%s &6in the" +
				" category &c%s&6 to &c%s&6.";

		if (name.length == 0) {
			this.showSyntax(getCurrentCommandIssuer(), this.getDefaultRegisteredCommand());
			return;
		}

		Account account = this.manager.getAccount(bank, id);
		if (account == null) throw new ConditionFailedException("There was no bank account found in the category "
				+ bank.toString() + " with the ID " + id + ".");

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i != name.length; i++)
			builder.append(name[i]).append(" ");
		String nameProcessed = builder.substring(0, builder.length()-1);

		account.setCustomName(nameProcessed);
		this.manager.saveAccount(account);
		player.sendMessage(Text.colors(String.format(message, account.getId(), bank.toString(), nameProcessed)));
	}

}