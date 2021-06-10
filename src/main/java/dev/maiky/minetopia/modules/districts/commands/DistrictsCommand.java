package dev.maiky.minetopia.modules.districts.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.util.Configuration;
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

@CommandAlias("districts")
@CommandPermission("minetopia.admin.districts")
public class DistrictsCommand extends BaseCommand {

	private Configuration configuration;

	public DistrictsCommand(Configuration configuration) {
		this.configuration = configuration;
	}

	@Default
	@HelpCommand
	public void onHelp(CommandSender sender) {
		Minetopia.showHelp(sender, this, getSubCommands());
	}

	@Subcommand("create")
	@Description("Create a district")
	public void onCreate(@Conditions("lookingAtBlock") Player player) {
		Block block = player.getTargetBlock(null, 50);

		if (this.configuration.get().contains(block.getType().toString())) {
			throw new ConditionFailedException("Er bestaat al een district met het blok " + block.getType().toString() + ", gebruik §4/"
			+ getExecCommandLabel() + " delete " + block.getType().toString() + " §com deze te verwijderen.");
		}

		this.configuration.get().set(block.getType().toString() + ".name", "???");
		this.configuration.save();
		String message = "&6Success! Created district with the block &c%s&6, edit the name with &c%s&6.";
		player.sendMessage(String.format(Text.colors(message), block.getType(), "/" + getExecCommandLabel() + " setname "
		+ block.getType() + " <name>"));
	}

	@Subcommand("delete")
	@Syntax("<type>")
	@Description("Create a district")
	@CommandCompletion("@existingDistricts")
	public void onDelete(CommandSender sender, @Conditions("validateMaterial") String type) {
		Material material = Material.valueOf(type.toUpperCase());

		if (!this.configuration.get().contains(material.toString())) {
			throw new ConditionFailedException("Er bestaat geen district met het blok " + material.toString() + ".");
		}

		this.configuration.get().set(material.toString(), null);
		this.configuration.save();
		String message = "&6Success! Deleted district with the block &c%s&6.";
		sender.sendMessage(String.format(Text.colors(message), material.toString()));
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
			throw new ConditionFailedException("Er bestaat geen district met het blok " + material.toString() + ".");
		}

		this.configuration.get().set(material.toString() + ".name", builder.substring(0, builder.length()-1));
		this.configuration.save();

		Minetopia.getPlugin(Minetopia.class).districtsModule.initializeCache();

		String message = "&6Success! Changed the name of the district with the block &c%s &6to &c%s&6.";
		sender.sendMessage(String.format(Text.colors(message), material.toString(), builder.substring(0, builder.length()-1)));
	}

	@Subcommand("setcolor")
	@Syntax("<type> <color>")
	@Description("Edit the color of a district")
	@CommandCompletion("@existingDistricts")
	public void onEdit(CommandSender sender, @Conditions("validateMaterial") String type, String color) {
		if (color.length() > 1) throw new ConditionFailedException("This is not a valid color code!");

		Material material = Material.valueOf(type.toUpperCase());

		if (!this.configuration.get().contains(material.toString())) {
			throw new ConditionFailedException("Er bestaat geen district met het blok " + material.toString() + ".");
		}

		this.configuration.get().set(material.toString() + ".color", color);
		this.configuration.save();

		Minetopia.getPlugin(Minetopia.class).districtsModule.initializeCache();

		String message = "&6Success! Changed the color of the district with the block &c%s &6to &c%s&6.";
		sender.sendMessage(String.format(Text.colors(message), material.toString(), "&" + color));
	}

	@Subcommand("list")
	@Description("See all districts")
	public void onList(CommandSender sender) {
		for (String key : this.configuration.get().getKeys(false)) {
			sender.sendMessage("§2- §a" + key + " §2[§a" + this.configuration.get().getString(key + ".name") + "§2]");
		}
		if (this.configuration.get().getKeys(false).size() == 0) {
			sender.sendMessage("§cNo districs made yet, use §4/" + getExecCommandLabel() + " §cto create the first!");
		}
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

}
