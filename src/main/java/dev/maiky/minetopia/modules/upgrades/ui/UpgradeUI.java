package dev.maiky.minetopia.modules.upgrades.ui;

import dev.maiky.minetopia.modules.data.managers.mongo.MongoPlayerManager;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUpgrades;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import dev.maiky.minetopia.modules.upgrades.upgrades.Upgrade;
import dev.maiky.minetopia.util.Message;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Slot;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * Door: Maiky
 * Info: Minetopia - 23 May 2021
 * Package: dev.maiky.minetopia.modules.upgrades.ui
 */

public class UpgradeUI extends Gui {

	public UpgradeUI(Player player) {
		super(player, 6, Message.UPGRADES_GUI_TITLE.raw());
	}

	@Override
	public void redraw() {
		MinetopiaUser user = MongoPlayerManager.getCache().get(this.getPlayer().getUniqueId());
		MinetopiaUpgrades upgrades = user.getMinetopiaUpgrades();

		HashMap<Integer, Integer> map = hash();
		HashMap<Integer, Integer> map2 = hash2();

		HashMap<Upgrade, MenuScheme> menuSchemeHashMap = new HashMap<>();
		String EMPTY_DEFAULT = "000000000";
		int index = 1;
		for(Upgrade upgrade : Upgrade.values()) {
			int max = upgrade.getMax();
			StringBuilder string = new StringBuilder("000");
			for (int i = 0; i < max; i++)
				string.append("1");
			while(string.length() != 9)
				string.append("0");

			MenuScheme menuScheme = new MenuScheme();
			for (int i = 0; i < 6; i++) {
				if (i == index)
					menuScheme.mask(string.toString().length() > 9 ? string.substring(1, 9) : string.toString());
				else menuScheme.mask(EMPTY_DEFAULT);
			}

			menuSchemeHashMap.put(upgrade, menuScheme);
			index++;
		}

		HashMap<Upgrade, MenuScheme> sideIcons = new HashMap<>();
		int index2 = 1;
		for(Upgrade upgrade : Upgrade.values()) {
			MenuScheme pattern = new MenuScheme();
			for (int i = 0; i < 6; i++) {
				if (i == index2)
					pattern.mask("010000000");
				else pattern.mask(EMPTY_DEFAULT);
			}

			sideIcons.put(upgrade, pattern);
			index2++;
		}

		for (Upgrade upgrade : sideIcons.keySet()) {
			MenuScheme scheme = sideIcons.get(upgrade);
			MenuPopulator populator = scheme.newPopulator(this);

			String[] strings = ("\n" + upgrade.getTag()).split("\n");

			populator.accept(ItemStackBuilder.of(Material.IRON_PICKAXE)
					.durability(upgrade.getDurability()).name(Message.UPGRADES_GUI_LABEL.format(upgrade.getLabel()))
					.breakable(false).flag(ItemFlag.HIDE_UNBREAKABLE)
					.lore(strings).buildItem().build());
		}

		for (Upgrade upgrade : menuSchemeHashMap.keySet()) {
			MenuPopulator menuPopulator = menuSchemeHashMap.get(upgrade).newPopulator(this);
			for (int i = 0; i < upgrade.getMax(); i++) {
				int level = i + 1;
				ItemStack itemStack = ItemStackBuilder.of(Material.IRON_PICKAXE)
						.name(Message.UPGRADES_GUI_LEVEL.format(level))
						.durability(upgrades.getUpgrades().get(upgrade) >= level ? map2.get(level) : map.get(level))
						.lore("", upgrades.getUpgrades().get(upgrade) >= level ? Message.UPGRADES_GUI_UNLOCKED.raw() : Message.UPGRADES_GUI_LOCKED.raw())
						.breakable(false)
						.flag(ItemFlag.HIDE_UNBREAKABLE)
						.build();
				menuPopulator.accept(ItemStackBuilder.of(itemStack).build(() -> {
					if (upgrades.getUpgrades().get(upgrade) != (level - 1)) {
						if (!(upgrades.getUpgrades().get(upgrade) >= level))
							getPlayer().sendMessage(Message.UPGRADES_GUI_ERROR_BUYPREVIOUS.raw());
						return;
					}

					if (upgrades.getPoints() <= 0) {
						getPlayer().sendMessage(Message.UPGRADES_GUI_ERROR_NOTENOUGHTOKENS.raw());
						return;
					}

					upgrades.setPoints(upgrades.getPoints() - 1);
					upgrades.getUpgrades().put(upgrade, level);
					getPlayer().sendMessage(Message.UPGRADES_GUI_SUCCESS_BUY.format(upgrade.getLabel(), level));
				}));
			}
		}

		MenuScheme close = new MenuScheme()
				.mask("000010000")
				.mask(EMPTY_DEFAULT)
				.mask(EMPTY_DEFAULT)
				.mask(EMPTY_DEFAULT)
				.mask(EMPTY_DEFAULT)
				.mask(EMPTY_DEFAULT);
		MenuPopulator closePop = close.newPopulator(this);
		while(closePop.hasSpace())
			closePop.accept(ItemStackBuilder.of(Material.BARRIER).name(Message.COMMON_GUI_CLOSEMENU.raw())
			.build(this::close));

		MenuScheme viewPoints = new MenuScheme()
				.mask("001000000")
				.mask(EMPTY_DEFAULT)
				.mask(EMPTY_DEFAULT)
				.mask(EMPTY_DEFAULT)
				.mask(EMPTY_DEFAULT)
				.mask(EMPTY_DEFAULT);
		MenuPopulator viewPop = viewPoints.newPopulator(this);
		while(viewPop.hasSpace())
			viewPop.accept(ItemStackBuilder.of(Material.SUGAR)
			.name(Message.UPGRADES_GUI_TOKENINFO_LABEL.format(upgrades.getPoints()))
			.lore(Message.UPGRADES_GUI_TOKENINFO_LORE.formatAsList()).buildItem().build());

		for (int i = 0; i < (6 * 9); i++) {
			Slot slot = this.getSlot(i);
			if (!slot.hasItem()) {
				slot.setItem(ItemStackBuilder.of(Material.STAINED_GLASS_PANE)
				.durability(7).name(" ").build());
			}
		}
	}

	private HashMap<Integer, Integer> hash() {
		int d = Material.IRON_PICKAXE.getMaxDurability();
		HashMap<Integer, Integer> hashMap = new HashMap<>();
		hashMap.put(1, (int) Math.round(0.168*d));
		hashMap.put(2, (int) Math.round(0.172*d));
		hashMap.put(3, (int) Math.round(0.176*d));
		hashMap.put(4, (int) Math.round(0.18*d));
		hashMap.put(5, (int) Math.round(0.184*d));
		return hashMap;
	}

	private HashMap<Integer, Integer> hash2() {
		int d = Material.IRON_PICKAXE.getMaxDurability();
		HashMap<Integer, Integer> hashMap = new HashMap<>();
		hashMap.put(1, (int) Math.round(0.02*d));
		hashMap.put(2, (int) Math.round(0.024*d));
		hashMap.put(3, (int) Math.round(0.028*d));
		hashMap.put(4, (int) Math.round(0.032*d));
		hashMap.put(5, (int) Math.round(0.036*d));
		return hashMap;
	}

}
