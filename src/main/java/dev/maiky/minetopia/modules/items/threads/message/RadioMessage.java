package dev.maiky.minetopia.modules.items.threads.message;

import lombok.Getter;

/**
 * Door: Maiky
 * Info: Minetopia - 24 May 2021
 * Package: dev.maiky.minetopia.modules.items.threads.message
 */

public class RadioMessage {

	@Getter
	private final String from;
	@Getter
	private final String data;
	@Getter
	private final Type type;

	public RadioMessage(String from, String data, Type type) {
		this.from = from;
		this.data = data;
		this.type = type;
	}

}
