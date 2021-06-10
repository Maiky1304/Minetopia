package dev.maiky.minetopia.modules.data.managers;

import com.google.gson.Gson;
import dev.maiky.minetopia.modules.guns.gun.Weapon;
import dev.maiky.minetopia.modules.guns.models.interfaces.Model;
import me.lucko.helper.sql.Sql;

/**
 * Door: Maiky
 * Info: Minetopia - 09 Jun 2021
 * Package: dev.maiky.minetopia.modules.data.managers
 */

public class WeaponManager {

	private final Sql sql;
	private final Gson gson;

	public static WeaponManager with(Sql sql) {
		return new WeaponManager(sql);
	}

	private WeaponManager(Sql sql) {
		this.sql = sql;
		this.gson = new Gson();
	}

	public void updateWeapon(Weapon weapon) {
		this.sql.execute("UPDATE `weapons` SET `json`=? WHERE `id`=?",
				preparedStatement ->
				{
					preparedStatement.setString(1, this.gson.toJson(weapon));
					preparedStatement.setInt(2, weapon.getRowId());
				});
	}

	public Weapon createWeapon(Model model) {
		Weapon weapon = new Weapon(model);
		String json = this.gson.toJson(weapon);
		this.sql.execute("INSERT INTO `weapons`(`json`) VALUES(?)",
				preparedStatement -> preparedStatement.setString(1, json));
		return this.sql.query("SELECT max(id) FROM `weapons`", preparedStatement -> {},
				rs ->
				{
					if (rs.next()) {
						weapon.setRowId(rs.getInt(1));
						this.sql.execute("UPDATE `weapons` SET `json`=? WHERE `json` LIKE ?",
								preparedStatement ->
								{
									preparedStatement.setString(1, this.gson.toJson(weapon));
									preparedStatement.setString(2, "%" + weapon.getLicense() + "%");
								});
						return weapon;
					}

					return null;
				}).orElse(null);
	}

	public Weapon getWeaponByLicense(String license) {
		return this.sql.query("SELECT `json`,`id` FROM `weapons` WHERE `json` LIKE ?",
				ps -> ps.setString(1, "%" + license + "%"),
				rs ->
				{
					if (rs.next()) {
						String json = rs.getString("json");
						Weapon weapon = this.gson.fromJson(json, Weapon.class);
						weapon.setRowId(rs.getInt("id"));
						return weapon;
					}

					return null;
				}).orElse(null);
	}

}
