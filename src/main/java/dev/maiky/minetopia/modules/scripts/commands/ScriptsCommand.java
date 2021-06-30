package dev.maiky.minetopia.modules.scripts.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.scripts.ScriptsModule;
import dev.maiky.minetopia.util.Message;
import me.lucko.scriptcontroller.environment.script.Script;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.nio.file.Path;
import java.util.Map;

/**
 * This project is owned by Maiky Perlee - © 2021
 */

@CommandAlias("scripts")
@CommandPermission("minetopia.admin")
public class ScriptsCommand extends BaseCommand {

    private ScriptsModule scriptsModule;

    public ScriptsCommand(ScriptsModule scriptsModule) {
        this.scriptsModule = scriptsModule;
    }

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
        issuer.sendMessage("§cGebruik: /" + this.getExecCommandLabel() + " " +
                cmd.getPrefSubCommand() + " " +
                cmd.getSyntaxText());
    }

    @Default
    @Subcommand("main")
    @Description("See the loaded addons")
    public void onMain(CommandSender sender) {
        StringBuilder builder = new StringBuilder();
        Map<Path, Script> scriptMap = scriptsModule.getEnvironment().getScriptRegistry().getAll();

        for (Path path : scriptMap.keySet()) {
            builder.append(ChatColor.GREEN).append(path).append(ChatColor.RESET).append(Message.SCRIPTS_SEPERATOR.raw()).append(" ");
        }

        String raw = builder.substring(0, builder.length()-2);
        sender.sendMessage(Message.SCRIPTS_VIEW.format(scriptMap.size(), raw));
    }

}
