package nl.me.easyclans.commands.clan_commands;

import nl.me.easyclans.commands.IClanCommand;
import nl.me.easyclans.configs.MessagesConfig;
import nl.me.easyclans.helpers.utils.MessageUtils;
import org.bukkit.entity.Player;

import java.util.List;

public class HelpCommand implements IClanCommand {
    @Override
    public boolean execute(Player player, String[] args) {
        if (!player.hasPermission("easyclans.clan.help") && !player.isOp()) return MessageUtils.onNoPermission(player, "easyclans.clan.help");
        return MessageUtils.onHelpMessage(player);
    }
}
