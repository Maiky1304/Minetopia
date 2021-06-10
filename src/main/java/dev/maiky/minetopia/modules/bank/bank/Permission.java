package dev.maiky.minetopia.modules.bank.bank;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Door: Maiky
 * Info: Minetopia - 30 May 2021
 * Package: dev.maiky.minetopia.modules.bank.bank
 */

public enum Permission {

	DEPOSIT("Stortrecht"),
	WITHDRAW("Opnamerecht"),
	ALL("Alle rechten");

	@Getter
	String label;

	Permission(String label) {
		this.label = label;
	}

	public static List<String> list() {
		List<String> list = new ArrayList<>();
		Iterator<Permission> bagTypeIterator = createIterator();
		while (bagTypeIterator.hasNext())
			list.add(bagTypeIterator.next().toString());
		return list;
	}

	public static Iterator<Permission> createIterator() {
		return new ArrayList<>(Arrays.asList(values())).iterator();
	}

	public static Permission opposite(Permission permission) {
		if ( permission == DEPOSIT )
			return WITHDRAW;
		else if ( permission == WITHDRAW )
			return DEPOSIT;
		else return null;
	}

}