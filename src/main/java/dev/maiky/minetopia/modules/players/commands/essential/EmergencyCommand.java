package dev.maiky.minetopia.modules.players.commands.essential;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import com.google.gson.Gson;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.items.threads.message.Emergency;
import dev.maiky.minetopia.modules.items.threads.message.RadioMessage;
import dev.maiky.minetopia.modules.items.threads.message.Type;
import me.lucko.helper.redis.Redis;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Door: Maiky
 * Info: Minetopia - 11 Jun 2021
 * Package: dev.maiky.minetopia.modules.players.commands.essential
 */

@CommandAlias("emergency|112|emergency")
@CommandPermission("minetopia.common.emergency")
public class EmergencyCommand extends BaseCommand {

	@HelpCommand
	public void onHelp(CommandSender sender) {
		Minetopia.showHelp(sender, this, getSubCommands());
	}

	@CatchUnknown
	public void onUnknown(CommandSender sender) {
		sender.sendMessage("§cUnknown subcommand");
		this.onHelp(sender);
	}

	@Override
	public void showSyntax(CommandIssuer issuer, RegisteredCommand<?> cmd) {
		issuer.sendMessage("§cGebruik: /" + this.getExecCommandLabel() + " " +
				cmd.getPrefSubCommand() + " " +
				cmd.getSyntaxText());
	}

	@Default
	@Subcommand("main")
	@Syntax("<message>")
	@Description("Send an emergency notification to all cops.")
	public void onMain(Player player, String... message) {
		if (message.length == 0) {
			this.showSyntax(getCurrentCommandIssuer(), getDefaultRegisteredCommand());
			return;
		}

		StringBuilder builder = new StringBuilder();
		for(String s : message)
			builder.append(s).append(" ");
		String rawMessage = builder.substring(0, builder.length()-1);

		Gson gson = new Gson();
		Location l = player.getLocation();
		Emergency emergency = new Emergency("X: " + ((int)l.getX()) + ", Z: " + ((int)l.getZ()) + ", " + l.getWorld().getName(),
				rawMessage);
		RadioMessage radioMessage = new RadioMessage(player.getName(), gson.toJson(emergency), Type.ALERT);
		String json = gson.toJson(radioMessage);

		Redis redis = DataModule.getInstance().getRedis();
		redis.getJedisPool().getResource().publish("mt-radio", json);
	}

}
