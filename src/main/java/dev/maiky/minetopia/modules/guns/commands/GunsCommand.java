package dev.maiky.minetopia.modules.guns.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.WeaponManager;
import dev.maiky.minetopia.modules.guns.GunsModule;
import dev.maiky.minetopia.modules.guns.gun.Weapon;
import dev.maiky.minetopia.modules.guns.models.interfaces.Model;
import dev.maiky.minetopia.modules.guns.models.util.Builder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Door: Maiky
 * Info: Minetopia - 09 Jun 2021
 * Package: dev.maiky.minetopia.modules.guns.commands
 */

@CommandAlias("guns|gun|weapons|wapens|wapen")
@CommandPermission("minetopia.moderation.guns")
public class GunsCommand extends BaseCommand {

	private WeaponManager weaponManager;
	private GunsModule module;

	public GunsCommand(GunsModule module) {
		this.module = module;
		this.weaponManager = WeaponManager.with(DataModule.getInstance().getSqlHelper());
	}

	@HelpCommand
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

	@Subcommand("get")
	@Syntax("<model>")
	@CommandPermission("minetopia.moderation.guns.get")
	@Description("Spawn a gun by model name")
	@CommandCompletion("@models")
	private void onGet(Player player, @Conditions("verifyModel") String modelName) {
		Model model = module.getModel(modelName);
		Weapon weapon = this.weaponManager.createWeapon(model);

		ItemStack itemStack = Builder.with(model).
				setLicense(weapon.getLicense()).buildItem();
		player.getInventory().addItem(itemStack);

		player.sendMessage("§6You have succesfully created a weapon with the modelname §c" + modelName + "§6.");
	}

}
