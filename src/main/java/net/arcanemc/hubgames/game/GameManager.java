package net.arcanemc.hubgames.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.arcanemc.hubgames.HubMinigames;
import net.arcanemc.hubgames.game.team.Team;

public class GameManager {
	
	private HubMinigames plugin;
	private int task;
	private HashMap<String, AbstractGame> games;
	HashMap<Player, AbstractGame> booted = new HashMap<Player, AbstractGame>();
	
	public GameManager(HubMinigames games) {
		plugin = games;
		this.games = new HashMap<String, AbstractGame>();
	}
	
	public void addGame(String id, AbstractGame game) {
		this.games.put(id, game);
	}
	
	public void removeGame(AbstractGame game) {
		this.games.remove(game);
	}
	
	public AbstractGame getGame(String id) {
		return this.games.get(id);
	}
	
	public HashMap<String, AbstractGame> getGames() {
		return this.games;
	}
	
	private void updateGames() {
		ArrayList<AbstractGame> games_ = new ArrayList<AbstractGame>(games.values());
		for (AbstractGame game : games_) {
			game.update();
			game.getTeams().update(game.getRegion());
			for(Player player : Bukkit.getServer().getWorld(plugin.HUBWORLD).getPlayers()) {
				if (!game.getTeams().isPlaying(player.getUniqueId())){
					if(game.getRegion().contains(player.getLocation()) && (booted.get(player) == null || !(booted.get(player).equals(game)))) {
						game.getRegion().bootPlayer(player);
						player.sendMessage(HubMinigames.formatTextForHub("You need to be on a " + ChatColor.GOLD + "Team " + ChatColor.RESET + "to play!"));
						player.sendMessage(HubMinigames.formatTextForHub("Join using " + ChatColor.GOLD + "/team <color>" + ChatColor.RESET + "!"));
						booted.put(player, game);
					} else if (!game.getRegion().contains(player.getLocation()) && booted.get(player) != null && booted.get(player).equals(game)) {
						booted.remove(player);
					}
				}
			}
		}
	}
	
	public void startGames() {
		Bukkit.getLogger().info("[HubGames] Starting Games");
		ArrayList<AbstractGame> games_ = new ArrayList<AbstractGame>(games.values());
		for (AbstractGame game : games_) {
			game.startGame();
		}
		
		task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new GameRunnable(this), 1, 1);
	}
	
	public void stopGames() {
		ArrayList<AbstractGame> games_ = new ArrayList<AbstractGame>(games.values());
		for (AbstractGame game : games_) {
			game.stopGame();
		}
		
		Bukkit.getServer().getScheduler().cancelTask(task);
	}
	
	public void joinPlayerToGame(UUID uuid, String teamName_) {
		int shortestDistance = -1;
		AbstractGame joinGame = null;
		for (AbstractGame game : new ArrayList<AbstractGame>(this.games.values())) {
			int distance = (int)(game.getRegion().getCenter().distance(Bukkit.getPlayer(uuid).getLocation()));
			if (shortestDistance == -1 || distance < shortestDistance) {
				shortestDistance = distance;
				joinGame = game;
			}
		}
		if (joinGame != null) {
			if (shortestDistance < Team.LEAVE_DISTANCE) {
				for (AbstractGame game : new ArrayList<AbstractGame>(this.games.values())) {
					if (game.getTeams().isPlaying(uuid)) {
						for (Team team : game.getTeams().getTeams()) {
							if(team.hasPlayer(uuid))  {
								if (joinGame.getTeams().getTeam(teamName_) != null && team != joinGame.getTeams().getTeam(teamName_)) {
								team.removePlayer(uuid);
								} else {
									Bukkit.getPlayer(uuid).sendMessage(HubMinigames.formatTextForHub("You're already on team "+ team.getDisplayName() + "!"));
								}
							}
						}
					}
				}
				joinGame.joinGame(uuid, teamName_);
			} else {
				Bukkit.getPlayer(uuid).sendMessage(HubMinigames.formatTextForHub("You aren't close enough to any " + ChatColor.GOLD + "Games" + ChatColor.RESET + "!"));
			}
		}
	}
	
	private class GameRunnable implements Runnable {

		private GameManager manager;
		
		public GameRunnable(GameManager manager_) {
			manager = manager_;
		}
		
		@Override
		public void run() {
			manager.updateGames();
		}

	}
}
