package nl.me.easyclans.integrations.discordsrv;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessagePreProcessEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import org.bukkit.Bukkit;

import java.util.UUID;

public class DiscordClanChatListener {

    @Subscribe
    public void onDiscordMessage(DiscordGuildMessagePreProcessEvent event) {
        String channelName = DiscordSRV.getPlugin().getDestinationGameChannelNameForTextChannel(event.getChannel());
        Bukkit.getLogger().info("Channel name: " + channelName);
        if (channelName != null && !channelName.isEmpty() && !channelName.equals("easyclans-admin") && channelName.startsWith("easyclans-")) {
            event.setCancelled(true);
            User user = event.getAuthor();
            if (user == null) return;
            if (user.isBot()) return;
            channelName = channelName.substring(10);
            UUID clanId = UUID.fromString(channelName);
            String message = event.getMessage().getContentRaw();
            DiscordSRVUtils.sendDiscordMessageToClanChannel(user, message, clanId);
        }
    }

}
