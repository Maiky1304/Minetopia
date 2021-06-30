package dev.maiky.minetopia.modules.players.ui.admintool;

import dev.maiky.minetopia.modules.colors.packs.ChatColor;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.PlayerManager;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import dev.maiky.minetopia.modules.players.ui.AdminToolUI;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Door: Maiky
 * Info: Minetopia - 16 Jun 2021
 * Package: dev.maiky.minetopia.modules.players.ui.admintool
 */

public class NameColorUI extends Gui {

	private final OfflinePlayer offlinePlayer;

	public NameColorUI(Player player, OfflinePlayer offlinePlayer) {
		super(player, 6, "§3Kies een naamkleur");
		this.offlinePlayer = offlinePlayer;
	}

	@Override
	public void redraw() {
		for (ChatColor color : ChatColor.values()) {
			if (color.font) continue;
			addItem(ItemStackBuilder.of(nbtFormat(ItemStackBuilder.of(Material.IRON_INGOT).name("§" + color.getColor() + color.itemName)
					.lore("", "&7Verander de naamkleur van &b" + offlinePlayer.getName() + "&7 hiernaar.").build(), color.toString().toLowerCase())).build(() -> {
				MinetopiaUser user;
				if (offlinePlayer.isOnline()) {
					user = PlayerManager.getCache().get(offlinePlayer.getUniqueId());
				} else {
					user = PlayerManager.with(DataModule.getInstance().getSqlHelper()).retrieve(offlinePlayer.getUniqueId());
				}
				user.setCityColor(color.getColor().replace("&", "§"));
				if (!offlinePlayer.isOnline())
					PlayerManager.with(DataModule.getInstance().getSqlHelper()).update(user);
			}));
		}

		for (int i = 36; i < 45; i++) {
			setItem(i, ItemStackBuilder.of(Material.STAINED_GLASS_PANE).name(" ").buildItem().build());
		}
		setItem(49, ItemStackBuilder.of(Material.LADDER).name("&cTerug naar vorige menu").build(() -> new AdminToolUI(getPlayer(), offlinePlayer).open()));
	}

	private ItemStack nbtFormat(ItemStack itemStack, String string) {
		net.minecraft.server.v1_12_R1.ItemStack nms = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound tagCompound = nms.getTag() == null ? new NBTTagCompound() : nms.getTag();
		tagCompound.setString("mtcustom", string);
		nms.setTag(tagCompound);
		return CraftItemStack.asCraftMirror(nms);
	}

}
