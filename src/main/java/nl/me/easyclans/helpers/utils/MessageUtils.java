package nl.me.easyclans.helpers.utils;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import nl.me.easyclans.EasyClans;
import nl.me.easyclans.configs.MessagesConfig;
import nl.me.easyclans.helpers.dto.ClanDTO;
import nl.me.easyclans.integrations.discordsrv.DiscordSRVUtils;
import nl.me.easyclans.integrations.placeholderapi.PlaceholderAPIUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MessageUtils {

    public static String formatMessage(CommandSender commandSender, String message) {
        message = PlaceholderAPIUtils.setPlaceholders(commandSender, message);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Send the entire clan a message
     * @param clan The clan to send the message to
     * @param message The message to send
     * @return Whether the command was successful
     */
    public static boolean sendClanMessage(CommandSender commandSender, ClanDTO clan, String message) {
        message = message.replace("{clan-prefix}", ColorUtils.formatPrefix(clan.getPrefix()));
        List<Player> onlineClanMembers = ClanUtils.getOnlinePlayersInClan(clan);
        for (Player onlineClanMember : onlineClanMembers) {
            onlineClanMember.sendMessage(formatMessage(onlineClanMember, message));
        }
        return true;
    }

    /**
     * When a player sends a private message to their clan
     * @param player The player that sent the message
     * @param message The message that was sent
     * @return Whether the command was successful
     */
    public static boolean onClanChatMessage(Player player, String message) {
        if (!player.hasPermission("easyclans.clan.chat") && !player.isOp()) return MessageUtils.onNoPermission(player, "easyclans.clan.chat");
        if (message == null || message.isEmpty()) return MessageUtils.onWrongUsage(player, "/clan chat <message>");
        ClanDTO clan = PlayerUtils.getClan(player);
        if (clan == null) return MessageUtils.onMustBeMember(player);
        List<Player> onlineClanMembers = ClanUtils.getOnlinePlayersInClan(clan);
        for (Player onlineClanMember : onlineClanMembers) {
            onlineClanMember.sendMessage(formatMessage(onlineClanMember,
                    EasyClans.getPlugin().getConfig().getString("onClanChatMessage")
                            .replace("{clan}", clan.getName())
                            .replace("{player}", player.getName())
                            .replace("{display}", player.getDisplayName())
                            .replace("{message}", message)));
        }
        if (DiscordSRVUtils.isDiscordSRVEnabled()) {
            DiscordSRVUtils.sendMessageToAdminChannel(player, message, clan.getName());
            DiscordSRVUtils.sendMessageToClanChannel(player, message, clan.getName(), clan.getClanId());
        }
        return true;
    }

    /**
     * The info message for the clan info command
     * @param commandSender The command sender to send the message to
     * @param clanDTO The clan to get the info from
     */
    public static void onInfoCommandMessage(CommandSender commandSender, ClanDTO clanDTO) {
        Player player = (Player) commandSender;
        List<String> messages = MessagesConfig.get().getStringList("clanInfo");
        String ownerName = Bukkit.getOfflinePlayer(clanDTO.getOwner()).getName();
        if (ownerName == null) ownerName = clanDTO.getOwner().toString();
        StringBuilder memberList = new StringBuilder();
        if (clanDTO.getMembers() == null ||  clanDTO.getMembers().size() == 0) memberList.append("none");
        else if (clanDTO.getMembers().size() == 1) {
            UUID member = clanDTO.getMembers().keySet().iterator().next();
            String memberName = Bukkit.getOfflinePlayer(member).getName();
            if (memberName == null) memberName = member.toString();
            memberList.append(memberName);
        }
        else if (clanDTO.getMembers().size() > 1) {
            for (Map.Entry<UUID, Boolean> member : clanDTO.getMembers().entrySet()) {
                String memberName = Bukkit.getOfflinePlayer(member.getKey()).getName();
                if (memberName == null) memberName = "unknown";
                memberList.append(memberName).append(", ");
            }
            memberList = new StringBuilder(memberList.substring(0, memberList.length() - 2));
        }
        String home = "not set";
        if (clanDTO.getHome() != null) {
            home = clanDTO.getHome().getWorld().getName() + ", " + clanDTO.getHome().getBlockX() + ", " + clanDTO.getHome().getBlockY() + ", " + clanDTO.getHome().getBlockZ();
        }
        if (!clanDTO.isHomePublic() && !ClanUtils.isMember(clanDTO.getClanId(), player.getUniqueId())) home = "private";
        StringBuilder allies = new StringBuilder("none");
        if (clanDTO.getAllies() != null && clanDTO.getAllies().size() > 0) {
            allies = new StringBuilder();
            for (UUID ally : clanDTO.getAllies()) {
                String allyClanName = ClanUtils.getClanNameFromUUID(ally);
                if (allyClanName == null) allyClanName = ally.toString();
                allies.append(allyClanName).append(", ");
            }
        }
        for (String message : messages) {
            message = message
                    .replace("{clan}", clanDTO.getName())
                    .replace("{owner}", ownerName)
                    .replace("{prefix}", ColorUtils.formatPrefix(clanDTO.getPrefix()))
                    .replace("{description}", clanDTO.getDescription())
                    .replace("{memberCount}", String.valueOf(clanDTO.getMembers().size()))
                    .replace("{members}", memberList.toString())
                    .replace("{home}", home)
                    .replace("{allies}", allies.toString())
                    .replace("{publicHome}", clanDTO.isHomePublic() ? "yes" : "no")
                    .replace("{friendlyFire}", clanDTO.isFriendlyFireEnabled() ? "yes" : "no");
            commandSender.sendMessage(formatMessage(commandSender, message));
        }
    }

    public static boolean onClanListMessage(Player sender, List<String> clans) {
        if (clans == null || clans.size() == 0) return MessageUtils.onNoClansFound(sender);
        StringBuilder clanList = new StringBuilder();
        for (String clan : clans) {
            clanList.append(clan).append(", ");
        }
        clanList = new StringBuilder(clanList.substring(0, clanList.length() - 2));
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onClanList")
                        .replace("{clans}", clanList.toString())));
        return true;
    }

    public static boolean onHelpMessage(CommandSender sender) {
        List<String> messages = MessagesConfig.get().getStringList("help");
        for (String message : messages) {
            sender.sendMessage(
                    formatMessage(sender,
                            message
                                    .replace("{version}", EasyClans.getPlugin().getDescription().getVersion())
                                    .replace("{prefix}", EasyClans.getPlugin().getDescription().getAuthors().get(0))
                                    .replace("{name}", EasyClans.getPlugin().getDescription().getName())
                                    .replace("{description}", EasyClans.getPlugin().getDescription().getDescription())
                    )
            );
        }
        return true;
    }

    public static boolean onGetSettingValue(CommandSender sender, String setting, String value) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onGetSettingValue")
                        .replace("{setting}", setting)
                        .replace("{value}", value)));
        return true;
    }

    public static boolean onSetSettingValue(CommandSender sender, String setting, String value) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onSetSettingValue")
                        .replace("{setting}", setting)
                        .replace("{value}", value)));
        return true;
    }

    public static boolean onSettingNotFound(CommandSender sender, String setting) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onSettingNotFound")
                        .replace("{setting}", setting)));
        return true;
    }

    public static boolean onWrongUsage(CommandSender sender, String usage) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onWrongUsage")
                        .replace("{usage}", usage)));
        return true;
    }

    public static boolean onNoPermission(CommandSender sender, String permission) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onNoPermission")
                        .replace("{permission}", permission)));
        return true;
    }

    public static boolean onPlayerNotFound(CommandSender sender, String player) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onPlayerNotFound")
                        .replace("{player}", player)));
        return true;
    }

    public static boolean onUnknownError(CommandSender sender) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onUnknownError")));
        return true;
    }

    public static boolean onInvalidName(CommandSender sender, int minLength, int maxLength) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onInvalidName")
                        .replace("{minLength}", String.valueOf(minLength))
                        .replace("{maxLength}", String.valueOf(maxLength))));
        return true;
    }

    public static boolean onInvalidPrefix(CommandSender sender, int minLength, int maxLength) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onInvalidPrefix")
                        .replace("{minLength}", String.valueOf(minLength))
                        .replace("{maxLength}", String.valueOf(maxLength))));
        return true;
    }

    public static boolean onConfigReloaded(CommandSender sender) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onConfigReloaded")));
        return true;
    }

    public static boolean onMustBePlayer(CommandSender sender) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onMustBePlayer")));
        return true;
    }

    public static boolean onClanCreated(CommandSender sender, String clanName) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onClanCreated")
                        .replace("{clan}", clanName)));
        return true;
    }

    public static boolean onClanDeleted(CommandSender sender, String clanName) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onClanDeleted")
                        .replace("{clan}", clanName)));
        return true;
    }

    public static boolean onClanRenamed(Player sender, String oldClanName, String newClanName) {
        ClanDTO clanDTO = ClanUtils.getClan(oldClanName);
        if (clanDTO == null) clanDTO = ClanUtils.getClan(newClanName);
        sendClanMessage(sender, clanDTO,
                MessagesConfig.get().getString("onClanRenamed")
                        .replace("{player}", sender.getName())
                        .replace("{display}", sender.getDisplayName())
                        .replace("{oldName}", oldClanName)
                        .replace("{newName}", newClanName));
        return true;
    }

    public static boolean onClanPrefixChanged(Player sender, String clanName, String oldPrefix, String newPrefix) {
        sendClanMessage(sender, ClanUtils.getClan(clanName),
                MessagesConfig.get().getString("onClanPrefixChanged")
                        .replace("{player}", sender.getName())
                        .replace("{display}", sender.getDisplayName())
                        .replace("{clan}", clanName)
                        .replace("{oldPrefix}", oldPrefix)
                        .replace("{newPrefix}", newPrefix));
        return true;
    }

    public static boolean onClanAlreadyExists(CommandSender sender, String clanName) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onClanAlreadyExists")
                        .replace("{clan}", clanName)));
        return true;
    }

    public static boolean onClanDoesNotExist(CommandSender sender, String clanName) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onClanDoesNotExist")
                        .replace("{clan}", clanName)));
        return true;
    }

    public static boolean onAlreadyInClan(Player sender, String clanName) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onAlreadyInClan")
                        .replace("{player}", sender.getName())
                        .replace("{display}", sender.getDisplayName())
                        .replace("{clan}", clanName)));
        return true;
    }

    public static boolean onCantLeaveBecauseOwner(Player sender, String clanName) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onCantLeaveBecauseOwner")
                        .replace("{player}", sender.getName())
                        .replace("{display}", sender.getDisplayName())
                        .replace("{clan}", clanName)));
        return true;
    }

    public static boolean onClanSetHome(Player sender, String clanName, Location location) {
        sendClanMessage(sender, ClanUtils.getClan(clanName),
                MessagesConfig.get().getString("onClanSetHome")
                        .replace("{player}", sender.getName())
                        .replace("{display}", sender.getDisplayName())
                        .replace("{clan}", clanName)
                        .replace("{x}", String.valueOf(location.getBlockX()))
                        .replace("{y}", String.valueOf(location.getBlockY()))
                        .replace("{z}", String.valueOf(location.getBlockZ()))
                        .replace("{world}", location.getWorld().getName()));
        return true;
    }

    public static boolean onClanRemoveHome(Player sender, String clanName) {
        sendClanMessage(sender, ClanUtils.getClan(clanName),
                MessagesConfig.get().getString("onClanRemoveHome")
                        .replace("{player}", sender.getName())
                        .replace("{display}", sender.getDisplayName())
                        .replace("{clan}", clanName));
        return true;
    }

    public static boolean onClanHomeNotSet(CommandSender sender, String clanName) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onClanHomeNotSet")
                        .replace("{clan}", clanName)));
        return true;
    }

    public static boolean onClanHomeTeleported(CommandSender sender, String clanName) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onClanHomeTeleported")
                        .replace("{clan}", clanName)));
        return true;
    }

    public static boolean onClanHomeNotPublic(CommandSender sender, String clanName) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onClanHomeNotPublic")
                        .replace("{clan}", clanName)));
        return true;
    }

    public static boolean onClanTooFewMembersForHome(CommandSender sender, String clanName, int minMembers) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onClanTooFewMembersForHome")
                        .replace("{clan}", clanName)
                        .replace("{minMembers}", String.valueOf(minMembers))));
        return true;
    }

    public static boolean onClanHomePublic(Player sender, String clanName) {
        sendClanMessage(sender, ClanUtils.getClan(clanName), MessagesConfig.get().getString("onClanHomePublic")
                .replace("{player}", sender.getName())
                .replace("{display}", sender.getDisplayName())
                .replace("{clan}", clanName));
        return true;
    }

    public static boolean onClanHomePrivate(Player sender, String clanName) {
        sendClanMessage(sender, ClanUtils.getClan(clanName), MessagesConfig.get().getString("onClanHomePrivate")
                .replace("{player}", sender.getName())
                .replace("{display}", sender.getDisplayName())
                .replace("{clan}", clanName));
        return true;
    }

    public static boolean onClanFriendlyFireOn(Player sender, String clanName) {
        sendClanMessage(sender, ClanUtils.getClan(clanName), MessagesConfig.get().getString("onClanFriendlyFireOn")
                .replace("{player}", sender.getName())
                .replace("{display}", sender.getDisplayName())
                .replace("{clan}", clanName));
        return true;
    }

    public static boolean onClanFriendlyFireOff(Player sender, String clanName) {
        sendClanMessage(sender, ClanUtils.getClan(clanName), MessagesConfig.get().getString("onClanFriendlyFireOff")
                .replace("{player}", sender.getName())
                .replace("{display}", sender.getDisplayName())
                .replace("{clan}", clanName));
        return true;
    }

    public static boolean onClanMemberAdded(Player addedPlayer, UUID clanUUID) {
        ClanDTO clan = ClanUtils.getClan(clanUUID);
        return sendClanMessage(addedPlayer, clan, MessagesConfig.get().getString("onClanMemberAdded")
                .replace("{clan}", clan.getName())
                .replace("{display}", addedPlayer.getDisplayName())
                .replace("{player}", addedPlayer.getName()));
    }

    public static boolean onClanMemberRemoved(CommandSender sender, String clanName, String playerName) {
        sendClanMessage(sender, ClanUtils.getClan(clanName), MessagesConfig.get().getString("onClanMemberRemoved")
                .replace("{clan}", clanName)
                .replace("{player}", playerName));
        return true;
    }

    public static boolean onClanAllyAdded(Player sender, String clanName, String allyName) {
        sendClanMessage(sender, ClanUtils.getClan(clanName), MessagesConfig.get().getString("onClanAllyAdded")
                .replace("{player}", sender.getName())
                .replace("{display}", sender.getDisplayName())
                .replace("{clan}", clanName)
                .replace("{ally}", allyName));
        return true;
    }

    public static boolean onClanAllyRemoved(Player sender, String clanName, String allyName) {
        sendClanMessage(sender, ClanUtils.getClan(clanName), MessagesConfig.get().getString("onClanAllyRemoved")
                .replace("{player}", sender.getName())
                .replace("{display}", sender.getDisplayName())
                .replace("{clan}", clanName)
                .replace("{ally}", allyName));
        return true;
    }

    public static boolean onClanAlreadyAllied(CommandSender sender, String clanName, String allyName) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onClanAlreadyAllied")
                        .replace("{clan}", clanName)
                        .replace("{ally}", allyName)));
        return true;
    }

    public static boolean onNoAlliesFound(CommandSender sender, String clanName) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onNoAlliesFound")
                        .replace("{clan}", clanName)));
        return true;
    }

    public static boolean onClanNotAllied(CommandSender sender, String clanName, String allyName) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onClanNotAllied")
                        .replace("{clan}", clanName)
                        .replace("{ally}", allyName)));
        return true;
    }

    public static boolean onCannotKickOwner(CommandSender sender, String clanName) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onCannotKickOwner")
                        .replace("{clan}", clanName)));
        return true;
    }

    public static boolean onCannotKickSelf(CommandSender sender, String clanName) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onCannotKickSelf")
                        .replace("{clan}", clanName)));
        return true;
    }

    public static boolean onClanLeaderAdded(CommandSender sender, String clanName, String playerName) {
        sendClanMessage(sender, ClanUtils.getClan(clanName), MessagesConfig.get().getString("onClanLeaderAdded")
                .replace("{clan}", clanName)
                .replace("{player}", playerName));
        return true;
    }

    public static boolean onClanLeaderRemoved(CommandSender sender, String clanName, String playerName) {
        sendClanMessage(sender, ClanUtils.getClan(clanName), MessagesConfig.get().getString("onClanLeaderRemoved")
                .replace("{clan}", clanName)
                .replace("{player}", playerName));
        return true;
    }

    public static boolean onCannotPromoteYourself(CommandSender sender, String clanName) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onCannotPromoteYourself")
                        .replace("{clan}", clanName)));
        return true;
    }

    public static boolean onPlayerAlreadyLeader(CommandSender sender, String clanName, String playerName) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onPlayerAlreadyLeader")
                        .replace("{clan}", clanName)
                        .replace("{player}", playerName)));
        return true;
    }

    public static boolean onPlayerNotALeader(CommandSender sender, String clanName, String playerName) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onPlayerNotALeader")
                        .replace("{clan}", clanName)
                        .replace("{player}", playerName)));
        return true;
    }

    public static boolean onClanOwnerChanged(Player sender, String clanName, String oldOwner, String newOwner) {
        sendClanMessage(sender, ClanUtils.getClan(clanName), MessagesConfig.get().getString("onClanOwnerChanged")
                .replace("{clan}", clanName)
                .replace("{player}", sender.getName())
                .replace("{display}", sender.getDisplayName())
                .replace("{oldOwnerName}", oldOwner)
                .replace("{newOwnerName}", newOwner));
        return true;
    }

    public static boolean onClanDescriptionChanged(Player sender, String clanName, String description) {
        sendClanMessage(sender, ClanUtils.getClan(clanName), MessagesConfig.get().getString("onClanDescriptionChanged")
                .replace("{player}", sender.getName())
                .replace("{display}", sender.getDisplayName())
                .replace("{clan}", clanName)
                .replace("{description}", description));
        return true;
    }

    public static boolean onMustBeLeader(CommandSender sender) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onMustBeLeader")));
        return true;
    }

    public static boolean onMustBeOwner(CommandSender sender) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onMustBeOwner")));
        return true;
    }

    public static boolean onMustBeMember(CommandSender sender) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onMustBeMember")));
        return true;
    }

    public static boolean onClanInviteCreated(Player sender, Player invitedPlayer, String clanName) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onClanInviteCreated")
                        .replace("{player}", sender.getName())
                        .replace("{display}", sender.getDisplayName())
                        .replace("{clan}", clanName)
                        .replace("{invitedPlayer}", invitedPlayer.getName())
                        .replace("{invitedPlayerDisplay}", invitedPlayer.getDisplayName())));
        invitedPlayer.sendMessage(formatMessage(invitedPlayer,
                MessagesConfig.get().getString("onClanInviteReceived")
                        .replace("{player}", sender.getName())
                        .replace("{display}", sender.getDisplayName())
                        .replace("{clan}", clanName)
                        .replace("{invitedPlayer}", invitedPlayer.getName())
                        .replace("{invitedPlayerDisplay}", invitedPlayer.getDisplayName())));
        return true;
    }

    public static boolean onClanInviteRemoved(Player sender, Player invitedPlayer, String clanName) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onClanInviteRemoved")
                        .replace("{player}", sender.getName())
                        .replace("{display}", sender.getDisplayName())
                        .replace("{clan}", clanName)
                        .replace("{invitedPlayer}", invitedPlayer.getName())
                        .replace("{invitedPlayerDisplay}", invitedPlayer.getDisplayName())));
        invitedPlayer.sendMessage(formatMessage(invitedPlayer,
                MessagesConfig.get().getString("onClanInviteRemovedReceived")
                        .replace("{player}", sender.getName())
                        .replace("{display}", sender.getDisplayName())
                        .replace("{clan}", clanName)
                        .replace("{invitedPlayer}", invitedPlayer.getName())
                        .replace("{invitedPlayerDisplay}", invitedPlayer.getDisplayName())));
        return true;
    }

    public static boolean onClanInviteExpired(Player sender, Player invitedPlayer, String clanName) {
        invitedPlayer.sendMessage(formatMessage(invitedPlayer,
                MessagesConfig.get().getString("onClanInviteExpired")
                        .replace("{player}", sender.getName())
                        .replace("{display}", sender.getDisplayName())
                        .replace("{invitedPlayer}", invitedPlayer.getName())
                        .replace("{invitedPlayerDisplay}", invitedPlayer.getDisplayName())
                        .replace("{clan}", clanName)));
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onClanInviteExpiredReceived")
                        .replace("{player}", sender.getName())
                        .replace("{display}", sender.getDisplayName())
                        .replace("{invitedPlayer}", invitedPlayer.getName())
                        .replace("{invitedPlayerDisplay}", invitedPlayer.getDisplayName())
                        .replace("{clan}", clanName)));
        return true;
    }

    public static boolean onClanInviteDeclined(Player invitedPlayer, String clanName) {
        invitedPlayer.sendMessage(formatMessage(invitedPlayer,
                MessagesConfig.get().getString("onClanInviteDeclined")
                        .replace("{invitedPlayer}", invitedPlayer.getName())
                        .replace("{invitedPlayerDisplay}", invitedPlayer.getDisplayName())
                        .replace("{clan}", clanName)));
        return true;
    }

    public static boolean onClanInviteDeclinedReceived(Player sender, Player invitedPlayer, String clanName) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onClanInviteDeclinedReceived")
                        .replace("{player}", sender.getName())
                        .replace("{display}", sender.getDisplayName())
                        .replace("{invitedPlayer}", invitedPlayer.getName())
                        .replace("{invitedPlayerDisplay}", invitedPlayer.getDisplayName())
                        .replace("{clan}", clanName)));
        return true;
    }

    public static boolean onClanInviteNotFound(Player sender, String clanName) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onClanInviteNotFound")
                        .replace("{player}", sender.getName())
                        .replace("{display}", sender.getDisplayName())
                        .replace("{clan}", clanName)));
        return true;
    }

    public static boolean onClanInviteNotSent(Player sender, Player invitedPlayer, String clanName) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onClanInviteNotSent")
                        .replace("{player}", sender.getName())
                        .replace("{display}", sender.getDisplayName())
                        .replace("{invitedPlayer}", invitedPlayer.getName())
                        .replace("{invitedPlayerDisplay}", invitedPlayer.getDisplayName())
                        .replace("{clan}", clanName)));
        return true;
    }

    public static boolean onClanLeave(Player sender, String clanName) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onClanLeave")
                        .replace("{player}", sender.getName())
                        .replace("{display}", sender.getDisplayName())
                        .replace("{clan}", clanName)));
        return true;
    }

    public static boolean onPlayerHasLeftClan(Player sender, String clanName) {
        ClanDTO clan = ClanUtils.getClan(clanName);
        return sendClanMessage(sender, clan,
                MessagesConfig.get().getString("onPlayerHasLeftClan")
                        .replace("{player}", sender.getName())
                        .replace("{display}", sender.getDisplayName())
                        .replace("{clan}", clanName)
        );
    }

    public static boolean onPlayerNotInClan(Player sender, String player) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onPlayerNotInClan"))
                .replace("{target}", player));
        return true;
    }

    public static boolean onCannotAllyYourself(Player sender, String clanName) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onCannotAllyYourself")
                        .replace("{player}", sender.getName())
                        .replace("{display}", sender.getDisplayName())
                        .replace("{clan}", clanName)));
        return true;
    }

    public static boolean onClanMaxMembersReached(Player sender, String clanName, int maxMembers) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onClanMaxMembersReached")
                        .replace("{player}", sender.getName())
                        .replace("{display}", sender.getDisplayName())
                        .replace("{maxMembers}", String.valueOf(maxMembers))
                        .replace("{clan}", clanName)));
        return true;
    }

    public static boolean onNoClansFound(Player sender) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onNoClansFound")));
        return true;
    }

    public static boolean onClanKicked(Player sender, Player target, String clanName) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onClanKicked")
                        .replace("{player}", sender.getName())
                        .replace("{display}", sender.getDisplayName())
                        .replace("{target}", target.getName())
                        .replace("{targetDisplay}", target.getDisplayName())
                        .replace("{clan}", clanName)));
        return true;
    }

    public static boolean onInvalidCodes(Player sender) {
        sender.sendMessage(formatMessage(sender,
                MessagesConfig.get().getString("onInvalidCodes")));
        return true;
    }

}
