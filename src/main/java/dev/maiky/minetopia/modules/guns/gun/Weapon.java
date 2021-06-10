package dev.maiky.minetopia.modules.guns.gun;

import dev.maiky.minetopia.modules.guns.models.interfaces.Model;
import dev.maiky.minetopia.util.Text;
import lombok.Getter;
import lombok.Setter;

/**
 * Door: Maiky
 * Info: Minetopia - 09 Jun 2021
 * Package: dev.maiky.minetopia.modules.guns.gun
 */

public class Weapon {

	@Getter
	private final String license;
	@Getter @Setter
	private int ammo;
	@Getter @Setter
	private int durability;
	@Getter
	private final String modelName;

	@Getter @Setter
	private int rowId;

	public Weapon(Model model) {
		this.license = Text.randomString(12);
		this.ammo = model.defaultAmmo();
		this.durability = model.defaultAmmo() * 3;
		this.modelName = model.modelName();
	}

}
