package nl.me.easyclans.commands.clan_commands.invites;

import nl.me.easyclans.commands.IClanCommand;
import nl.me.easyclans.helpers.dto.ClanDTO;
import nl.me.easyclans.helpers.dto.InviteDTO;
import nl.me.easyclans.helpers.utils.ClanUtils;
import nl.me.easyclans.helpers.utils.InviteUtils;
import nl.me.easyclans.helpers.utils.MessageUtils;
import nl.me.easyclans.helpers.utils.PlayerUtils;
import org.bukkit.entity.Player;

public class AcceptCommand implements IClanCommand {
    @Override
    public boolean execute(Player player, String[] args) {
        if (!player.hasPermission("easyclans.clan.accept") && !player.isOp()) return MessageUtils.onNoPermission(player, "easyclans.clan.accept");
        if (args.length < 2) return MessageUtils.onWrongUsage(player, "/clan accept <clan>");
        String clanName = args[1];
        if (clanName == null || clanName.isEmpty()) return MessageUtils.onWrongUsage(player, "/clan accept <clan>");
        InviteDTO invite = InviteUtils.getInvite(ClanUtils.getClanUUIDFromName(clanName), player.getUniqueId());
        if (invite == null) return MessageUtils.onClanInviteNotFound(player, clanName);
        ClanDTO clan = PlayerUtils.getClan(player);
        if (clan != null) {
            InviteUtils.removeInvite(invite.getClan(), invite.getInvitedPlayer());
            return MessageUtils.onAlreadyInClan(player, clan.getName());
        }
        InviteUtils.removeInvite(invite.getClan(), invite.getInvitedPlayer());
        ClanUtils.addMember(invite.getClan(), invite.getInvitedPlayer(), false);
        PlayerUtils.setClan(player, invite.getClan());
        return MessageUtils.onClanMemberAdded(player, invite.getClan());
    }
}
