package dev.maiky.minetopia.modules.items.displays;

import dev.maiky.minetopia.modules.data.managers.PlayerManager;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import dev.maiky.minetopia.util.Message;
import dev.maiky.minetopia.util.Skull;
import me.lucko.helper.cooldown.Cooldown;
import me.lucko.helper.cooldown.CooldownMap;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Slot;
import me.lucko.helper.menu.scheme.MenuScheme;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

/**
 * Door: Maiky
 * Info: Minetopia - 24 May 2021
 * Package: dev.maiky.minetopia.modules.items.displays
 */

public class RadioUI extends Gui {

	public static CooldownMap<Player> cooldownMap = CooldownMap.create(Cooldown.of(1, TimeUnit.MINUTES));
	public static CooldownMap<Player> policeChatToggle = CooldownMap.create(Cooldown.of(1, TimeUnit.SECONDS));

	private final MinetopiaUser user;

	public RadioUI(Player player) {
		super(player, 3, "&3Portofoon");
		this.user = PlayerManager.getCache().get(player.getUniqueId());
	}

	private final MenuScheme EMERGENCY = new MenuScheme()
			.mask("000000000")
			.mask("010000000")
			.mask("000000000");

	private final MenuScheme POLICECHAT = new MenuScheme()
			.mask("000000000")
			.mask("000100000")
			.mask("000000000");

	@Override
	public void redraw() {
		ItemStack emergencyButton = Skull.getCustomSkull("884e92487c6749995b79737b8a9eb4c43954797a6dd6cd9b4efce17cf475846");
		emergencyButton = ItemStackBuilder.of(emergencyButton)
				.name("&c&lNoodknop").lore("", "&7Gebruik deze knop &4&nALLEEN&7 voor noodgevallen!").build();
		EMERGENCY.newPopulator(this).accept(ItemStackBuilder.of(emergencyButton).build(this::emergencyButton));

		ItemStack policeChat = Skull.getCustomSkull("67157cffb06063b352dc68478f476e7d202c3ba6e7cbf297241be81681074bf");
		policeChat = ItemStackBuilder.of(policeChat)
				.name("&9&lPolitiechat").lore("", "&7Zet hiermee de politiechat " + (user.isPoliceChat() ? "&cuit" : "&aaan") + "&7.").build();
		POLICECHAT.newPopulator(this).accept(ItemStackBuilder.of(policeChat).build(this::policeChat));

		for (int i = 0; i < 27; i++) {
			Slot slot = this.getSlot(i);
			if (slot.hasItem()) continue;
			slot.setItem(ItemStackBuilder.of(Material.STAINED_GLASS_PANE).name(" ").durability(7).build());
		}
	}

	private void policeChat() {
		if (!policeChatToggle.test(getPlayer())) {
			getPlayer().sendMessage(Message.ITEMS_POLICE_CHATCOOLDOWN.format(policeChatToggle.get(getPlayer()).remainingTime(TimeUnit.SECONDS)));
			return;
		}

		this.user.setPoliceChat(!this.user.isPoliceChat());
		this.redraw();

		getPlayer().sendMessage(Message.ITEMS_POLICE_CHATSTATUS.format(user.isPoliceChat() ? "ingeschakeld" : "uitgeschakeld"));
	}

	private void emergencyButton() {
		getPlayer().sendMessage(Message.ITEMS_POLICE_EMERGENCYBUTTON.raw());
		/*
		if (!cooldownMap.test(getPlayer())) {
			getPlayer().sendMessage("§cJe moet nog " + cooldownMap.get(getPlayer()).remainingTime(TimeUnit.SECONDS) +
					" seconden wachten voordat je dit weer kunt doen.");
			return;
		}

		Gson gson = new Gson();
		Location l = getPlayer().getLocation();
		Emergency emergency = new Emergency("X: " + ((int)l.getX()) + ", Z: " + ((int)l.getZ()) + ", " + l.getWorld().getName());
		RadioMessage message = new RadioMessage(getPlayer().getName(), gson.toJson(emergency), Type.EMERGENCY);
		String json = gson.toJson(message);

		Redis redis = DataModule.getInstance().getRedis();
		redis.getJedisPool().getResource().publish("mt-radio", json);

		 */
	}

}
