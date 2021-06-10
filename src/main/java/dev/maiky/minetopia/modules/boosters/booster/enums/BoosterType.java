package dev.maiky.minetopia.modules.boosters.booster.enums;

import dev.maiky.minetopia.modules.bank.bank.Bank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Door: Maiky
 * Info: Minetopia - 07 Jun 2021
 * Package: dev.maiky.minetopia.modules.boosters.booster
 */

public enum BoosterType {

	BLACKSHARD;

	public static List<String> list() {
		List<String> list = new ArrayList<>();
		Iterator<BoosterType> bagTypeIterator = createIterator();
		while(bagTypeIterator.hasNext())
			list.add(bagTypeIterator.next().toString());
		return list;
	}

	public static Iterator<BoosterType> createIterator() {
		return new ArrayList<>(Arrays.asList(values())).iterator();
	}

}
