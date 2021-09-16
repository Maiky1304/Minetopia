package dev.maiky.minetopia.modules.bank.bank;

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

	BUSINESS("Zakelijke", ChatColor.AQUA, Material.IRON_INGOT, "bedrijfsrekening"),
	PERSONAL("Persoonlijke", ChatColor.GOLD, Material.IRON_INGOT, "priverekening"),
	SAVINGS("Spaar", ChatColor.RED, Material.IRON_INGOT, "spaarrekening"),
	GOVERNMENT("Overheids", ChatColor.GREEN, Material.IRON_INGOT, "staatsrekening");

	public final String label;
	public final Material icon;
	public final ChatColor color;
	public final String nbtTag;

	Bank(String label, ChatColor color, Material icon, String nbtTag) {
		this.label = label;
		this.icon = icon;
		this.color = color;
		this.nbtTag = nbtTag;
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
