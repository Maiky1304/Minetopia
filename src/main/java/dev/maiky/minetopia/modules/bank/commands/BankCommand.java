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
import dev.maiky.minetopia.util.Message;
import dev.maiky.minetopia.util.Numbers;
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

	@Subcommand("deletepinconsole")
	@Description("Delete the pin console you're looking at from the database")
	@CommandPermission("minetopia.moderation.banking.delete")
	public void onDelete(Player player) {
		Block targetBlock = player.getTargetBlock(null, 15);
		if (targetBlock.getType() != Material.PURPUR_STAIRS) throw new ConditionFailedException(Message.BANKING_ERROR_PINCONSOLE_LOOKING.raw());

		Console console = this.pinManager.find(targetBlock.getLocation());
		if (console == null) throw new ConditionFailedException(Message.BANKING_ERROR_NOREGISTRATION.raw());
		this.pinManager.delete(console);
		player.sendMessage(Message.BANKING_PINCONSOLE_DELETED.raw());
	}

	@Subcommand("setupconsole")
	@Description("Setup a pin console")
	@CommandPermission("minetopia.moderation.banking.setuppin")
	@Syntax("<soort> <id>")
	@CommandCompletion("@bankTypes @nothing")
	public void onSetup(Player player, @Conditions("noPrivate") Bank bank, int id) {
		if (this.manager.getAccount(bank, id) == null) throw new ConditionFailedException(Message.BANKING_ERROR_NOBANKACCOUNT.format(bank.toString(), id));

		Block targetBlock = player.getTargetBlock(null, 15);
		if (targetBlock.getType() != Material.PURPUR_STAIRS) throw new ConditionFailedException(Message.BANKING_ERROR_PINCONSOLE_LOOKING.raw());

		Console console = new Console(targetBlock.getLocation(), id, bank);
		this.pinManager.insert(console);

		player.sendMessage(Message.BANKING_PINCONSOLE_SUCCESS.format(id, bank.toString().toLowerCase()));
	}

	@Subcommand("create")
	@Description("Create a bank account")
	@CommandPermission("minetopia.moderation.banking.create")
	@Syntax("<soort>")
	@CommandCompletion("@bankTypes")
	public void createAccount(Player player, @Conditions("noPrivate") Bank bank) {
		Account account = this.manager.createAccount(bank);
		player.sendMessage(Message.BANKING_ACCOUNT_SUCCESS.format(account.getId(), account.getBank().toString()));
	}

	@Subcommand("delete")
	@Description("Delete a bank account")
	@CommandPermission("minetopia.moderation.banking.delete")
	@Syntax("<soort> <id>")
	@CommandCompletion("@bankTypes @nothing")
	public void deleteAccount(Player player, @Conditions("noPrivate") Bank bank, int id) {
		if (this.manager.getAccount(bank, id) == null) throw new ConditionFailedException(Message.BANKING_ERROR_NOBANKACCOUNT
				.format(bank.toString().toLowerCase(), id));

		this.manager.deleteAccount(bank, id);
		player.sendMessage(Message.BANKING_ACCOUNT_DELETED.format(id, bank.toString().toLowerCase()));
	}

	@Subcommand("grant")
	@Description("Grant a player permission to an account")
	@Syntax("<soort> <id> <speler> <permission>")
	@CommandCompletion("@bankTypes @nothing @players @bankPermissions")
	@CommandPermission("minetopia.moderation.banking.permissions.grant")
	public void createOverride(Player player, @Conditions("noPrivate") Bank bank, int id, OfflinePlayer target, Permission permission) {
		Account account = this.manager.getAccount(bank, id);
		if (account == null) throw new ConditionFailedException(Message.BANKING_ERROR_NOBANKACCOUNT.format(bank.toString().toLowerCase(),
				id));

		if (account.getPermissions().containsKey(target.getUniqueId()) && account.getPermissions().get(target.getUniqueId()).contains(permission)) throw new ConditionFailedException(
				Message.BANKING_ERROR_ALREADYHASPERMISSION.format(target.getName())
		);

		List<Permission> overrides = account.getPermissions().containsKey(target.getUniqueId()) ? account.getPermissions().get(target.getUniqueId())
				: new ArrayList<>();
		if (permission == Permission.ALL && (overrides.contains(Permission.DEPOSIT) && overrides.contains(Permission.WITHDRAW)))
			throw new ConditionFailedException(Message.BANKING_ERROR_ALREADYHASALLPERMISSIONS.format(target.getName()));

		if (permission == Permission.ALL) {
			if (overrides.contains(Permission.ALL))
				throw new ConditionFailedException(Message.BANKING_ERROR_ALREADYHASALLPERMISSIONS.format(target.getName()));
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

		player.sendMessage(Message.BANKING_OVERRIDES_SUCCESS.format(permission.toString().toLowerCase(), target.getName(), id,
				bank.toString().toLowerCase()));
	}

	@Subcommand("revoke")
	@Description("Revoke a player's permission to an account")
	@Syntax("<soort> <id> <speler> [permission]")
	@CommandCompletion("@bankTypes @nothing @players @bankPermissions")
	@CommandPermission("minetopia.moderation.banking.permissions.revoke")
	public void revokeOverride(Player player, @Conditions("noPrivate") Bank bank, int id, OfflinePlayer offlinePlayer, Permission permission) {
		Account account = this.manager.getAccount(bank, id);
		if ( account == null ) throw new ConditionFailedException(Message.BANKING_ERROR_NOBANKACCOUNT.format(bank.toString().toLowerCase(),
				id));

		if ( !account.getPermissions().containsKey(offlinePlayer.getUniqueId()) ||
				(account.getPermissions().containsKey(offlinePlayer.getUniqueId()) && (!account.getPermissions()
						.get(offlinePlayer.getUniqueId()).contains(Permission.ALL) && !account.getPermissions()
						.get(offlinePlayer.getUniqueId()).contains(permission))) )
			throw new ConditionFailedException(Message.BANKING_ERROR_DOESNTHAVEPERMISSION.format(offlinePlayer.getName()));

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

		player.sendMessage(Message.BANKING_OVERRIDES_DELETED.format(permission.toString().toLowerCase(), offlinePlayer.getName(), id, bank.toString()
		.toLowerCase()));
	}

	@Subcommand("list")
	@Syntax("<soort>")
	@Description("Get a list of all accounts")
	@CommandPermission("minetopia.moderation.banking.list")
	@CommandCompletion("@bankTypes @nothing")
	public void listAccounts(Player player, @Conditions("noPrivate") Bank bank, @Default("1") int page) {
		int perPage = 10;

		List<Account> accounts = this.manager.allAccounts(bank);
		int pages = accounts.size() / perPage;

		if (page > (pages + 1)) throw new ConditionFailedException(Message.BANKING_LIST_PAGENOTFOUND.raw());
		int index = (page - 1) * perPage;
		int max = index + perPage;

		player.sendMessage(Message.BANKING_LIST_HEADER.format(page, (pages + 1)));
		if (accounts.size() == 0) {
			player.sendMessage(Message.BANKING_LIST_NOACCOUNTS.raw());
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
		if (name.length == 0) {
			this.showSyntax(getCurrentCommandIssuer(), this.getDefaultRegisteredCommand());
			return;
		}

		Account account = this.manager.getAccount(bank, id);
		if (account == null) throw new ConditionFailedException(Message.BANKING_ERROR_NOBANKACCOUNT.format(bank.toString().toLowerCase(),
				id));

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i != name.length; i++)
			builder.append(name[i]).append(" ");
		String nameProcessed = builder.substring(0, builder.length()-1);

		account.setCustomName(nameProcessed);
		this.manager.saveAccount(account);
		player.sendMessage(Message.BANKING_ACCOUNT_RENAMED.format(nameProcessed));
	}



}