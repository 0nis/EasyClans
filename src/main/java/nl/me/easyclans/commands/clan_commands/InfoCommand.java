package nl.me.easyclans.commands.clan_commands;

import nl.me.easyclans.commands.IClanCommand;
import nl.me.easyclans.helpers.dto.ClanDTO;
import nl.me.easyclans.helpers.utils.ClanUtils;
import nl.me.easyclans.helpers.utils.MessageUtils;
import nl.me.easyclans.helpers.utils.PlayerUtils;
import org.bukkit.entity.Player;

public class InfoCommand implements IClanCommand {
    @Override
    public boolean execute(Player player, String[] args) {
        if (!player.hasPermission("easyclans.clan.info") && !player.isOp()) return MessageUtils.onNoPermission(player, "easyclans.clan.info");
        if (args.length == 1) {
            ClanDTO clan = PlayerUtils.getClan(player);
            if (clan == null) return MessageUtils.onMustBeMember(player);
            player.sendMessage(" ");
            MessageUtils.onInfoCommandMessage(player, clan);
            player.sendMessage(" ");
            return true;
        } else if (args.length == 2) {
            String clanName = args[1];
            if (clanName == null || clanName.isEmpty()) return MessageUtils.onWrongUsage(player, "/clan info <clan>");
            ClanDTO clan = ClanUtils.getClan(clanName);
            if (clan != null && clan.getName().equalsIgnoreCase(clanName)) {
                player.sendMessage(" ");
                MessageUtils.onInfoCommandMessage(player, clan);
                player.sendMessage(" ");
                return true;
            }
        } else {
            return MessageUtils.onWrongUsage(player, "/clan info [clan]");
        }
        return true;
    }
}
