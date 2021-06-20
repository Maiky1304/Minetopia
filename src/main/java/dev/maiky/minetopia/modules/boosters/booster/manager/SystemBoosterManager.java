package dev.maiky.minetopia.modules.boosters.booster.manager;

import dev.maiky.minetopia.modules.boosters.booster.enums.BoosterType;
import dev.maiky.minetopia.modules.data.DataModule;
import redis.clients.jedis.Jedis;

/**
 * Door: Maiky
 * Info: Minetopia - 17 Jun 2021
 * Package: dev.maiky.minetopia.modules.boosters.booster.manager
 */

public class SystemBoosterManager {

	private final static Jedis jedis;

	static {
		jedis = DataModule.getInstance().getRedis().getJedis();
	}

	public static void update(String owner, BoosterType type, int percentage) {
		jedis.set(type.toString(), String.valueOf(percentage));
		jedis.set("expiry:" + type.toString(), String.valueOf((System.currentTimeMillis() + 3600000L)));
		jedis.set("last:" + type.toString(), owner);
	}

	public static int get(BoosterType type) {
		if (!jedis.exists(type.toString())) return 0;
		return Integer.parseInt(jedis.get(type.toString()));
	}

	public static long getExpiry(BoosterType type) {
		if (!jedis.exists("expiry:" + type.toString())) return 0L;
		return Long.parseLong(jedis.get("expiry:" + type.toString()));
	}

	public static String getLastUser(BoosterType type) {
		if (!jedis.exists("last:" + type.toString())) return null;
		return jedis.get("last:" + type.toString());
	}

}
