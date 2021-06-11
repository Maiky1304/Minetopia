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

	@Getter
	private String message = null;

	public Emergency(String location) {
		this.location = location;
	}

	public Emergency(String location, String message) {
		this.location = location;
		this.message= message;
	}
}
