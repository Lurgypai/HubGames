package net.arcanemc.hubgames.game.team;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.DyeColor;

import net.arcanemc.hubgames.region.Region;

public class TeamManager {
	private ArrayList<Team> teams = new ArrayList<Team>();
	
	public TeamManager() {
		
		Team red = new Team("red", "&cRed&r", 250, 25, 10, DyeColor.RED);		
		Team blue = new Team("blue", "&9Blue&r", 10, 90, 250, DyeColor.BLUE);		
		Team green = new Team("green", "&aGreen&r", 70, 170, 20, DyeColor.GREEN);		
		Team yellow = new Team("yellow", "&eYellow&r", 250, 250, 30, DyeColor.YELLOW);
		
		teams.add(red);
		teams.add(blue);
		teams.add(green);
		teams.add(yellow);
	}
	
	public ArrayList<Team> getTeams() {
		return this.teams;
	}
	
	public Team getTeam(String id) {
		for (Team team : this.teams) {
			if (team.getName().equalsIgnoreCase(id)) {
				return team;
			}
		}
		return null;
	}
	
	public Team getTeam(UUID player) {
		for (Team team : this.teams) {
			for(UUID uuid : team.getPlayers()) {
				if (player.equals(uuid))
					return team;
			}
		}
		return null;
	}
	
	public void update(Region region_) {
		for (Team team : this.teams) {
			team.update(region_);
		}
	}
	
	public boolean isPlaying(UUID uuid_) {
		 for (Team team : this.teams) {
			 if (team.hasPlayer(uuid_)) {
				 return true;
			 }
		 }
		 return false;
	}
}
