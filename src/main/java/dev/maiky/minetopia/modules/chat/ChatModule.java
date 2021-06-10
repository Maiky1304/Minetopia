package dev.maiky.minetopia.modules.chat;

import com.google.gson.Gson;
import dev.maiky.minetopia.MinetopiaModule;
import dev.maiky.minetopia.modules.colors.fonts.FontSet;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.PlayerManager;
import dev.maiky.minetopia.modules.items.threads.message.RadioMessage;
import dev.maiky.minetopia.modules.items.threads.message.Type;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import dev.maiky.minetopia.util.Text;
import me.lucko.helper.Events;
import me.lucko.helper.redis.Redis;
import me.lucko.helper.terminable.composite.CompositeClosingException;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Door: Maiky
 * Info: Minetopia - 22 May 2021
 * Package: dev.maiky.minetopia.modules.chat
 */

public class ChatModule implements MinetopiaModule {

	private final CompositeTerminable composite = CompositeTerminable.create();

	private boolean enabled;

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	@Override
	public void reload() {
		this.disable();
		this.enable();
	}

	@Override
	public void enable() {
		this.enabled = true;

		// Register Events
		this.registerEvents();
	}

	private void registerEvents() {
		Events.subscribe(AsyncPlayerChatEvent.class, EventPriority.HIGH)
		.filter(e -> PlayerManager.getCache().containsKey(e.getPlayer().getUniqueId()))
		.filter(e -> !PlayerManager.getCache().get(e.getPlayer().getUniqueId()).isPoliceChat())
		.filter(e -> !e.isCancelled())
		.handler(e -> {
			e.setMessage(e.getMessage().replaceAll("%", "%%"));

			e.getRecipients().clear();
			for (Player p : Bukkit.getOnlinePlayers()) {
				Location pLoc = p.getLocation();
				double distance = pLoc.distance(e.getPlayer().getLocation());
				if (distance <= 16)
					e.getRecipients().add(p);
			}

			MinetopiaUser user = PlayerManager.getCache().get(e.getPlayer().getUniqueId());
			String format = "&3[%s&3] &8[%s%s&8] %s%s%s: %s";
			String processed = String.format(Text.colors(format), Text.colors("&" + user.getCurrentLevelColor().getColor()
					+ (user.getCurrentLevelColor().font ? FontSet.process("Level " +  user.getLevel()) : "Level " +  user.getLevel())),
					Text.colors("&" + user.getCurrentPrefixColor().getColor()),
					user.getCurrentPrefixColor().font ? FontSet.process(user.getCurrentPrefix()) : user.getCurrentPrefix(), String.format("§%s", user.getCityColor()),
					e.getPlayer().getName(), Text.colors("&" + user.getCurrentChatColor().getColor()), user.getCurrentChatColor()
			.font ? FontSet.process(e.getMessage()) : e.getMessage());
			e.setFormat(processed);
		}).bindWith(composite);
		Events.subscribe(AsyncPlayerChatEvent.class, EventPriority.HIGH)
				.filter(e -> PlayerManager.getCache().containsKey(e.getPlayer().getUniqueId()))
				.filter(e -> PlayerManager.getCache().get(e.getPlayer().getUniqueId()).isPoliceChat())
				.handler(e -> {
					e.setCancelled(true);

					Gson gson = new Gson();
					RadioMessage radioMessage = new RadioMessage(e.getPlayer().getName(), e.getMessage(), Type.MESSAGE);
					String json = gson.toJson(radioMessage);

					Redis redis = DataModule.getInstance().getRedis();
					redis.getJedisPool().getResource().publish("mt-radio", json);
				}).bindWith(composite);
	}

	@Override
	public void disable() {
		this.enabled = false;

		try {
			this.composite.close();
		} catch (CompositeClosingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return "Chat";
	}
}
