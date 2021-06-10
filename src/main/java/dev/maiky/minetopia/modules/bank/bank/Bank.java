package dev.maiky.minetopia.modules.bank.bank;

import dev.maiky.minetopia.modules.bags.bag.BagType;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Door: Maiky
 * Info: Minetopia - 30 May 2021
 * Package: dev.maiky.minetopia.modules.bank.bank
 */

public enum Bank {

	BUSINESS("Zakelijke", ChatColor.AQUA, Material.DIAMOND_BLOCK),
	PERSONAL("Persoonlijke", ChatColor.GOLD, Material.GOLD_BLOCK),
	SAVINGS("Spaar", ChatColor.RED, Material.REDSTONE_BLOCK),
	GOVERNMENT("Overheids", ChatColor.GREEN, Material.EMERALD_BLOCK);

	public String label;
	public Material icon;
	public ChatColor color;

	Bank(String label, ChatColor color, Material icon) {
		this.label = label;
		this.color = color;
		this.icon = icon;
	}

	public static List<String> list() {
		List<String> list = new ArrayList<>();
		Iterator<Bank> bagTypeIterator = createIterator();
		while(bagTypeIterator.hasNext())
			list.add(bagTypeIterator.next().toString());
		return list;
	}

	public static Iterator<Bank> createIterator() {
		return new ArrayList<>(Arrays.asList(values())).iterator();
	}

}
