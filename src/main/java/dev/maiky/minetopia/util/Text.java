package dev.maiky.minetopia.util;

import org.bukkit.ChatColor;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Door: Maiky
 * Info: Minetopia - 22 May 2021
 * Package: dev.maiky.minetopia.util
 */

public class Text {

	public static String colors(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}
	public static List<String> colors(List<String> list) {
		List<String> copy = new ArrayList<>();
		list.forEach(s -> copy.add(colors(s)));
		return copy;
	}
	public static String strip(String string) {
		return ChatColor.stripColor(string);
	}

	public static String randomString(int length) {
		SecureRandom random = new SecureRandom();
		char[] chars = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm'
		, '1', '2', '3', '4', '5', '6'};
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < length; i++)
			builder.append(chars[random.nextInt(chars.length)]);
		return builder.toString();
	}

}
