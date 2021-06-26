package dev.maiky.minetopia.modules.security.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.bags.bag.Bag;
import dev.maiky.minetopia.modules.bags.bag.BagType;
import dev.maiky.minetopia.modules.bags.ui.KofferUI;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.BagManager;
import dev.maiky.minetopia.modules.security.SecurityModule;
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

	@Override
	public void showSyntax(CommandIssuer issuer, RegisteredCommand<?> cmd) {
		issuer.sendMessage("§cGebruik: /" + this.getExecCommandLabel() + " " +
				cmd.getPrefSubCommand() + " " +
				cmd.getSyntaxText());
	}

	@Default
	@Syntax("<player>")
	@Description("Search a player")
	@CommandCompletion("@players")
	public void onSearch(Player player, @Conditions("online|notBeingSearched") OfflinePlayer offlinePlayer) {
		final Player target = offlinePlayer.getPlayer();

		if (player.getLocation().distance(target.getLocation()) >= 4)
			throw new ConditionFailedException("§cJe bent te ver weg van §4" + offlinePlayer.getName() + " §com hem/haar te fouilleren.");

		player.sendMessage("§6Je fouilleerd nu §c" + target.getName() + "§6.");
		target.sendTitle("§3Politie", "§bJe wordt nu §lgefouilleerd§b door §3" + player.getName() + "§b.",
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

							BagManager bagManager = BagManager.with(DataModule.getInstance().getSqlHelper());
							Bag bag = bagManager.getBag(id);
							if (bag == null) {
								e.getWhoClicked().sendMessage("§cEr is iets fout gegaan met het ophalen van deze bag, contacteer een developer.");
								return;
							}

							bag.getHistory().put(e.getWhoClicked().getName(), new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
							bagManager.saveBag(bag);

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
								e.getWhoClicked().sendMessage("§cDeze koffer bevat §4geen §cillegale items.");
								return;
							}

							e.getWhoClicked().sendMessage("§cDeze koffer bevat de volgende §4illegale §citems:");
							for (String s : illegalItems)
								e.getWhoClicked().sendMessage(" §8- §7" + s);
							return;
						}
					}

					e.setCancelled(true);
					e.getWhoClicked().sendMessage("§cJe kunt alleen §4illegale §citems innemen.");
				}).bindWith(terminable);
		Events.subscribe(InventoryCloseEvent.class)
				.filter(e -> e.getInventory().equals(inventory))
				.filter(e -> e.getPlayer().getUniqueId().equals(player.getUniqueId()))
				.handler(e -> {
					beingSearched.remove(target.getUniqueId());
					e.getPlayer().sendMessage("§6Je bent gestopt met het fouilleren van §c" + target.getName() + "§6.");
					target.sendMessage("§6Je wordt niet meer §cgefouilleerd§6.");
					target.sendTitle("§3Politie", "§bJe wordt niet meer gefouilleerd.", 20, 80, 20);

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
