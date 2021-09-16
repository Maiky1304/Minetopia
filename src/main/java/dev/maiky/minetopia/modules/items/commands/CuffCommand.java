package dev.maiky.minetopia.modules.items.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.util.Message;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

import static org.bukkit.potion.PotionEffectType.*;

/**
 * This project is owned by Maiky Perlee - © 2021
 */

@CommandAlias("handboei|cuff|handcuff")
@Description("Zet iemand in de handboeien!")
public class CuffCommand extends BaseCommand {

    @HelpCommand
    public void onHelp(CommandSender sender) {
        Minetopia.showHelp(sender, this, getSubCommands());
    }

    @CatchUnknown
    public void onUnknown(CommandSender sender) {
        sender.sendMessage(Message.COMMON_COMMAND_UNKNOWNSUBCOMMAND.raw());
        this.onHelp(sender);
    }

    @Override
    public void showSyntax(CommandIssuer issuer, RegisteredCommand<?> cmd) {
        issuer.sendMessage(Message.COMMON_COMMAND_SYNTAX.format(getExecCommandLabel(), cmd.getPrefSubCommand(), cmd.getSyntaxText()));
    }

    @Default
    @Subcommand("main")
    @Syntax("<player>")
    @CommandPermission("minetopia.job.police")
    @CommandCompletion("@players")
    public void onMain(Player player, OfflinePlayer target) {
        if (target == null) throw new ConditionFailedException(Message.COMMON_ERROR_PLAYEROFFLINE.raw());
        if (!target.isOnline()) throw new ConditionFailedException(Message.COMMON_ERROR_PLAYEROFFLINE.raw());

        Player entity = target.getPlayer();

        List<MetadataValue> metadata = entity.getMetadata("cuffed");
        boolean cuffed = false;
        if (!metadata.isEmpty()) {
            MetadataValue value = metadata.iterator().next();
            if (value.value() instanceof Boolean)
                cuffed = value.asBoolean();
        }

        Minetopia minetopia = Minetopia.getPlugin(Minetopia.class);
        entity.removePotionEffect(SLOW_DIGGING);
        entity.removePotionEffect(BLINDNESS);
        entity.removePotionEffect(SLOW);
        entity.removePotionEffect(REGENERATION);

        if (cuffed) {
            entity.removeMetadata("cuffed", minetopia);
            entity.removeMetadata("cuffedBy", minetopia);

            entity.sendMessage(Message.ITEMS_POLICE_UNCUFF.format(player.getName()));
            player.sendMessage(Message.ITEMS_POLICE_UNCUFFEXEC.format(entity.getName()));
        } else {
            entity.addPotionEffect(new PotionEffect(SLOW_DIGGING, Integer.MAX_VALUE, 0), true);
            entity.addPotionEffect(new PotionEffect(BLINDNESS, Integer.MAX_VALUE, 0), true);
            entity.addPotionEffect(new PotionEffect(SLOW, Integer.MAX_VALUE, 3), true);
            entity.addPotionEffect(new PotionEffect(REGENERATION, Integer.MAX_VALUE, 255), true);

            entity.setMetadata("cuffed", new FixedMetadataValue(minetopia, true));
            entity.setMetadata("cuffedBy", new FixedMetadataValue(minetopia, player.getUniqueId().toString()));

            entity.sendMessage(Message.ITEMS_POLICE_CUFF.format(player.getName()));
            player.sendMessage(Message.ITEMS_POLICE_CUFFEXEC.format(entity.getName()));

            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    if (entity.hasMetadata("cuffed")) {
                        if (!player.isOnline()) {
                            this.cancel();
                            return;
                        }

                        double d = 0.0D;
                        try {
                            d = player.getLocation().distance(entity.getLocation());
                            if (d >= 12.5D)
                                entity.teleport(player);
                        } catch (Exception ignored) {}

                        if (d >= 4.5D) {
                            Vector direction = player.getLocation().toVector().subtract(entity.getLocation().toVector()).setY(0).multiply(2).normalize();
                            entity.setVelocity(direction);
                        }
                    } else this.cancel();
                }
            };
            runnable.runTaskTimer(Minetopia.getPlugin(Minetopia.class), 0L, 5L);
        }
    }

}
