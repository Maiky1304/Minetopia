package dev.maiky.minetopia.modules.items.threads.message;

import lombok.Getter;

/**
 * Door: Maiky
 * Info: Minetopia - 24 May 2021
 * Package: dev.maiky.minetopia.modules.items.threads.message
 */

public class Emergency {

	@Getter
	private final String location;

	public Emergency(String location) {
		this.location = location;
	}
}
