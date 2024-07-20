package nl.me.easyclans.commands;

import nl.me.easyclans.commands.admin_commands.GetClanIDCommand;
import nl.me.easyclans.commands.admin_commands.ReloadCommand;
import nl.me.easyclans.commands.clan_commands.*;
import nl.me.easyclans.commands.clan_commands.invites.AcceptCommand;
import nl.me.easyclans.commands.clan_commands.invites.CancelCommand;
import nl.me.easyclans.commands.clan_commands.invites.DeclineCommand;
import nl.me.easyclans.commands.clan_commands.invites.InviteCommand;
import nl.me.easyclans.commands.clan_commands.settings.AllyCommand;
import nl.me.easyclans.commands.clan_commands.settings.DemoteCommand;
import nl.me.easyclans.commands.clan_commands.settings.PromoteCommand;
import nl.me.easyclans.commands.clan_commands.settings.SetCommand;
import nl.me.easyclans.helpers.dto.ClanDTO;
import nl.me.easyclans.helpers.dto.InviteDTO;
import nl.me.easyclans.helpers.utils.ClanUtils;
import nl.me.easyclans.helpers.utils.InviteUtils;
import nl.me.easyclans.helpers.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ClanCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 0) {
            IClanCommand clanCommand = new HelpCommand();
            return clanCommand.execute((Player) commandSender, args);
        } else if (args.length > 0) {
            if (!commandSender.hasPermission("easyclans.clan") && !commandSender.isOp()) return MessageUtils.onNoPermission(commandSender, "easyclans.clan");
            if (!(commandSender instanceof Player)) return MessageUtils.onMustBePlayer(commandSender);
            switch (args[0]) {
                case "create": {
                    IClanCommand clanCommand = new CreateCommand();
                    return clanCommand.execute((Player) commandSender, args);
                }
                case "disband": {
                    IClanCommand clanCommand = new DisbandCommand();
                    return clanCommand.execute((Player) commandSender, args);
                }
                case "invite": {
                    IClanCommand clanCommand = new InviteCommand();
                    return clanCommand.execute((Player) commandSender, args);
                }
                case "accept": {
                    IClanCommand clanCommand = new AcceptCommand();
                    return clanCommand.execute((Player) commandSender, args);
                }
                case "cancel": {
                    IClanCommand clanCommand = new CancelCommand();
                    return clanCommand.execute((Player) commandSender, args);
                }
                case "decline": {
                    IClanCommand clanCommand = new DeclineCommand();
                    return clanCommand.execute((Player) commandSender, args);
                }
                case "kick": {
                    IClanCommand clanCommand = new KickCommand();
                    return clanCommand.execute((Player) commandSender, args);
                }
                case "leave": {
                    IClanCommand clanCommand = new LeaveCommand();
                    return clanCommand.execute((Player) commandSender, args);
                }
                case "promote": {
                    IClanCommand clanCommand = new PromoteCommand();
                    return clanCommand.execute((Player) commandSender, args);
                }
                case "demote": {
                    IClanCommand clanCommand = new DemoteCommand();
                    return clanCommand.execute((Player) commandSender, args);
                }
                case "info": {
                    IClanCommand clanCommand = new InfoCommand();
                    return clanCommand.execute((Player) commandSender, args);
                }
                case "list": {
                    IClanCommand clanCommand = new ListCommand();
                    return clanCommand.execute((Player) commandSender, args);
                }
                case "set": {
                    IClanCommand clanCommand = new SetCommand();
                    return clanCommand.execute((Player) commandSender, args);
                }
                case "home": {
                    IClanCommand clanCommand = new HomeCommand();
                    return clanCommand.execute((Player) commandSender, args);
                }
                case "ally": {
                    IClanCommand clanCommand = new AllyCommand();
                    return clanCommand.execute((Player) commandSender, args);
                }
                case "chat": {
                    IClanCommand clanCommand = new ChatCommand();
                    return clanCommand.execute((Player) commandSender, args);
                }
                case "help": {
                    IClanCommand clanCommand = new HelpCommand();
                    return clanCommand.execute((Player) commandSender, args);
                }
                case "reload": {
                    IClanCommand clanCommand = new ReloadCommand();
                    return clanCommand.execute((Player) commandSender, args);
                }
                default: {
                    IClanCommand clanCommand = new HelpCommand();
                    return clanCommand.execute((Player) commandSender, args);
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (!commandSender.hasPermission("easyclans.clan") && !commandSender.isOp()) return Collections.emptyList();
        if (args.length == 1) {
            return Arrays.asList("create", "disband", "info", "chat", "invite", "accept", "cancel", "decline", "leave", "kick", "set", "promote", "demote", "home").stream()
                    .filter(name -> name.toLowerCase().contains(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            switch (args[0]) {
                case "create":
                    return Collections.singletonList("<name>");
                case "info":
                case "home":
                    return ClanUtils.getAlLClanNames();
                case "ally":
                    return Arrays.asList("add", "remove").stream()
                            .filter(name -> name.toLowerCase().contains(args[1].toLowerCase()))
                            .collect(Collectors.toList());
                case "invite":
                    return Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .filter(name -> name.toLowerCase().contains(args[1].toLowerCase()))
                            .collect(Collectors.toList());
                case "accept":
                case "decline":
                    if (!(commandSender instanceof Player)) return Collections.emptyList();
                    List<InviteDTO> clansInvited = InviteUtils.getInvitesByPlayer(((Player) commandSender).getUniqueId());
                    if (clansInvited != null && !clansInvited.isEmpty())
                        return clansInvited.stream()
                                .map(invite -> ClanUtils.getClanNameFromUUID(invite.getClan()))
                                .collect(Collectors.toList());
                    else return Collections.emptyList();
                case "cancel":
                    if (!(commandSender instanceof Player)) return Collections.emptyList();
                    ClanDTO clan = ClanUtils.getClan(((Player) commandSender).getUniqueId());
                    if (clan != null) {
                        List<InviteDTO> invites = InviteUtils.getInvitesByClan(clan.getClanId());
                        if (invites != null && !invites.isEmpty())
                            return invites.stream()
                                    .map(invite -> Bukkit.getOfflinePlayer(invite.getInvitedPlayer()).getName())
                                    .collect(Collectors.toList());
                        else return Collections.emptyList();
                    } else return Collections.emptyList();
                case "promote":
                case "demote":
                    if (!(commandSender instanceof Player)) return Collections.emptyList();
                    ClanDTO playerClan = ClanUtils.getClan(((Player) commandSender).getUniqueId());
                    if (playerClan != null) {
                        List<Player> members = ClanUtils.getOnlinePlayersInClan(playerClan);
                        if (members != null && !members.isEmpty())
                            return members.stream()
                                    .map(Player::getName)
                                    .filter(name -> name.toLowerCase().contains(args[1].toLowerCase()))
                                    .collect(Collectors.toList());
                        else return Collections.emptyList();
                    } else return Collections.emptyList();
                case "chat":
                    return Collections.singletonList("<message>");
                case "set":
                    return Arrays.asList("name", "prefix", "description", "home", "friendlyFireEnabled", "owner").stream()
                            .filter(name -> name.toLowerCase().contains(args[1].toLowerCase()))
                            .collect(Collectors.toList());
                default:
                    return Collections.emptyList();
            }
        }
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("ally")) {
                return ClanUtils.getAlLClanNames();
            }
            if (args[0].equalsIgnoreCase("set")) {
                switch (args[1]) {
                    case "name":
                        return Collections.singletonList("<name>");
                    case "prefix":
                        return Collections.singletonList("<prefix>");
                    case "description":
                        return Collections.singletonList("<description>");
                    case "home":
                        return Arrays.asList("here", "none", "public", "private");
                    case "friendlyFireEnabled":
                        return Arrays.asList("true", "false");
                    case "owner":
                        return Bukkit.getOnlinePlayers().stream()
                                .map(Player::getName)
                                .filter(name -> name.toLowerCase().contains(args[2].toLowerCase()))
                                .collect(Collectors.toList());
                    default:
                        return Collections.emptyList();
                }
            }
        }
        return Collections.emptyList();
    }
}
