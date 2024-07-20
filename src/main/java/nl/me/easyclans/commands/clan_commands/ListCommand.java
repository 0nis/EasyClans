package nl.me.easyclans.commands.clan_commands;

import nl.me.easyclans.commands.IClanCommand;
import nl.me.easyclans.helpers.utils.ClanUtils;
import nl.me.easyclans.helpers.utils.MessageUtils;
import org.bukkit.entity.Player;

import java.util.List;

public class ListCommand implements IClanCommand {
    @Override
    public boolean execute(Player player, String[] args) {
        if (!player.hasPermission("easyclans.clan.list") && !player.isOp()) return MessageUtils.onNoPermission(player, "easyclans.clan.list");
        List<String> clans = ClanUtils.getAlLClanNames();
        if (clans == null || clans.size() == 0) return MessageUtils.onNoClansFound(player);
        return MessageUtils.onClanListMessage(player, clans);
    }
}
