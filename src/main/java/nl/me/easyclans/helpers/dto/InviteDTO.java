package nl.me.easyclans.helpers.dto;

import java.util.UUID;

public class InviteDTO {

    private UUID clan;
    private UUID invitedBy;
    private UUID invitedPlayer;
    private long startTime;
    private boolean isExpired;

    public InviteDTO(UUID clan, UUID invitedBy, UUID invitedPlayer, long startTime) {
        this.clan = clan;
        this.invitedBy = invitedBy;
        this.invitedPlayer = invitedPlayer;
        this.startTime = startTime;
        this.isExpired = false;
    }

    public UUID getClan() {
        return clan;
    }

    public void setClan(UUID clan) {
        this.clan = clan;
    }

    public UUID getInvitedBy() {
        return invitedBy;
    }

    public void setInvitedBy(UUID invitedBy) {
        this.invitedBy = invitedBy;
    }

    public UUID getInvitedPlayer() {
        return invitedPlayer;
    }

    public void setInvitedPlayer(UUID invitedPlayer) {
        this.invitedPlayer = invitedPlayer;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public void setExpired(boolean expired) {
        isExpired = expired;
    }
}
