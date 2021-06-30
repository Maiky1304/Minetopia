package dev.maiky.minetopia.modules.items.police;

import dev.maiky.minetopia.util.Message;
import me.lucko.helper.Schedulers;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Door: Maiky
 * Info: BlackMT-MinetopiaCore - 06 Apr 2021
 * Package: nl.blackminetopia.minetopia.modules.pvp.events
 */

public class Taser implements Listener {

	@EventHandler
	public void onUseTaser(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		Action action = event.getAction();

		if (!action.toString().startsWith("RIGHT_CLICK")) {
			return;
		}

		if (event.getHand() != EquipmentSlot.HAND) return;

		PlayerInventory playerInventory = event.getPlayer().getInventory();
		if (playerInventory.getItemInMainHand() == null) return;
		ItemStack itemInHand = playerInventory.getItemInMainHand();
		if (itemInHand.getType() != Material.IRON_HOE) return;
		String displayName = itemInHand.getItemMeta().getDisplayName();
		if (displayName == null || !(displayName.startsWith("§e[") && displayName.endsWith("§e]"))) {
			ItemMeta meta = itemInHand.getItemMeta();
			meta.setDisplayName(buildProgressBar(9));
			itemInHand.setItemMeta(meta);
			playerInventory.setItemInMainHand(itemInHand);
			handleFire(p);
			playerInventory.setItemInMainHand(setCooldown(itemInHand, 8));
		} else {
			Response response = checkCooldown(p, itemInHand);
			if (response == Response.TAKE) {
				net.minecraft.server.v1_12_R1.ItemStack nms = CraftItemStack.asNMSCopy(itemInHand);
				NBTTagCompound tagCompound = nms.getTag() == null ? new NBTTagCompound() : nms.getTag();
				tagCompound.remove("cooldownTaser");
				nms.setTag(tagCompound);
				playerInventory.setItemInMainHand(CraftItemStack.asCraftMirror(nms));
			} else if (response == Response.STILL) return;
			int green = getLoaded(displayName);

			if (green == 0) {
				p.sendMessage(Message.ITEMS_TASER_EMPTY.raw());
				return;
			}

			ItemMeta meta = itemInHand.getItemMeta();
			meta.setDisplayName(buildProgressBar(green - 1));
			itemInHand.setItemMeta(meta);
			playerInventory.setItemInMainHand(itemInHand);
			handleFire(p);
			playerInventory.setItemInMainHand(setCooldown(itemInHand, 8));
		}
	}

	public Response checkCooldown(Player p, ItemStack itemStack) {
		net.minecraft.server.v1_12_R1.ItemStack nms = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound tagCompound = nms.getTag() == null ? new NBTTagCompound() : nms.getTag();
		if (!tagCompound.hasKey("cooldownTaser")) return Response.NOPE;
		String cooldown = tagCompound.getString("cooldownTaser");
		long l = Long.parseLong(cooldown);
		if (l > System.currentTimeMillis()) {
			double time = System.currentTimeMillis();
			double diff = l - time;
			double secs = diff / 1000;
			p.sendMessage(Message.ITEMS_TASER_COOLDOWN.format(new DecimalFormat("0.0").format(secs)));
			return Response.STILL;
		}
		return Response.TAKE;
	}

	public enum Response {
		TAKE,STILL,NOPE;
	}

	public ItemStack setCooldown(ItemStack itemStack, int seconds) {
		net.minecraft.server.v1_12_R1.ItemStack nms = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound tagCompound = nms.getTag() == null ? new NBTTagCompound() : nms.getTag();
		tagCompound.setString("cooldownTaser", String.valueOf(getDate(Calendar.SECOND, seconds).getTime()));
		nms.setTag(tagCompound);
		return CraftItemStack.asCraftMirror(nms);
	}

	public Date getDate(int unit, int unitFromNow) {
		Date now = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		calendar.add(unit, unitFromNow);
		return calendar.getTime();
	}

	public void handleFire(Player p) {
		final Arrow arrow = p.launchProjectile(Arrow.class, p.getLocation().getDirection().multiply(1.5d));
		arrow.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
		arrow.setCustomName("TASER_BULLET_PROJECTILE");
		arrow.setShooter(p);
		arrow.setKnockbackStrength(0);
		Schedulers.sync().runLater(() -> p.getWorld().spawnParticle(Particle.CRIT, arrow.getLocation(), 7), 20);
	}

	public int getLoaded(String string) {
		string = string.substring(3, string.length()-3);
		String[] strings = string.split("\\|");

		int loaded = 0;
		for (String s : strings) {
			if (s.equals("§c")) continue;
			loaded++;
		}

		return loaded;
	}

	public String buildProgressBar(int loaded) {
		int max = 10;
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < loaded; i++)
			builder.append("§a|");
		for (int i = 0; i < (max - loaded); i++)
			builder.append("§c|");
		return String.format("§e[%s§e]", builder.toString());
	}

	@EventHandler
	public void onTazerHit(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		if (!(event.getDamager() instanceof Arrow))
			return;
		if (!event.getDamager().getCustomName().equals("TASER_BULLET_PROJECTILE"))
			return;
		Player target = (Player) event.getEntity();
		Arrow arrow = (Arrow) event.getDamager();
		Player shooter = (Player) arrow.getShooter();
		shooter.sendMessage(Message.ITEMS_TASER_HIT.format(target.getName()));
		target.sendMessage(Message.ITEMS_TASER_HITTED.format(shooter.getName()));

		if (target.hasPotionEffect(PotionEffectType.BLINDNESS))
			target.removePotionEffect(PotionEffectType.BLINDNESS);
		target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 100));

		Firework firework = (Firework) target.getWorld().spawnEntity(target.getLocation(), EntityType.FIREWORK);
		FireworkMeta meta = firework.getFireworkMeta();
		meta.setPower(1);
		meta.addEffect(FireworkEffect.builder()
				.flicker(true)
				.trail(true)
				.withColor(Color.RED)
				.withFade(Color.WHITE)
				.build());
		firework.setFireworkMeta(meta);
		event.setDamage(0.0D);
	}

	@EventHandler
	public void onHit(ProjectileHitEvent event) {
		Projectile projectile = event.getEntity();
		if (!(projectile instanceof Arrow))
			return;
		if (projectile.getCustomName().equals("TASER_BULLET_PROJECTILE"))
			projectile.remove();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		if (p.hasPotionEffect(PotionEffectType.BLINDNESS)
		&& p.getPotionEffect(PotionEffectType.BLINDNESS).getAmplifier() == 100) {
			event.setCancelled(true);
		}
	}

}
