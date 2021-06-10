package dev.maiky.minetopia.modules.bags.bag;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

/**
 * Door: Maiky
 * Info: Minetopia - 29 May 2021
 * Package: dev.maiky.minetopia.modules.bags.bag
 */

public class Bag {

	@Getter @Setter
	private String base64Contents;
	@Getter @Setter
	private int id = -1;
	@Getter
	private final HashMap<String, String> history = new HashMap<>();
	@Getter
	private final int rows;
	@Getter
	private final BagType type;

	public Bag(String base64Contents, int rows, BagType type) {
		this.base64Contents = base64Contents;
		this.rows = rows;
		this.type = type;
	}

}
