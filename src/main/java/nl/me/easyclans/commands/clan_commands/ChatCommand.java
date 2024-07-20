package nl.me.easyclans.commands.clan_commands;

import nl.me.easyclans.commands.IClanCommand;
import nl.me.easyclans.helpers.utils.MessageUtils;
import org.bukkit.entity.Player;

public class ChatCommand implements IClanCommand {
    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length < 2) return MessageUtils.onWrongUsage(player, "/clan chat <message>");
        String message = String.join(" ", args).substring(5);
        return MessageUtils.onClanChatMessage(player, message);
    }
}
