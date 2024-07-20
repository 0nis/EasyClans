package nl.me.easyclans.commands.clan_commands.invites;

import nl.me.easyclans.commands.IClanCommand;
import nl.me.easyclans.helpers.dto.InviteDTO;
import nl.me.easyclans.helpers.utils.ClanUtils;
import nl.me.easyclans.helpers.utils.InviteUtils;
import nl.me.easyclans.helpers.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DeclineCommand implements IClanCommand {
    @Override
    public boolean execute(Player player, String[] args) {
        if (!player.hasPermission("easyclans.clan.decline") && !player.isOp()) return MessageUtils.onNoPermission(player, "easyclans.clan.decline");
        if (args.length < 2) return MessageUtils.onWrongUsage(player, "/clan decline <clan>");
        String clanName = args[1];
        if (clanName == null || clanName.isEmpty()) return MessageUtils.onWrongUsage(player, "/clan decline <clan>");
        InviteDTO invite = InviteUtils.getInvite(ClanUtils.getClanUUIDFromName(clanName), player.getUniqueId());
        if (invite == null) return MessageUtils.onClanInviteNotFound(player, clanName);
        InviteUtils.removeInvite(invite.getClan(), invite.getInvitedPlayer());
        Player sender = Bukkit.getPlayer(invite.getInvitedBy());
        MessageUtils.onClanInviteDeclined(player , clanName);
        if (sender != null && sender.isOnline()) MessageUtils.onClanInviteDeclinedReceived(sender, player, clanName);
        return true;
    }
}
