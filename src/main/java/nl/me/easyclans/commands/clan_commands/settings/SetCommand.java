package nl.me.easyclans.commands.clan_commands.settings;

import nl.me.easyclans.EasyClans;
import nl.me.easyclans.commands.IClanCommand;
import nl.me.easyclans.helpers.dto.ClanDTO;
import nl.me.easyclans.helpers.utils.ClanUtils;
import nl.me.easyclans.helpers.utils.ColorUtils;
import nl.me.easyclans.helpers.utils.MessageUtils;
import nl.me.easyclans.helpers.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class SetCommand implements IClanCommand {
    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length < 2) return MessageUtils.onWrongUsage(player, "/clan set <setting> <value>");
        String setting = args[1];
        ClanDTO clan = PlayerUtils.getClan(player);
        if (clan == null) return MessageUtils.onMustBeMember(player);
        if (args.length == 2 && !setting.equalsIgnoreCase("home")) {
            switch (setting) {
                case "name":
                    if (!player.hasPermission("easyclans.clan.set.name") && !player.isOp())
                        return MessageUtils.onNoPermission(player, "easyclans.clan.set.name");
                    String name = clan.getName();
                    return MessageUtils.onGetSettingValue(player, setting, name);
                case "prefix":
                    if (!player.hasPermission("easyclans.clan.set.prefix") && !player.isOp())
                        return MessageUtils.onNoPermission(player, "easyclans.clan.set.prefix");
                    String prefix = clan.getPrefix();
                    return MessageUtils.onGetSettingValue(player, setting, prefix);
                case "description":
                    if (!player.hasPermission("easyclans.clan.set.description") && !player.isOp())
                        return MessageUtils.onNoPermission(player, "easyclans.clan.set.description");
                    String description = clan.getDescription();
                    return MessageUtils.onGetSettingValue(player, setting, description);
                case "friendlyFireEnabled":
                    if (!player.hasPermission("easyclans.clan.set.friendlyFireEnabled") && !player.isOp())
                        return MessageUtils.onNoPermission(player, "easyclans.clan.set.friendlyFireEnabled");
                    String friendlyFireEnabled = clan.isFriendlyFireEnabled() ? "true" : "false";
                    return MessageUtils.onGetSettingValue(player, setting, friendlyFireEnabled);
                case "home":
                    if (!player.hasPermission("easyclans.clan.set.home") && !player.isOp())
                        return MessageUtils.onNoPermission(player, "easyclans.clan.set.home");
                    Location location = clan.getHome();
                    if (location == null) return MessageUtils.onGetSettingValue(player, setting, "not set");
                    String locationString = location.getWorld().getName() + ", " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ();
                    String homePublic = clan.isHomePublic() ? "true" : "false";
                    MessageUtils.onGetSettingValue(player, setting, homePublic);
                    return MessageUtils.onGetSettingValue(player, setting, locationString);
                case "owner":
                    if (!player.hasPermission("easyclans.clan.set.owner") && !player.isOp())
                        return MessageUtils.onNoPermission(player, "easyclans.clan.set.owner");
                    String owner = Bukkit.getOfflinePlayer(clan.getOwner()).getName();
                    return MessageUtils.onGetSettingValue(player, setting, owner);
                default:
                    return MessageUtils.onSettingNotFound(player, setting);
            }
        }
        if (!PlayerUtils.isPlayerLeader(player, clan)) return MessageUtils.onMustBeLeader(player);
        String value = "";
        if (!setting.equalsIgnoreCase("home")) {
            value = String.join(" ", args).substring(args[0].length() + args[1].length() + 2);
        } else {
            if (args.length == 3) {
                value = args[2];
            } else {
                value = null;
            }
        }
        ConfigurationSection config = EasyClans.getPlugin().getConfig();
        switch (setting) {

            // The name (identifiers in commands) of the clan
            case "name":
                if (!player.hasPermission("easyclans.clan.set.name") && !player.isOp())
                    return MessageUtils.onNoPermission(player, "easyclans.clan.set.name");
                String oldName = clan.getName();
                int minNameLength = config.getInt("minClanNameLength");
                int maxNameLength = config.getInt("maxClanNameLength");
                if (value.length() < minNameLength || value.length() > maxNameLength)
                    return MessageUtils.onInvalidName(player, minNameLength, maxNameLength);
                if (!value.matches("^[a-zA-Z0-9_-]+$"))
                    return MessageUtils.onInvalidName(player, minNameLength, maxNameLength);
                if (!ColorUtils.isNameOrPrefixAllowed(value))
                    return MessageUtils.onInvalidCodes(player);
                List<String> clanNames = ClanUtils.getAlLClanNames();
                if (clanNames.contains(args[1]))
                    return MessageUtils.onClanAlreadyExists(player, value);
                ClanUtils.setName(clan.getClanId(), value);
                return MessageUtils.onClanRenamed(player, oldName, value);

            // The prefix of the clan, for optional decoration
            case "prefix":
                if (!player.hasPermission("easyclans.clan.set.prefix") && !player.isOp())
                    return MessageUtils.onNoPermission(player, "easyclans.clan.set.prefix");
                String oldPrefix = clan.getPrefix();
                int minPrefixLength = config.getInt("minClanPrefixLength");
                int maxPrefixLength = config.getInt("maxClanPrefixLength");
                String formattedPrefix = ChatColor.stripColor(ColorUtils.formatPrefix(value));
                if (formattedPrefix.length() < minPrefixLength || formattedPrefix.length() > maxPrefixLength)
                    return MessageUtils.onInvalidPrefix(player, minPrefixLength, maxPrefixLength);
                if (!ColorUtils.isNameOrPrefixAllowed(value))
                    return MessageUtils.onInvalidCodes(player);
                ClanUtils.setPrefix(clan.getClanId(), value);
                return MessageUtils.onClanPrefixChanged(player, clan.getName(), oldPrefix, value);

            // The description of the clan
            case "description":
                if (!player.hasPermission("easyclans.clan.set.description") && !player.isOp())
                    return MessageUtils.onNoPermission(player, "easyclans.clan.set.description");
                ClanUtils.setDescription(clan.getClanId(), value);
                return MessageUtils.onClanDescriptionChanged(player, clan.getName(), value);

            // Whether friendly fire is enabled or not; meaning whether clan members can hurt each other
            case "friendlyFireEnabled":
                if (!player.hasPermission("easyclans.clan.set.friendlyFireEnabled") && !player.isOp())
                    return MessageUtils.onNoPermission(player, "easyclans.clan.set.friendlyFireEnabled");
                boolean friendlyFireEnabled = Boolean.parseBoolean(value);
                ClanUtils.setIsFriendlyFireEnabled(clan.getClanId(), friendlyFireEnabled);
                if (friendlyFireEnabled) return MessageUtils.onClanFriendlyFireOn(player, clan.getName());
                return MessageUtils.onClanFriendlyFireOff(player, clan.getName());

            // The clan home location or whether it is public or not
            case "home":
                if (!player.hasPermission("easyclans.clan.set.home") && !player.isOp())
                    return MessageUtils.onNoPermission(player, "easyclans.clan.set.home");
                int minMembersForHome = config.getInt("minMembersForHome");
                if (value == null || value.equalsIgnoreCase("here")) {
                    if (clan.getMembers().size() < minMembersForHome)
                        return MessageUtils.onClanTooFewMembersForHome(player, clan.getName(), minMembersForHome);
                    Location location = player.getLocation();
                    ClanUtils.setHome(clan.getClanId(), location);
                    return MessageUtils.onClanSetHome(player, clan.getName(), location);
                } else if (value.equalsIgnoreCase("none") || value.equalsIgnoreCase("null") || value.equalsIgnoreCase("remove")) {
                    ClanUtils.setHome(clan.getClanId(), null);
                    return MessageUtils.onClanRemoveHome(player, clan.getName());
                } else if (value.equalsIgnoreCase("public") || value.equalsIgnoreCase("private")) {
                    if (!player.hasPermission("easyclans.clan.set.public-home") && !player.isOp())
                        return MessageUtils.onNoPermission(player, "easyclans.clan.set.public-home");
                    boolean homePublic = value.equalsIgnoreCase("public") ? true : false;
                    ClanUtils.setIsHomePublic(clan.getClanId(), homePublic);
                    if (homePublic) return MessageUtils.onClanHomePublic(player, clan.getName());
                    return MessageUtils.onClanHomePrivate(player, clan.getName());
                } else {
                    return MessageUtils.onWrongUsage(player, "/clan set home [none]");
                }

            // The owner of the clan (they can set leaders and disband the clan)
            case "owner":
                if (!player.hasPermission("easyclans.clan.set.owner") && !player.isOp())
                    return MessageUtils.onNoPermission(player, "easyclans.clan.set.owner");
                if (!clan.getOwner().toString().equals(player.getUniqueId().toString())) return MessageUtils.onMustBeOwner(player);
                UUID newOwnerUUID = PlayerUtils.getPlayerUUIDFromName(value);
                if (newOwnerUUID == null) return MessageUtils.onPlayerNotFound(player, value);
                OfflinePlayer newOwner = Bukkit.getOfflinePlayer(newOwnerUUID);
                if (newOwner == null) return MessageUtils.onPlayerNotFound(player, value);
                if (!ClanUtils.isMember(clan.getClanId(), newOwnerUUID)) return MessageUtils.onPlayerNotInClan(player, newOwner.getName());
                ClanUtils.setOwner(clan.getClanId(), newOwner.getUniqueId());
                return MessageUtils.onClanOwnerChanged(player, clan.getName(), player.getName(), value);

            // Oopsie :(
            default:
                return MessageUtils.onSettingNotFound(player, setting);
        }
    }
}
