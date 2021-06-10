package dev.maiky.minetopia.modules.levels.plots;

import lombok.Getter;

/**
 * Door: Maiky
 * Info: Minetopia - 24 May 2021
 * Package: dev.maiky.minetopia.modules.levels.plots
 */

public class IPlot {

	@Getter
	private final String id;

	public IPlot(String id) {
		this.id = id;
	}
}
