package nl.me.easyclans.helpers.utils;

import nl.me.easyclans.EasyClans;
import nl.me.easyclans.helpers.EasyClansResponse;
import nl.me.easyclans.helpers.Status;
import nl.me.easyclans.helpers.dto.InviteDTO;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class InviteUtils {

    private static final List<InviteDTO> invites = new ArrayList<>();

    public static void addInvite(UUID clanUUID, UUID invitedByUUID, UUID invitedPlayerUUID) {
        invites.add(new InviteDTO(clanUUID, invitedByUUID, invitedPlayerUUID, System.currentTimeMillis()));
    }

    public static void removeInvite(UUID clanUUID, UUID invitedPlayerUUID) {
        invites.removeIf(inviteDTO -> inviteDTO.getClan() == clanUUID && inviteDTO.getInvitedPlayer().equals(invitedPlayerUUID));
    }

    public static InviteDTO getInvite(UUID clanUUID, UUID invitedPlayerUUID) {
        for (InviteDTO inviteDTO : invites) {
            if ((inviteDTO.getClan().toString()).equals(clanUUID.toString()) && (inviteDTO.getInvitedPlayer().toString()).equals(invitedPlayerUUID.toString())) {
                return inviteDTO;
            }
        }
        return null;
    }

    public static List<InviteDTO> getInvitesByClan(UUID clanUUID) {
        if (clanUUID == null) return null;
        List<InviteDTO> clanInvites = new ArrayList<>();
        for (InviteDTO invite : invites) {
            if ((invite.getClan().toString()).equals(clanUUID.toString())) {
                clanInvites.add(invite);
            }
        }
        return clanInvites;
    }

    public static List<InviteDTO> getInvitesByPlayer(UUID invitedPlayerUUID) {
        List<InviteDTO> playerInvites = new ArrayList<>();
        for (InviteDTO inviteDTO : invites) {
            if (inviteDTO.getInvitedPlayer().equals(invitedPlayerUUID)) {
                playerInvites.add(inviteDTO);
            }
        }
        return playerInvites;
    }

    public static EasyClansResponse removeExpiredInvites() {
        Iterator<InviteDTO> iterator = invites.iterator();
        while (iterator.hasNext()) {
            InviteDTO request = iterator.next();
            if (request.getStartTime() + (EasyClans.getPlugin().getConfig().getLong("clanInviteTimeout") * 1000) <= System.currentTimeMillis()) {
                Player sender = EasyClans.getPlugin().getServer().getPlayer(request.getInvitedBy());
                Player receiver = EasyClans.getPlugin().getServer().getPlayer(request.getInvitedPlayer());
                if (sender == null || receiver == null) {
                    iterator.remove();
                    continue;
                }
                MessageUtils.onClanInviteExpired(sender, receiver, ClanUtils.getClanNameFromUUID(request.getClan()));
                iterator.remove();
            }
        }
        return new EasyClansResponse(Status.SUCCESS, "EXPIRED_INVITES_REMOVED", null);
    }

}
