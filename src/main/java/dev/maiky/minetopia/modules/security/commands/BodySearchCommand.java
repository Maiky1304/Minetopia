package dev.maiky.minetopia.modules.security.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.bags.bag.Bag;
import dev.maiky.minetopia.modules.bags.bag.BagType;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.mongo.MongoBagManager;
import dev.maiky.minetopia.modules.security.SecurityModule;
import dev.maiky.minetopia.util.Message;
import dev.maiky.minetopia.util.SerializationUtils;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.composite.CompositeClosingException;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.PlayerInventory;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Door: Maiky
 * Info: Minetopia - 11 Jun 2021
 * Package: dev.maiky.minetopia.modules.security.commands
 */

@CommandAlias("bodysearch|fouilleer")
@CommandPermission("minetopia.special.bodysearch")
public class BodySearchCommand extends BaseCommand {

	private static final HashMap<UUID, UUID> beingSearched = new HashMap<>();

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

	@Default
	@Syntax("<player>")
	@Description("Search a player")
	@CommandCompletion("@players")
	public void onSearch(Player player, @Conditions("online|notBeingSearched") OfflinePlayer offlinePlayer) {
		final Player target = offlinePlayer.getPlayer();

		if (player.getLocation().distance(target.getLocation()) >= 4)
			throw new ConditionFailedException(Message.SECURITY_BODYSEARCH_ERROR_TOOFARAWAY.format(offlinePlayer.getName()));

		player.sendMessage(Message.SECURITY_BODYSEARCH_SUCCESS_START.format(target.getName()));
		target.sendTitle(Message.SECURITY_BODYSEARCH_SUCCESS_START_TITLE.raw(), Message.SECURITY_BODYSEARCH_SUCCESS_START_SUBTITLE
				.format(player.getName()),
				20, 80, 20);

		beingSearched.put(target.getUniqueId(), player.getUniqueId());

		PlayerInventory inventory = target.getInventory();
		player.openInventory(inventory);

		CompositeTerminable terminable = CompositeTerminable.create();

		Events.subscribe(InventoryClickEvent.class)
				.filter(e -> {
					System.out.println(e.getWhoClicked().getUniqueId().equals(target.getUniqueId()));
					return e.getWhoClicked().getUniqueId().equals(target.getUniqueId());
				})
				.handler(e -> e.setCancelled(true)).bindWith(terminable);
		Events.subscribe(PlayerInteractEvent.class, EventPriority.HIGHEST)
				.filter(e -> e.getPlayer().getUniqueId().equals(target.getUniqueId()))
				.handler(e -> e.setCancelled(true)).bindWith(terminable);
		Events.subscribe(PlayerMoveEvent.class)
				.filter(e -> e.getPlayer().getUniqueId().equals(target.getUniqueId()))
				.handler(e -> {
					if (e.getFrom().distanceSquared(e.getTo()) > 0.0D) {
						e.getPlayer().teleport(e.getFrom());
					}
				}).bindWith(terminable);
		Events.subscribe(InventoryClickEvent.class)
				.filter(e -> e.getInventory().equals(inventory))
				.filter(e -> e.getWhoClicked().getUniqueId().equals(player.getUniqueId()))
				.filter(e -> e.getCurrentItem() != null)
				.filter(e -> e.getCurrentItem().getType() != Material.AIR)
				.filter(e -> !SecurityModule.isIllegal(e.getCurrentItem().getType()))
				.handler(e -> {
					List<Material> materialList = new ArrayList<>();
					for (BagType value : BagType.values()) materialList.add(value.material);
					if (materialList.contains(e.getCurrentItem().getType())) {
						ItemStack nms = CraftItemStack.asNMSCopy(e.getCurrentItem());
						if (nms.getTag() != null && nms.getTag().hasKey("id")) {
							NBTTagCompound tagCompound = nms.getTag();

							assert tagCompound != null;
							int id = tagCompound.getInt("id");

							MongoBagManager bagManager = DataModule.getInstance().getBagManager();
							Bag bag = bagManager.find(b -> b.getBagId() == id).findFirst().orElse(null);
							if (bag == null) {
								e.getWhoClicked().sendMessage(Message.BAGS_ERROR_OPEN_OTHER.raw());
								return;
							}

							bag.getHistory().put(e.getWhoClicked().getName(), new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
							bagManager.save(bag);

							org.bukkit.inventory.ItemStack[] itemStacks = SerializationUtils.itemStackArrayFromBase64(bag.getBase64Contents());
							List<String> illegalItems = new ArrayList<>();
							for (org.bukkit.inventory.ItemStack itemStack : itemStacks) {
								if (SecurityModule.isIllegal(itemStack.getType())) {
									String add = !itemStack.hasItemMeta() ?
											itemStack.getType().toString().toLowerCase() :
											itemStack.getItemMeta().hasDisplayName() ? ChatColor.stripColor(itemStack.getItemMeta().getDisplayName())
													: itemStack.getType().toString().toLowerCase();
									illegalItems.add(add);
								}
							}

							if (illegalItems.size() == 0) {
								e.setCancelled(true);
								e.getWhoClicked().sendMessage(Message.SECURITY_BODYSEARCH_ERROR_NOILLEGALITEMS.raw());
								return;
							}

							e.getWhoClicked().sendMessage(Message.SECURITY_BODYSEARCH_SUCCESS_ILLEGALITEMS_HEADER.raw());
							for (String s : illegalItems)
								e.getWhoClicked().sendMessage(Message.SECURITY_BODYSEARCH_SUCCESS_ILLEGALITEMS_ENTRY.format(s));
							return;
						}
					}

					e.setCancelled(true);
					e.getWhoClicked().sendMessage(Message.SECURITY_BODYSEARCH_ERROR_ONLYTAKEILLEGAL.raw());
				}).bindWith(terminable);
		Events.subscribe(InventoryCloseEvent.class)
				.filter(e -> e.getInventory().equals(inventory))
				.filter(e -> e.getPlayer().getUniqueId().equals(player.getUniqueId()))
				.handler(e -> {
					beingSearched.remove(target.getUniqueId());
					e.getPlayer().sendMessage(Message.SECURITY_BODYSEARCH_SUCCESS_STOP_POLICE.format(target.getName()));
					target.sendMessage(Message.SECURITY_BODYSEARCH_SUCCESS_STOP_PLAYER.raw());
					target.sendTitle(Message.SECURITY_BODYSEARCH_SUCCESS_STOP_TITLE.raw(), Message.SECURITY_BODYSEARCH_SUCCESS_STOP_SUBTITLE.raw(), 20, 80, 20);

					try {
						terminable.close();
					} catch (CompositeClosingException compositeClosingException) {
						compositeClosingException.printStackTrace();
					}
				}).bindWith(terminable);
	}

	public static HashMap<UUID, UUID> getBeingSearched() {
		return beingSearched;
	}
}
