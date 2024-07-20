package nl.me.easyclans.helpers.utils;

import nl.me.easyclans.helpers.EasyClansResponse;
import nl.me.easyclans.helpers.Status;
import nl.me.easyclans.configs.ClanConfig;
import nl.me.easyclans.helpers.dto.ClanDTO;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public class ClanUtils {

    /**
     * Get a clan's UUID by name
     * @param name The id of the clan
     * @return The {@link UUID} of the clan or null if not found
     */
    public synchronized static UUID getClanUUIDFromName(String name) {
        if (name == null || name.isEmpty()) return null;
        ConfigurationSection clansSection = ClanConfig.get().getConfigurationSection("");
        if (clansSection != null) {
            for (String clanIdString : clansSection.getKeys(false)) {
                ConfigurationSection clanSection = clansSection.getConfigurationSection(clanIdString);
                if (clanSection != null && name.equalsIgnoreCase(clanSection.getString("name"))) {
                    return UUID.fromString(clanIdString);
                }
            }
        }
        return null;
    }

    /**
     * Get a clan's name by UUID
     * @param clanId The id of the clan
     * @return The {@link String} of the clan or null if not found
     */
    public synchronized static String getClanNameFromUUID(UUID clanId) {
        ConfigurationSection clanSection = ClanConfig.get().getConfigurationSection(clanId.toString());
        if (clanSection != null) {
            return clanSection.getString("name");
        }
        return null;
    }

    /**
     * Get a clan by name
     * @param name The name of the clan
     * @return The {@link ClanDTO} or null if not found
     */
    public synchronized static ClanDTO getClan(String name) {
        UUID clanId = getClanUUIDFromName(name);
        if (clanId == null) return null;
        return getClan(clanId);
    }

    /**
     * Get a clan by UUID
     * @param clanId The UUID of the clan
     * @return The {@link ClanDTO} or null if not found
     */
    public synchronized static ClanDTO getClan(UUID clanId) {
        ConfigurationSection clanSection = ClanConfig.get().getConfigurationSection(clanId.toString());
        if (clanSection == null) return null;

        Map<UUID, Boolean> members = new HashMap<>();
        List<UUID> allies = new ArrayList<>();
        Location home = null;
        ConfigurationSection membersSection = clanSection.getConfigurationSection("members");
        ConfigurationSection homeSection = clanSection.getConfigurationSection("home");

        if (membersSection != null) {
            for (String memberUUID : membersSection.getKeys(false)) {
                boolean isLeader = membersSection.getBoolean(memberUUID + ".leader");
                members.put(UUID.fromString(memberUUID), isLeader);
            }
        }
        for (String ally : clanSection.getStringList("allies")) {
            allies.add(UUID.fromString(ally));
        }
        UUID owner = UUID.fromString(clanSection.getString("owner"));

        if (homeSection != null) {
            home = new Location(
                    Bukkit.getWorld(homeSection.getString("world")),
                    homeSection.getDouble("x"),
                    homeSection.getDouble("y"),
                    homeSection.getDouble("z"),
                    (float) homeSection.getDouble("yaw"),
                    (float) homeSection.getDouble("pitch")
            );
        }

        ClanDTO clan = new ClanDTO(
                clanId,
                clanSection.getString("name"),
                clanSection.getString("prefix"),
                clanSection.getString("description"),
                owner, members, allies, home,
                clanSection.getBoolean("isHomePublic"),
                clanSection.getBoolean("isFriendlyFireEnabled")
        );
        return clan;
    }

    /**
     * Create a clan
     * @param clan The {@link ClanDTO} to save
     */
    public synchronized static EasyClansResponse createClan(ClanDTO clan) {

        if (clan.getClanId() == null) {
            return new EasyClansResponse(Status.ERROR, "CLAN_ID_NULL", null);
        }
        if (clan.getName() == null || clan.getName().isEmpty()) {
            return new EasyClansResponse(Status.ERROR, "CLAN_NAME_EMPTY", null);
        }

        ConfigurationSection clanSection = ClanConfig.get().getConfigurationSection(clan.getClanId().toString());
        if (clanSection != null) {
            return new EasyClansResponse(Status.ERROR, "CLAN_ALREADY_EXISTS", null);
        }
        clanSection = ClanConfig.get().createSection(clan.getClanId().toString());

        clanSection.set("name", clan.getName());
        clanSection.set("prefix", clan.getPrefix() == null ? clan.getName() : clan.getPrefix());

        ConfigurationSection membersSection = clanSection.getConfigurationSection("members");
        if (membersSection == null) {
            membersSection = clanSection.createSection("members");
        }

        for (UUID memberUUID : clan.getMembers().keySet()) {
            membersSection.set(memberUUID.toString() + ".leader", clan.getMembers().get(memberUUID));
        }

        clanSection.set("allies", clan.getAllies());
        clanSection.set("owner", clan.getOwner().toString());
        clanSection.set("isHomePublic", false);
        clanSection.set("isFriendlyFireEnabled", false);
        clanSection.set("description", "This clan has no description yet.");

        ClanConfig.save();
        return new EasyClansResponse(Status.SUCCESS, "CLAN_SAVED", clan);
    }

    /**
     * Delete a clan by UUID
     * @param clanId The UUID of the clan
     * @return The {@link EasyClansResponse}
     */
    public synchronized static EasyClansResponse deleteClan(UUID clanId) {
        ConfigurationSection clanSection = ClanConfig.get().getConfigurationSection(clanId.toString());
        if (clanSection == null) {
            return new EasyClansResponse(Status.ERROR, "CLAN_NOT_FOUND", null);
        }
        ClanConfig.get().set(clanId.toString(), null);
        ClanConfig.save();
        return new EasyClansResponse(Status.SUCCESS, "CLAN_DELETED", null);
    }

    /**
     * Overwrite the name of a clan
     * @param clanId The UUID of the clan
     * @param name The new name of the clan
     * @return The {@link EasyClansResponse}
     */
    public synchronized static EasyClansResponse setName(UUID clanId, String name) {
        if (clanId == null) return new EasyClansResponse(Status.ERROR, "CLAN_ID_NULL", null);
        if (name == null || name.isEmpty()) return new EasyClansResponse(Status.ERROR, "CLAN_NAME_EMPTY", null);

        ConfigurationSection clanSection = ClanConfig.get().getConfigurationSection(clanId.toString());
        if (clanSection == null) return new EasyClansResponse(Status.ERROR, "CLAN_NOT_FOUND", null);

        clanSection.set("name", name);
        ClanConfig.save();
        return new EasyClansResponse(Status.SUCCESS, "CLAN_NAME_SET", null);
    }

    /**
     * Overwrite the prefix of a clan
     * @param clanId The UUID of the clan
     * @param prefix The new prefix of the clan
     * @return The {@link EasyClansResponse}
     */
    public synchronized static EasyClansResponse setPrefix(UUID clanId, String prefix) {
        if (clanId == null) return new EasyClansResponse(Status.ERROR, "CLAN_ID_NULL", null);
        if (prefix == null || prefix.isEmpty()) return new EasyClansResponse(Status.ERROR, "CLAN_PREFIX_EMPTY", null);

        ConfigurationSection clanSection = ClanConfig.get().getConfigurationSection(clanId.toString());
        if (clanSection == null) return new EasyClansResponse(Status.ERROR, "CLAN_NOT_FOUND", null);

        clanSection.set("prefix", prefix);
        ClanConfig.save();
        return new EasyClansResponse(Status.SUCCESS, "CLAN_PREFIX_SET", null);
    }

    /**
     * Add a member to a clan
     * @param clanId The UUID of the clan
     * @param memberUUID The UUID of the member
     * @param isLeader Whether the member is a leader
     * @return The {@link EasyClansResponse}
     */
    public synchronized static EasyClansResponse addMember(UUID clanId, UUID memberUUID, boolean isLeader) {
        if (clanId == null) return new EasyClansResponse(Status.ERROR, "CLAN_ID_NULL", null);
        if (memberUUID == null) return new EasyClansResponse(Status.ERROR, "MEMBER_UUID_NULL", null);

        ConfigurationSection clanSection = ClanConfig.get().getConfigurationSection(clanId.toString());
        if (clanSection == null) return new EasyClansResponse(Status.ERROR, "CLAN_NOT_FOUND", null);

        ConfigurationSection membersSection = clanSection.getConfigurationSection("members");

        if (membersSection == null) membersSection = clanSection.createSection("members");
        if (membersSection.get(memberUUID.toString()) != null) return new EasyClansResponse(Status.ERROR, "MEMBER_ALREADY_EXISTS", null);

        membersSection.set(memberUUID.toString() + ".leader", isLeader);
        ClanConfig.save();
        return new EasyClansResponse(Status.SUCCESS, "MEMBER_ADDED", null);
    }

    /**
     * Remove a member from a clan
     * @param clanId The UUID of the clan
     * @param memberUUID The UUID of the member
     * @return The {@link EasyClansResponse}
     */
    public synchronized static EasyClansResponse removeMember(UUID clanId, UUID memberUUID) {
        if (clanId == null) return new EasyClansResponse(Status.ERROR, "CLAN_ID_NULL", null);
        if (memberUUID == null) return new EasyClansResponse(Status.ERROR, "MEMBER_UUID_NULL", null);
        ConfigurationSection clanSection = ClanConfig.get().getConfigurationSection(clanId.toString());
        if (clanSection == null) return new EasyClansResponse(Status.ERROR, "CLAN_NOT_FOUND", null);

        ConfigurationSection membersSection = clanSection.getConfigurationSection("members");

        if (membersSection == null) return new EasyClansResponse(Status.ERROR, "MEMBER_NOT_FOUND", null);
        if (membersSection.get(memberUUID.toString()) == null) return new EasyClansResponse(Status.ERROR, "MEMBER_NOT_FOUND", null);

        membersSection.set(memberUUID.toString(), null);
        membersSection.set(memberUUID + ".leader", null);
        membersSection.set(memberUUID.toString(), null);

        ClanConfig.save();
        return new EasyClansResponse(Status.SUCCESS, "MEMBER_REMOVED", null);
    }

    /**
     * Set a member as a leader
     * @param clanId The UUID of the clan
     * @param memberUUID The UUID of the member
     * @param isLeader Whether the member is a leader
     * @return The {@link EasyClansResponse}
     */
    public synchronized static EasyClansResponse setLeader(UUID clanId, UUID memberUUID, boolean isLeader) {
        if (clanId == null) return new EasyClansResponse(Status.ERROR, "CLAN_ID_NULL", null);
        if (memberUUID == null) return new EasyClansResponse(Status.ERROR, "MEMBER_UUID_NULL", null);
        ConfigurationSection clanSection = ClanConfig.get().getConfigurationSection(clanId.toString());
        if (clanSection == null) return new EasyClansResponse(Status.ERROR, "CLAN_NOT_FOUND", null);

        ConfigurationSection membersSection = clanSection.getConfigurationSection("members");

        if (membersSection == null) return new EasyClansResponse(Status.ERROR, "MEMBER_NOT_FOUND", null);
        if (membersSection.get(memberUUID.toString()) == null)
            return new EasyClansResponse(Status.ERROR, "MEMBER_NOT_FOUND", null);

        membersSection.set(memberUUID + ".leader", isLeader);
        ClanConfig.save();
        return new EasyClansResponse(Status.SUCCESS, "MEMBER_LEADER_SET", null);
    }

    /**
     * Set the home of a clan
     * @param clanId The UUID of the clan
     * @param location The location of the home
     * @return The {@link EasyClansResponse}
     */
    public synchronized static EasyClansResponse setHome(UUID clanId, Location location) {
        if (clanId == null) return new EasyClansResponse(Status.ERROR, "CLAN_ID_NULL", null);
        ConfigurationSection clanSection = ClanConfig.get().getConfigurationSection(clanId.toString());
        if (clanSection == null) return new EasyClansResponse(Status.ERROR, "CLAN_NOT_FOUND", null);
        ConfigurationSection homeSection = clanSection.getConfigurationSection("home");
        if (location != null) {
            if (homeSection == null) homeSection = clanSection.createSection("home");
            homeSection.set("world", location.getWorld().getName());
            homeSection.set("x", location.getX());
            homeSection.set("y", location.getY());
            homeSection.set("z", location.getZ());
            homeSection.set("yaw", location.getYaw());
            homeSection.set("pitch", location.getPitch());
        } else {
            clanSection.set("home", null);
        }
        ClanConfig.save();
        return new EasyClansResponse(Status.SUCCESS, "CLAN_HOME_SET", null);
    }

    /**
     * Add an ally to a clan
     * @param clanId The UUID of the clan
     * @param allyId The UUID of the ally
     * @return The {@link EasyClansResponse}
     */
    public synchronized static EasyClansResponse addAlly(UUID clanId, UUID allyId) {
        if (clanId == null) return new EasyClansResponse(Status.ERROR, "CLAN_ID_NULL", null);
        if (allyId == null) return new EasyClansResponse(Status.ERROR, "ALLY_ID_NULL", null);
        ConfigurationSection clanSection = ClanConfig.get().getConfigurationSection(clanId.toString());
        if (clanSection == null) return new EasyClansResponse(Status.ERROR, "CLAN_NOT_FOUND", null);
        List<String> alliesList = clanSection.getStringList("allies");
        if (alliesList == null) alliesList = new ArrayList<>();
        if (alliesList.contains(allyId.toString())) return new EasyClansResponse(Status.ERROR, "ALLY_ALREADY_EXISTS", null);
        alliesList.add(allyId.toString());
        clanSection.set("allies", alliesList);
        ClanConfig.save();
        return new EasyClansResponse(Status.SUCCESS, "ALLY_ADDED", null);
    }

    /**
     * Remove an ally from a clan
     * @param clanId The UUID of the clan
     * @param allyId The UUID of the ally
     * @return The {@link EasyClansResponse}
     */
    public synchronized static EasyClansResponse removeAlly(UUID clanId, UUID allyId) {
        if (clanId == null) return new EasyClansResponse(Status.ERROR, "CLAN_ID_NULL", null);
        if (allyId == null) return new EasyClansResponse(Status.ERROR, "ALLY_ID_NULL", null);
        ConfigurationSection clanSection = ClanConfig.get().getConfigurationSection(clanId.toString());
        if (clanSection == null) return new EasyClansResponse(Status.ERROR, "CLAN_NOT_FOUND", null);
        List<String> alliesList = clanSection.getStringList("allies");
        if (alliesList == null || alliesList.isEmpty()) return new EasyClansResponse(Status.ERROR, "NO_ALLIES_FOUND", null);
        if (!alliesList.contains(allyId.toString())) return new EasyClansResponse(Status.ERROR, "ALLY_NOT_FOUND", null);
        alliesList.remove(allyId.toString());
        clanSection.set("allies", alliesList);
        ClanConfig.save();
        return new EasyClansResponse(Status.SUCCESS, "ALLY_REMOVED", null);
    }

    /**
     * Set the owner of a clan
     * @param clanId The UUID of the clan
     * @param ownerUUID The UUID of the owner
     * @return The {@link EasyClansResponse}
     */
    public synchronized static EasyClansResponse setOwner(UUID clanId, UUID ownerUUID) {
        if (clanId == null) return new EasyClansResponse(Status.ERROR, "CLAN_ID_NULL", null);
        if (ownerUUID == null) return new EasyClansResponse(Status.ERROR, "OWNER_UUID_NULL", null);
        ConfigurationSection clanSection = ClanConfig.get().getConfigurationSection(clanId.toString());
        if (clanSection == null) return new EasyClansResponse(Status.ERROR, "CLAN_NOT_FOUND", null);
        clanSection.set("owner", ownerUUID.toString());
        ClanConfig.save();
        return new EasyClansResponse(Status.SUCCESS, "OWNER_SET", null);
    }

    /**
     * Set whether the home of a clan is public or not
     * @param clanId The UUID of the clan
     * @param isPublic Whether the home is public or not
     * @return The {@link EasyClansResponse}
     */
    public synchronized static EasyClansResponse setIsHomePublic(UUID clanId, boolean isPublic) {
        if (clanId == null) return new EasyClansResponse(Status.ERROR, "CLAN_ID_NULL", null);
        ConfigurationSection clanSection = ClanConfig.get().getConfigurationSection(clanId.toString());
        if (clanSection == null) return new EasyClansResponse(Status.ERROR, "CLAN_NOT_FOUND", null);
        clanSection.set("isHomePublic", isPublic);
        ClanConfig.save();
        return new EasyClansResponse(Status.SUCCESS, "IS_HOME_PUBLIC_SET", null);
    }

    /**
     * Set whether the friendly fire of a clan is enabled or not
     * @param clanId The UUID of the clan
     * @param isFriendlyFireEnabled Whether the friendly fire is enabled or not
     * @return The {@link EasyClansResponse}
     */
    public synchronized static EasyClansResponse setIsFriendlyFireEnabled(UUID clanId, boolean isFriendlyFireEnabled) {
        if (clanId == null) return new EasyClansResponse(Status.ERROR, "CLAN_ID_NULL", null);
        ConfigurationSection clanSection = ClanConfig.get().getConfigurationSection(clanId.toString());
        if (clanSection == null) return new EasyClansResponse(Status.ERROR, "CLAN_NOT_FOUND", null);
        clanSection.set("isFriendlyFireEnabled", isFriendlyFireEnabled);
        ClanConfig.save();
        return new EasyClansResponse(Status.SUCCESS, "IS_FRIENDLY_FIRE_SET", null);
    }

    /**
     * Set the description of a clan
     * @param clanId The UUID of the clan
     * @param description The description of the clan
     * @return The {@link EasyClansResponse}
     */
    public synchronized static EasyClansResponse setDescription(UUID clanId, String description) {
        if (clanId == null) return new EasyClansResponse(Status.ERROR, "CLAN_ID_NULL", null);
        if (description == null) return new EasyClansResponse(Status.ERROR, "DESCRIPTION_NULL", null);
        ConfigurationSection clanSection = ClanConfig.get().getConfigurationSection(clanId.toString());
        if (clanSection == null) return new EasyClansResponse(Status.ERROR, "CLAN_NOT_FOUND", null);
        clanSection.set("description", description);
        ClanConfig.save();
        return new EasyClansResponse(Status.SUCCESS, "DESCRIPTION_SET", null);
    }

    /**
     * Gets the online players in a clan
     * @param clan The clan
     * @return The online players in the clan
     */
    public static List<Player> getOnlinePlayersInClan(ClanDTO clan) {
        if (clan == null) return null;
        if (clan.getMembers() == null || clan.getMembers().isEmpty()) return null;
        List<Player> players = new ArrayList<>();
        for (Map.Entry<UUID, Boolean> entry : clan.getMembers().entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null && player.isOnline()) {
                players.add(player);
            }
        }
        return players;
    }

    /**
     * Gets a list of all the clan names
     * @return The list of all the clan names
     */
    public synchronized static List<String> getAlLClanNames() {
        List<String> clanNames = new ArrayList<>();
        for (String clanId : ClanConfig.get().getKeys(false)) {
            clanNames.add(ClanConfig.get().getString(clanId + ".name"));
        }
        return clanNames;
    }

    /**
     * Whether the player is a member of the specified clan
     * @param clanId The UUID of the clan
     * @param playerId The UUID of the player
     * @return Whether the player is a member of the specified clan
     */
    public synchronized static boolean isMember(UUID clanId, UUID playerId) {
        if (clanId == null) return false;
        if (playerId == null) return false;
        ClanDTO clan = getClan(clanId);
        if (clan == null) return false;
        Map<UUID, Boolean> members = clan.getMembers();
        if (members == null || members.isEmpty()) return false;
        return members.containsKey(playerId);
    }

    /**
     * Validates whether friendly fire is enabled in a clan
     * @param clanId The UUID of the clan
     * @return Whether friendly fire is enabled in the clan
     */
    public synchronized static boolean isFriendlyFireEnabledInClan(UUID clanId) {
        if (clanId == null) return true;
        ConfigurationSection clanSection = ClanConfig.get().getConfigurationSection(clanId.toString());
        if (clanSection == null) return true;
        return clanSection.getBoolean("isFriendlyFireEnabled");
    }

    /**
     * Validates whether two clans are mutual allies, meaning that they've got eachother in their allies list
     * @param clanId1 The UUID of the first clan
     * @param clanId2 The UUID of the second clan
     * @return Whether the two clans are mutual allies
     */
    public synchronized static boolean areMutualAllies(UUID clanId1, UUID clanId2) {
        if (clanId1 == null) return false;
        if (clanId2 == null) return false;
        ConfigurationSection clanSection1 = ClanConfig.get().getConfigurationSection(clanId1.toString());
        if (clanSection1 == null) return false;
        ConfigurationSection clanSection2 = ClanConfig.get().getConfigurationSection(clanId2.toString());
        if (clanSection2 == null) return false;
        List<String> allies1 = clanSection1.getStringList("allies");
        List<String> allies2 = clanSection2.getStringList("allies");
        if (allies1 == null || allies1.isEmpty()) return false;
        if (allies2 == null || allies2.isEmpty()) return false;
        return allies1.contains(clanId2.toString()) && allies2.contains(clanId1.toString());
    }

    /**
     * Gets the allies of a clan
     * @param clanId The UUID of the clan
     * @return The allies of the clan
     */
    public synchronized static List<String> getClanAllies(UUID clanId) {
        if (clanId == null) return null;
        ConfigurationSection clanSection = ClanConfig.get().getConfigurationSection(clanId.toString());
        if (clanSection == null) return null;
        return clanSection.getStringList("allies");
    }

}
