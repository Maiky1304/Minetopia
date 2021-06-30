/*
 * This file is part of Minetopia.
 *
 *  Copyright (c) Maiky1304 (Maiky) <maiky@blackmt.nl>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package dev.maiky.minetopia.modules.players.commands.essential;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.util.Message;
import dev.maiky.minetopia.util.Options;
import org.bukkit.command.CommandSender;

@CommandAlias("minetopia|sdb|minetopiasdb|mtcore|minetopiacore|mcore|maikydev")
public class MinetopiaCommand extends BaseCommand {

	@Default
	@HelpCommand
	public void onHelp(CommandSender sender) {
		final Minetopia minetopia = Minetopia.getInstance();
		sender.sendMessage("§3Deze server maakt gebruik van §b" + minetopia.getDescription().getName() + " §3met de versie §b" + minetopia.getDescription().getVersion() + "§3.");
		sender.sendMessage("§3De eigenaar van deze license is §b?§3.");
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

	@Subcommand("reload")
	@Description("Reload de configuratie")
	@CommandPermission("minetopia.admin")
	public void onReload(CommandSender sender) {
		Minetopia.getInstance().getMessages().reload();
		Minetopia.getInstance().getConfiguration().reload();
		Options.loadAll();
		Message.loadAll();
		Minetopia.getInstance().reloadModules();
		sender.sendMessage(Message.PLAYER_SUCCESSFULLY_RELOAD.raw());
	}

}
