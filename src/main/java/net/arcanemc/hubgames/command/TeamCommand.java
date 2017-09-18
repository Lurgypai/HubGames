package net.arcanemc.hubgames.command;

import org.bukkit.entity.Player;

import net.arcanemc.core.api.commands.Command;
import net.arcanemc.core.api.commands.CommandBase;
import net.arcanemc.hubgames.HubMinigames;

public class TeamCommand extends CommandBase {
	
	
	
	@Command(name = "team", playerOnly = true)
	public void onTeamCommand(Player player, String[] args) {
			if(args.length == 1) {
				HubMinigames.getInstance().getGameManager().joinPlayerToGame(player.getUniqueId(), args[0]);
			}
	}
}
