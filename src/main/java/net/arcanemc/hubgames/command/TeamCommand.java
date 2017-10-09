package net.arcanemc.hubgames.command;

import org.bukkit.entity.Player;

import net.arcanemc.core.api.commands.nCommands.Command;
import net.arcanemc.core.api.commands.nCommands.Inject;
import net.arcanemc.hubgames.HubMinigames;

public class TeamCommand {
	@Command(names = { "team" })
	public void onTeamCommand(@Inject(data = Inject.Data.SENDER, nullable = false)Player player, @Inject(nullable = true)String args) {
				HubMinigames.getInstance().getGameManager().joinPlayerToGame(player.getUniqueId(), args);
	}
}
