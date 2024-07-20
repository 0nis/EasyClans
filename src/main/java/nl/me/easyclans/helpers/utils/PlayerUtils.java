package nl.me.easyclans.helpers.utils;

import nl.me.easyclans.helpers.EasyClansResponse;
import nl.me.easyclans.helpers.Status;
import nl.me.easyclans.configs.PlayerConfig;
import nl.me.easyclans.helpers.dto.ClanDTO;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PlayerUtils {

    /**
     * Get a player's clan
     * @param player The player to get the clan from
     * @return A {@link ClanDTO} of the clan
     */
    public synchronized static ClanDTO getClan(OfflinePlayer player) {
        if (player == null) return null;

        ConfigurationSection playerSection = PlayerConfig.get().getConfigurationSection(player.getUniqueId().toString());
        if (playerSection == null) return null;

        if (playerSection.contains("clan")) {
            ClanDTO clan = ClanUtils.getClan(UUID.fromString(playerSection.getString("clan")));
            if (clan != null) {
                return clan;
            }
        }
        return null;
    }

    /**
     * Sets the clan of a player
     * @param player The player to set the clan of
     * @param clanId The id of the clan to set
     */
    public synchronized static EasyClansResponse setClan(Player player, UUID clanId) {
        if (player == null) return new EasyClansResponse(Status.ERROR, "PLAYER_NULL", null);
        if (clanId == null) return new EasyClansResponse(Status.ERROR, "CLAN_NULL", null);

        ConfigurationSection playerSection = PlayerConfig.get().getConfigurationSection(player.getUniqueId().toString());
        if (playerSection == null) {
            playerSection = PlayerConfig.get().createSection(player.getUniqueId().toString());
        }

        if (playerSection.contains("clan")) {
            return new EasyClansResponse(Status.ERROR, "PLAYER_ALREADY_IN_CLAN", null);
        }
        if (!playerSection.contains("name")) {
            playerSection.set("name", player.getName());
        }

        playerSection.set("clan", clanId.toString());

        PlayerConfig.save();
        return new EasyClansResponse(Status.SUCCESS, "CLAN_SET", clanId);
    }

    /**
     * Removes a clan from a player
     * @param playerId The player to remove the clan from
     * @return A {@link EasyClansResponse} with the status and message
     */
    public synchronized static EasyClansResponse removeClan(UUID playerId) {
        if (playerId == null) return new EasyClansResponse(Status.ERROR, "PLAYER_NULL", null);

        ConfigurationSection playerSection = PlayerConfig.get().getConfigurationSection(playerId.toString());
        if (!playerSection.contains("clan")) {
            return new EasyClansResponse(Status.ERROR, "PLAYER_NOT_IN_CLAN", null);
        }

        playerSection.set("clan", null);

        PlayerConfig.save();
        return new EasyClansResponse(Status.SUCCESS, "CLAN_REMOVED", null);
    }

    /**
     * Validates if a player is the leader or owner of a clan
     * @param player The player to validate
     * @param clan The clan to validate
     * @return True if the player is the leader or owner of the clan
     */
    public static boolean isPlayerLeader(OfflinePlayer player, ClanDTO clan) {
        if (player == null) return false;
        if (clan == null) return false;

        if (clan.getOwner().equals(player.getUniqueId())) return true;
        return clan.getMembers().entrySet().stream()
                .anyMatch(entry -> entry.getKey().equals(player.getUniqueId()) && entry.getValue());
    }

    /**
     * Kicks all players out of a clan (used when a clan is deleted)
     * @param clanId The id of the clan to kick all players out of
     * @return A {@link EasyClansResponse} with the status of the operation
     */
    public synchronized static EasyClansResponse kickAllPlayersOutOfClan(UUID clanId) {
        if (clanId == null) return new EasyClansResponse(Status.ERROR, "CLAN_NULL", null);
        List<Player> players = new ArrayList<>();
        for (String playerUUID : PlayerConfig.get().getKeys(false)) {
            if (playerUUID == null) continue;
            ConfigurationSection playerSection = PlayerConfig.get().getConfigurationSection(playerUUID);
            if (playerSection == null) continue;
            if (!playerSection.contains("clan")) continue;
            if (playerSection.getString("clan") == null) continue;
            if (playerSection.getString("clan").equals(clanId.toString())) {
                playerSection.set("clan", null);
            }
        }
        PlayerConfig.save();
        return new EasyClansResponse(Status.SUCCESS, "PLAYERS_KICKED", null);
    }

    /**
     * Gets a player's UUID from their name
     * @param name The name of the player to get the UUID from
     * @return The UUID of the player
     */
    public synchronized static UUID getPlayerUUIDFromName(String name) {
        if (name == null) return null;
        for (String playerUUID : PlayerConfig.get().getKeys(false)) {
            if (playerUUID == null) continue;
            ConfigurationSection playerSection = PlayerConfig.get().getConfigurationSection(playerUUID);
            if (playerSection == null) continue;
            if (!playerSection.contains("name")) continue;
            if (playerSection.getString("name") == null) continue;
            if (playerSection.getString("name").equalsIgnoreCase(name)) {
                return UUID.fromString(playerUUID);
            }
        }
        return null;
    }

    /**
     * Validates whether a player is in a clan (any clan) or not
     * @param player The player to validate
     * @return True if the player is in a clan
     */
    public synchronized static boolean hasClan(OfflinePlayer player) {
        if (player == null) return false;
        ConfigurationSection playerSection = PlayerConfig.get().getConfigurationSection(player.getUniqueId().toString());
        if (playerSection == null) return false;
        return playerSection.contains("clan");
    }

    /**
     * Validates whether friendly fire is enabled or not between the damager and damagee
     * @param damager The player who is damaging
     * @param damagee The player who is being damaged
     * @return True if friendly fire is enabled
     */
    public synchronized static boolean isFriendlyFireEnabledBetweenPlayers(Player damager, Player damagee) {
        ConfigurationSection damagerSection = PlayerConfig.get().getConfigurationSection(damager.getUniqueId().toString());
        ConfigurationSection damageeSection = PlayerConfig.get().getConfigurationSection(damagee.getUniqueId().toString());

        // If either player is not in a clan, return true
        if (damagerSection == null || damageeSection == null) return true;
        if (!damagerSection.contains("clan") || !damageeSection.contains("clan")) return true;
        if (damagerSection.getString("clan") == null || damageeSection.getString("clan") == null) return true;

        if (Objects.equals(damagerSection.getString("clan"), damageeSection.getString("clan"))) {
            if (!ClanUtils.isFriendlyFireEnabledInClan(UUID.fromString(damagerSection.getString("clan")))) {
                return false;
            }
        } else {
            if (ClanUtils.areMutualAllies(UUID.fromString(damagerSection.getString("clan")), UUID.fromString(damageeSection.getString("clan")))) {
                if (!ClanUtils.isFriendlyFireEnabledInClan(UUID.fromString(damagerSection.getString("clan")))) {
                    if (!ClanUtils.isFriendlyFireEnabledInClan(UUID.fromString(damageeSection.getString("clan")))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
