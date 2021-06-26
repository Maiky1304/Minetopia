package dev.maiky.minetopia.modules.guns.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.WeaponManager;
import dev.maiky.minetopia.modules.guns.GunsModule;
import dev.maiky.minetopia.modules.guns.gun.Weapon;
import dev.maiky.minetopia.modules.guns.models.interfaces.Model;
import dev.maiky.minetopia.modules.guns.models.util.Builder;
import dev.maiky.minetopia.modules.guns.ui.GunUI;
import dev.maiky.minetopia.util.Message;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

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

	@Subcommand("get")
	@Syntax("<model>")
	@CommandPermission("minetopia.moderation.guns.get")
	@Description("Spawn a gun by model name")
	@CommandCompletion("@models")
	public void onGet(Player player, @Conditions("verifyModel") String modelName) {
		Model model = module.getModel(modelName);
		Weapon weapon = this.weaponManager.createWeapon(model);

		ItemStack itemStack = Builder.with(model).
				setLicense(weapon.getLicense()).buildItem();
		player.getInventory().addItem(itemStack);

		player.sendMessage(Message.GUNS_CREATED.format(model.modelName()));
	}

	@Subcommand("getammo")
	@Syntax("<model>")
	@CommandPermission("minetopia.moderation.guns.get")
	@Description("Spawn ammo by model name")
	@CommandCompletion("@models")
	public void onGetAmmo(Player player, @Conditions("verifyModel") String modelName) {
		Model model = module.getModel(modelName);

		ItemStack itemStack = Builder.with(model).buildAmmo();
		player.getInventory().addItem(itemStack);

		player.sendMessage(Message.GUNS_GETAMMO.format(model.modelName()));
	}

	@Subcommand("setdurability")
	@Syntax("<number>")
	@CommandPermission("minetopia.moderation.guns.setdurability")
	@Description("Change the durability of a gun")
	public void onSet(@Conditions("hasGun") Player player, int durability) {
		final ItemStack mainHand = player.getInventory().getItemInMainHand();
		NBTTagCompound nbtTagCompound = Objects.requireNonNull(CraftItemStack.asNMSCopy(mainHand).getTag());

		String license = nbtTagCompound.getString("license");
		Weapon weapon = weaponManager.getWeaponByLicense(license);

		weapon.setDurability(durability);
		weaponManager.updateWeapon(weapon);

		player.sendMessage(Message.GUNS_SETDURABILITY.format(license, durability));
	}

	@Subcommand("give")
	@Syntax("<target> <model> <durability>")
	@CommandPermission("minetopia.moderation.guns.give")
	@Description("Give a weapon to a player")
	@CommandCompletion("@players @models @nothing")
	public void onGive(CommandSender sender, @Conditions("online") OfflinePlayer offlinePlayer,  @Conditions("verifyModel") String modelName, int durability) {
		if (offlinePlayer.getPlayer().getInventory().firstEmpty() == -1) throw new ConditionFailedException(Message.COMMON_ERROR_OTHER_NOINVSPACE.raw());

		Player target = offlinePlayer.getPlayer();
		Model model = module.getModel(modelName);

		Weapon weapon = this.weaponManager.createWeapon(model);
		weapon.setDurability(durability);
		this.weaponManager.updateWeapon(weapon);

		ItemStack itemStack = Builder.with(model).
				setLicense(weapon.getLicense()).buildItem();
		target.getInventory().addItem(itemStack);
	}

	@Subcommand("menu")
	@CommandPermission("minetopia.moderation.guns.menu")
	@Description("Open the gun model menu")
	public void onMenu(Player player) {
		GunUI gunUI = new GunUI(player, this.module);
		gunUI.open();
	}

}
