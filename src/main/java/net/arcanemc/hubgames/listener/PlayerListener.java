package net.arcanemc.hubgames.listener;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import net.arcanemc.hubgames.HubMinigames;
import net.arcanemc.hubgames.game.AbstractGame;
import net.arcanemc.hubgames.game.team.Team;

public class PlayerListener implements Listener {
	
	private HubMinigames basketball = HubMinigames.getInstance();
	
	@EventHandler
	public void playerLeave(PlayerQuitEvent e) {
		for (AbstractGame game : new ArrayList<AbstractGame>(basketball.getGameManager().getGames().values())) {
			if(game.getTeams().isPlaying(e.getPlayer().getUniqueId())) {
				for(Team team : game.getTeams().getTeams()) {
					if(team.hasPlayer(e.getPlayer().getUniqueId())) {
						team.removePlayer(e.getPlayer().getUniqueId());
						if (CraftItemStack.asNMSCopy(e.getPlayer().getInventory().getItem(1)).getTag().hasKey("kothSword")) {
							e.getPlayer().getInventory().setItem(1, new ItemStack(Material.AIR));
						}
					}
				}
			}
		}
	}
}
