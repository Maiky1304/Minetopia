package dev.maiky.minetopia.modules.items.threads;

import com.google.gson.Gson;
import dev.maiky.minetopia.modules.items.threads.message.Emergency;
import dev.maiky.minetopia.modules.items.threads.message.RadioMessage;
import dev.maiky.minetopia.modules.items.threads.message.Type;
import dev.maiky.minetopia.util.Items;
import dev.maiky.minetopia.util.Sounds;
import me.lucko.helper.redis.Redis;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * Door: Maiky
 * Info: Minetopia - 24 May 2021
 * Package: dev.maiky.minetopia.modules.items.threads
 */

public class RadioThread extends Thread {

	private final Jedis subscriber;
	private final String[] channels = {"mt-radio"};
	private final Gson gson = new Gson();

	public RadioThread(Redis redis) {
		super("Radio Listener");
		this.subscriber = redis.getJedisPool().getResource();
	}

	@Override
	public void run() {
		this.subscriber.subscribe(new JedisPubSub() {
			@Override
			public void onMessage(String channel, String message) {
				if (channel.equals(channels[0])) {
					RadioMessage radioMessage = gson.fromJson(message, RadioMessage.class);

					if (radioMessage.getType() == Type.MESSAGE) {
						String msg = radioMessage.getData();

						for (Player p : Bukkit.getOnlinePlayers()) {
							if (Items.hasItemMatches(p, Material.DIAMOND_HOE, (short) 67)) {
								p.sendMessage("§3[§bPolitiechat§3] §7" + radioMessage.getFrom() + "§f: " + msg);
							}
						}
					} else if (radioMessage.getType() == Type.EMERGENCY) {
						Emergency emergency = gson.fromJson(radioMessage.getData(), Emergency.class);
						String location = emergency.getLocation();

						for (Player p : Bukkit.getOnlinePlayers()) {
							if (Items.hasItemMatches(p, Material.DIAMOND_HOE, (short) 67)) {
								p.sendMessage("§c[§4§lNoodknop§c] §4" + radioMessage.getFrom() + " §fheeft op de noodknop gedrukt! (§c"
								+ location + "§f)");
								Sounds.playSoundRepeating(p, Sound.UI_BUTTON_CLICK, 0.75f, 5, 10);
							}
						}
					}
				}
			}
		}, channels);
	}

}