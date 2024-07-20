package nl.me.easyclans.commands.clan_commands;

import nl.me.easyclans.EasyClans;
import nl.me.easyclans.commands.IClanCommand;
import nl.me.easyclans.helpers.dto.ClanDTO;
import nl.me.easyclans.helpers.utils.ClanUtils;
import nl.me.easyclans.helpers.utils.MessageUtils;
import nl.me.easyclans.helpers.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;

public class HomeCommand implements IClanCommand {
    @Override
    public boolean execute(Player player, String[] args) {
        if (!player.hasPermission("easyclans.clan.home") && !player.isOp()) return MessageUtils.onNoPermission(player, "easyclans.clan.home");
        if (args.length == 1) {
            ClanDTO clan = PlayerUtils.getClan(player);
            if (clan == null) return MessageUtils.onMustBeMember(player);
            if (clan.getHome() == null) return MessageUtils.onClanHomeNotSet(player, clan.getName());
            teleportPlayerWithVehicle(player, clan.getHome());
            return MessageUtils.onClanHomeTeleported(player, clan.getName());
        } else {
            String clanName = args[1];
            ClanDTO clan = ClanUtils.getClan(clanName);
            if (clan == null) return MessageUtils.onClanDoesNotExist(player, clanName);
            if (!clan.getName().equals(clanName)) return MessageUtils.onClanDoesNotExist(player, clanName);
            if (clan.getHome() == null) return MessageUtils.onClanHomeNotSet(player, clan.getName());
            if (!clan.isHomePublic()) return MessageUtils.onClanHomeNotPublic(player, clan.getName());
            teleportPlayerWithVehicle(player, clan.getHome());
            return MessageUtils.onClanHomeTeleported(player, clan.getName());
        }
    }

    /**
     * This method teleports the player to a specified location, including their vehicle if they are in one.
     * @param player The player to teleport (including their vehicle)
     * @param location The location to teleport the player (including their vehicle) to
     */
    public static void teleportPlayerWithVehicle(Player player, Location location) {
        Entity vehicle = player.getVehicle();
        if (vehicle != null && vehicle.isValid() && vehicle.getPassengers().contains(player) && vehicle.getType() != EntityType.MINECART) {
            player.leaveVehicle();
            List<Entity> passengers = vehicle.getPassengers();
            for (Entity passenger : passengers) {
                passenger.leaveVehicle();
            }
            vehicle.teleport(location);
            for (Entity passenger : passengers) {
                passenger.teleport(location);
            }
            player.teleport(location);

            player.hideEntity(EasyClans.getPlugin(), vehicle);
            player.showEntity(EasyClans.getPlugin(), vehicle);

            for (Entity passenger : passengers) {
                player.hideEntity(EasyClans.getPlugin(), passenger);
                player.showEntity(EasyClans.getPlugin(), passenger);
            }

            Bukkit.getScheduler().runTaskLater(EasyClans.getPlugin(), () -> {
                vehicle.addPassenger(player);
                if (!passengers.isEmpty()) {
                    for (Entity passenger : passengers) {
                        vehicle.addPassenger(passenger);
                    }
                }
            }, 3L);
        } else {
            player.teleport(location);
        }
    }
}
