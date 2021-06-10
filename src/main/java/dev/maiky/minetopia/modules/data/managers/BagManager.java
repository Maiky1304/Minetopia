package dev.maiky.minetopia.modules.data.managers;

import com.google.gson.Gson;
import dev.maiky.minetopia.modules.bags.bag.Bag;
import dev.maiky.minetopia.modules.bags.bag.BagType;
import dev.maiky.minetopia.util.SerializationUtils;
import me.lucko.helper.sql.Sql;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Door: Maiky
 * Info: Minetopia - 26 May 2021
 * Package: dev.maiky.minetopia.modules.data.managers
 */

public class BagManager {

	private final Sql sql;
	private final Gson gson;

	public static BagManager with(Sql sql) {
		return new BagManager(sql);
	}

	private BagManager(Sql sql) {
		this.sql = sql;
		this.gson = new Gson();
	}

	public void saveBag(Bag bag) {
		String json = this.gson.toJson(bag);

		this.sql.execute("UPDATE `bags` SET `json`=? WHERE `id`=?",
				preparedStatement ->
				{
					preparedStatement.setString(1, json);
					preparedStatement.setInt(2, bag.getId());
				});
	}

	public Bag getBag(int id) {
		return this.sql.query("SELECT `json` FROM `bags` WHERE `id`=?",
				preparedStatement ->
				{
					preparedStatement.setInt(1, id);
				}, resultSet ->
				{
					if ( resultSet.next() ) {
						String json = resultSet.getString("json");
						Bag bag = this.gson.fromJson(json, Bag.class);
						bag.setId(id);
						return bag;
					}

					return null;
				}).orElse(null);
	}

	public Bag createBag(BagType bagType, int rows) {
		Bag bag = new Bag(SerializationUtils.itemStackArrayToBase64(new ItemStack[0]), rows, bagType);
		String json = this.gson.toJson(bag);

		this.sql.execute("INSERT INTO `bags`(`json`) VALUES(?)",
				preparedStatement ->
						preparedStatement.setString(1, json));

		return this.sql.query("SELECT max(id) FROM `bags`", preparedStatement -> {
				},
				resultSet ->
				{
					if ( resultSet.next() ) {
						int id = resultSet.getInt(1);
						bag.setId(id);
					}

					return bag;
				}).orElse(bag);
	}

	public List<Bag> getBags() {
		List<Bag> bags = new ArrayList<>();

		return this.sql.query("SELECT * FROM `bags` WHERE 1",
				preparedStatement -> {
				},
				resultSet ->
				{
					while (resultSet.next()) {
						String json = resultSet.getString("json");
						Bag bag = this.gson.fromJson(json, Bag.class);
						bags.add(bag);
					}

					return bags;
				}).orElse(bags);
	}

}
