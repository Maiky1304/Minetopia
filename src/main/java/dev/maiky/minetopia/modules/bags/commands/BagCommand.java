package dev.maiky.minetopia.modules.bags.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.bags.bag.Bag;
import dev.maiky.minetopia.modules.bags.bag.BagType;
import dev.maiky.minetopia.modules.bags.ui.CreateUI;
import dev.maiky.minetopia.modules.bags.ui.KofferUI;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.mongo.MongoBagManager;
import dev.maiky.minetopia.util.Message;
import dev.maiky.minetopia.util.SerializationUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Door: Maiky
 * Info: Minetopia - 28 May 2021
 * Package: dev.maiky.minetopia.modules.bags.commands
 */

@CommandAlias("bag|bags|koffer|koffers|rugzak|rugzakken")
@CommandPermission("minetopia.admin.bag")
public class BagCommand extends BaseCommand {

	private final MongoBagManager manager;
	private final List<Material> materialList = new ArrayList<>();

	public BagCommand() {
		this.manager = DataModule.getInstance().getBagManager();

		for (BagType value : BagType.values()) materialList.add(value.material);
	}

	@Subcommand("main")
	@Description("Shows the subcommands")
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
		issuer.sendMessage("§cGebruik: /" + this.getExecCommandLabel() + " " +
				cmd.getPrefSubCommand() + " " +
				cmd.getSyntaxText());
	}

	@Subcommand("history")
	@Syntax("[id]")
	@Description("Check history of a specific bag, can also be the bag you're holding.")
	public void onHistory(Player player, @Default("-1") int id) {
		long start = System.currentTimeMillis();

		boolean checkHand = id == -1;

		Bag bag;

		if (checkHand) {
			if (player.getInventory().getItemInMainHand() == null) {
				throw new ConditionFailedException(Message.BAGS_ERROR_NOTINHAND.raw());
			}

			if (!this.materialList.contains(player.getInventory().getItemInMainHand().getType())) {
				throw new ConditionFailedException(Message.BAGS_ERROR_NOTINHAND.raw());
			}

			net.minecraft.server.v1_12_R1.ItemStack nms = CraftItemStack.asNMSCopy(player.getInventory().getItemInMainHand());
			if (nms.getTag() == null)
				throw new ConditionFailedException(Message.BAGS_ERROR_NOTINHAND.raw());
			if (!nms.getTag().hasKey("id"))
				throw new ConditionFailedException(Message.BAGS_ERROR_NOTINHAND.raw());

			bag = this.manager.find(b -> b.getId().toString().equals(nms.getTag().getString("id")))
					.findFirst().orElse(null);
		} else {
			bag = this.manager.find(b -> b.getBagId() == id).findFirst().orElse(null);
		}

		if (bag == null)
			throw new ConditionFailedException(Message.BAGS_ERROR_NOTBYID.format(id));

		player.sendMessage(Message.BAGS_HISTORY_HEADER.format(bag.getBagId()));
		for (String string : bag.getHistory().keySet()) {
			player.sendMessage(Message.BAGS_HISTORY_LINE.format(string, bag.getHistory().get(string)));
		}
		if (bag.getHistory().size() == 0)
			player.sendMessage(Message.BAGS_HISTORY_NODATA.raw());
		player.sendMessage(" ");
		player.sendMessage(Message.BAGS_HISTORY_TOOK.format(((System.currentTimeMillis() - start))));
	}

	@Subcommand("create")
	@Syntax("<type> <rows>")
	@Description("Create a bag or backpack")
	@CommandCompletion("@bagTypes")
	public void createBag(Player player, BagType type, int rows) {
		if (player.getInventory().firstEmpty() == -1) {
			throw new ConditionFailedException(Message.COMMON_ERROR_SELF_NOINVSPACE.raw());
		}

		Bag bag = this.manager.createBag(type, rows);
		ItemStack item = bag.getType().create(bag.getBagId());

		player.getInventory().addItem(item);
		player.sendMessage(Message.BAGS_CREATED.format(bag.getBagId()));
	}

	@Subcommand("openbag")
	@Syntax("<id>")
	@Description("Open a bag by id")
	public void openBag(Player player, int id) {
		Bag bag = this.manager.find(b -> b.getBagId() == id).findFirst().orElse(null);
		if (bag == null)
			throw new ConditionFailedException(Message.BAGS_ERROR_NOTBYID.format(id));
		ItemStack[] itemStacks = SerializationUtils.itemStackArrayFromBase64(bag.getBase64Contents());

		bag.getHistory().put("[Lookup] " + player.getName(), new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
		this.manager.save(bag);

		KofferUI kofferUI = new KofferUI(player, bag.getBagId(), itemStacks, 0);
		kofferUI.open();
	}

	@Subcommand("retrievebag")
	@Syntax("<id>")
	@Description("Retrieve a bag by id")
	public void retrieveBag(Player player, int id) {
		if (player.getInventory().firstEmpty() == -1) {
			throw new ConditionFailedException(Message.COMMON_ERROR_SELF_NOINVSPACE.raw());
		}

		Bag bag = this.manager.find(b -> b.getBagId() == id).findFirst().orElse(null);
		if (bag == null)
			throw new ConditionFailedException(Message.BAGS_ERROR_NOTBYID.format(id));

		ItemStack itemStack = bag.getType().create(bag.getBagId());
		player.getInventory().addItem(itemStack);
	}

	@Subcommand("menu")
	@Description("Use the bag menu")
	public void onMenu(Player player) {
		CreateUI createUI = new CreateUI(player);
		createUI.open();
	}

}
