package nl.me.easyclans.integrations.discordsrv;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import nl.me.easyclans.EasyClans;
import nl.me.easyclans.helpers.EasyClansResponse;
import nl.me.easyclans.helpers.Status;
import nl.me.easyclans.helpers.dto.ClanDTO;
import nl.me.easyclans.helpers.utils.ClanUtils;
import nl.me.easyclans.helpers.utils.MessageUtils;
import nl.me.easyclans.helpers.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class DiscordSRVUtils {

    private static final String ADMIN_CHANNEL = "easyclans-admin";

    /**
     * Validates whether DiscordSRV is installed or not
     * @return true if DiscordSRV is installed, false otherwise
     */
    public static boolean isDiscordSRVEnabled() {
        return Bukkit.getPluginManager().getPlugin("DiscordSRV") != null;
    }

    /**
     * Subscribes to DiscordSRV events
     */
    public static void subscribeToDiscordSRV() {
        if (!isDiscordSRVEnabled()) return;
        try {
            DiscordSRV.api.subscribe(new DiscordClanChatListener());
        } catch (Exception e) {
            Bukkit.getLogger().warning("[EasyClans] Unable to subscribe to DiscordSRV events");
        }
    }

    /**
     * Gets the channel to send admin messages to
     * @return the channel to send admin messages to
     */
    public static TextChannel getClansAdminChannel() {
        if (!isDiscordSRVEnabled()) return null;
        return DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("easyclans-admin");
    }

    public static TextChannel getClansChannel(UUID clanId) {
        if (!isDiscordSRVEnabled()) return null;
        return DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("easyclans-" + clanId.toString());
    }

    /**
     * Sends a message to discord
     * @param sender the sender to send the message to
     */
    private static void sendToDiscord(Consumer<TextChannel> sender) {
        TextChannel channel = getClansAdminChannel();
        if (channel == null) Bukkit.getLogger().warning("[EasyClans] Unable to send message to discord channel");
        else sender.accept(channel);
    }

    /**
     * Sends a message to the admin channel
     * @param author the author of the message
     * @param message the message to send
     */
    public static void sendMessageToAdminChannel(Player author, String message, String clanName) {
        if (!isDiscordSRVEnabled()) return;
        String messageFormat = EasyClans.getPlugin().getConfig().getString("messageFormat-clans-to-discord-admin");
        if (messageFormat == null) messageFormat = "[{clan}] {player}: {message}";
        message = messageFormat
                .replace("{clan}", clanName)
                .replace("{player}", author.getName())
                .replace("{display}", author.getDisplayName())
                .replace("{message}", message);
        final String finalMessage = message;
        // sendToDiscord(channel -> {
            DiscordSRV.getPlugin().processChatMessage(author, finalMessage, ADMIN_CHANNEL, false);
        // });
    }

    public static void sendMessageToClanChannel(Player author, String message, String clanName, UUID clanId) {
        if (!isDiscordSRVEnabled()) return;
        String messageFormat = EasyClans.getPlugin().getConfig().getString("messageFormat-clans-to-discord");
        if (messageFormat == null) messageFormat = "{message}";
        message = messageFormat
                .replace("{clan}", clanName)
                .replace("{player}", author.getName())
                .replace("{display}", author.getDisplayName())
                .replace("{message}", message);
        final String finalMessage = message;
        DiscordSRV.getPlugin().processChatMessage(author, finalMessage, "easyclans-" + clanId.toString(), false);
    }

    public static void sendDiscordMessageToClanChannel(User author, String message, UUID clanId) {
        if (!isDiscordSRVEnabled()) return;
        ConfigurationSection config = EasyClans.getPlugin().getConfig();
        String messageFormat = config.getString("messageFormat-discord-to-clans");
        if (messageFormat == null) messageFormat = "&8[&e&l{clan}&8] &8[&3Discord&8] &a{player} &8> &f{message}";
        String clanName = ClanUtils.getClanNameFromUUID(clanId);
        if (clanName == null) return;
        message = messageFormat
                .replace("{clan}", clanName)
                .replace("{player}", author.getName())
                .replace("{display}", author.getName())
                .replace("{message}", message);
        ClanDTO clan = ClanUtils.getClan(clanId);
        if (clan == null) return;
        List<Player> onlineClanMembers = ClanUtils.getOnlinePlayersInClan(clan);
        for (Player onlineClanMember : onlineClanMembers) {
            onlineClanMember.sendMessage(MessageUtils.formatMessage(onlineClanMember, message));
        }
    }

}
