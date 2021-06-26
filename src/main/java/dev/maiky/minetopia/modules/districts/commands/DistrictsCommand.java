package dev.maiky.minetopia.modules.districts.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.util.Configuration;
import dev.maiky.minetopia.util.Message;
import dev.maiky.minetopia.util.Text;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Door: Maiky
 * Info: Minetopia - 24 May 2021
 * Package: dev.maiky.minetopia.modules.districts.commands
 */

@CommandAlias("districts|areas|subcity|substad|stad|steden|town|dorp|gebied|gebieden")
@CommandPermission("minetopia.admin.districts")
public class DistrictsCommand extends BaseCommand {

	private Configuration configuration;

	public DistrictsCommand(Configuration configuration) {
		this.configuration = configuration;
	}

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
	@Description("Create a district")
	public void onCreate(@Conditions("lookingAtBlock") Player player) {
		Block block = player.getTargetBlock(null, 50);

		if (this.configuration.get().contains(block.getType().toString())) {
			throw new ConditionFailedException(Message.DISTRICTS_ERROR_ALREADYEXISTS.format(block.getType().toString(),
					getExecCommandLabel(), block.getType().toString()));
		}

		this.configuration.get().set(block.getType().toString() + ".name", "Onbekend");
		this.configuration.save();
		player.sendMessage(Message.DISTRICTS_CREATED.format(block.getType().toString(), String.format("/%s setname %s <name>",
				getExecCommandLabel(), block.getType())));
	}

	@Subcommand("delete")
	@Syntax("<type>")
	@Description("Create a district")
	@CommandCompletion("@existingDistricts")
	public void onDelete(CommandSender sender, @Conditions("validateMaterial") String type) {
		Material material = Material.valueOf(type.toUpperCase());

		if (!this.configuration.get().contains(material.toString())) {
			throw new ConditionFailedException(Message.DISTRICTS_ERROR_DOESNTEXIST.format(material.toString()));
		}

		this.configuration.get().set(material.toString(), null);
		this.configuration.save();
		sender.sendMessage(Message.DISTRICTS_DELETED.format(material.toString()));
	}

	@Subcommand("setname")
	@Syntax("<type> <name>")
	@Description("Edit the name of a district")
	@CommandCompletion("@existingDistricts")
	public void onEdit(CommandSender sender, @Conditions("validateMaterial") String type, String... name) {
		if (name.length == 0) {
			this.showSyntax(getCurrentCommandIssuer(), getDefaultRegisteredCommand());
			return;
		}

		StringBuilder builder = new StringBuilder();
		for (String s : name)
			builder.append(s).append(" ");

		Material material = Material.valueOf(type.toUpperCase());

		if (!this.configuration.get().contains(material.toString())) {
			throw new ConditionFailedException(Message.DISTRICTS_ERROR_DOESNTEXIST.format(material.toString()));
		}

		this.configuration.get().set(material.toString() + ".name", builder.substring(0, builder.length()-1));
		this.configuration.save();

		Minetopia.getPlugin(Minetopia.class).districtsModule.initializeCache();

		sender.sendMessage(Message.DISTRICTS_RENAMED.format(material.toString(), builder.substring(0, builder.length()-1)));
	}

	@Subcommand("setcolor")
	@Syntax("<type> <color>")
	@Description("Edit the color of a district")
	@CommandCompletion("@existingDistricts")
	public void onEdit(CommandSender sender, @Conditions("validateMaterial") String type, String color) {
		if (color.length() > 1) throw new ConditionFailedException(Message.COMMON_ERROR_INVALIDCOLORCODE.raw());

		Material material = Material.valueOf(type.toUpperCase());

		if (!this.configuration.get().contains(material.toString())) {
			throw new ConditionFailedException(Message.DISTRICTS_ERROR_DOESNTEXIST.format(material.toString()));
		}

		this.configuration.get().set(material.toString() + ".color", color);
		this.configuration.save();

		Minetopia.getPlugin(Minetopia.class).districtsModule.initializeCache();

		sender.sendMessage(Message.DISTRICTS_RECOLORED.format(material.toString(), "&" + color));
	}

	@Subcommand("list")
	@Description("See all districts")
	public void onList(CommandSender sender) {
		for (String key : this.configuration.get().getKeys(false)) {
			sender.sendMessage(Message.DISTRICTS_LIST_ENTRY.format(key, this.configuration.get().getString(key + ".name")));
		}
		if (this.configuration.get().getKeys(false).size() == 0) {
			sender.sendMessage(Message.DISTRICTS_LIST_EMPTY.format(getExecCommandLabel()));
		}
	}

}
