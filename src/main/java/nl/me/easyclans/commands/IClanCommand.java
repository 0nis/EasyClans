package nl.me.easyclans.commands;

import org.bukkit.entity.Player;

public interface IClanCommand {

    boolean execute(Player player, String[] args);

}
