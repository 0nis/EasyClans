package nl.me.easyclans.helpers.dto;

import org.bukkit.Location;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ClanDTO {

    private UUID clanId;
    private String name;
    private String prefix;
    private String description;
    private UUID owner;
    private Map<UUID, Boolean> members;
    private List<UUID> allies;
    private Location home;
    private boolean isHomePublic;
    private boolean isFriendlyFireEnabled;

    public ClanDTO() {
    }

    public ClanDTO(UUID clanId, String name, String prefix, String description, UUID owner, Map<UUID, Boolean> members, List<UUID> allies, Location home, boolean isHomePublic, boolean isFriendlyFireEnabled) {
        this.clanId = clanId;
        this.name = name;
        this.prefix = prefix;
        this.description = description;
        this.owner = owner;
        this.members = members;
        this.allies = allies;
        this.home = home;
        this.isHomePublic = isHomePublic;
        this.isFriendlyFireEnabled = isFriendlyFireEnabled;
    }

    public UUID getClanId() {
        return clanId;
    }

    public void setClanId(UUID clanId) {
        this.clanId = clanId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public Map<UUID, Boolean> getMembers() {
        return members;
    }

    public void setMembers(Map<UUID, Boolean> members) {
        this.members = members;
    }

    public List<UUID> getAllies() {
        return allies;
    }

    public void setAllies(List<UUID> allies) {
        this.allies = allies;
    }

    public Location getHome() {
        return home;
    }

    public void setHome(Location home) {
        this.home = home;
    }

    public boolean isHomePublic() {
        return isHomePublic;
    }

    public void setHomePublic(boolean homePublic) {
        isHomePublic = homePublic;
    }

    public boolean isFriendlyFireEnabled() {
        return isFriendlyFireEnabled;
    }

    public void setFriendlyFireEnabled(boolean friendlyFireEnabled) {
        isFriendlyFireEnabled = friendlyFireEnabled;
    }
}
