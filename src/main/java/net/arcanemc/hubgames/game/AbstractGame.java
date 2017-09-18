package net.arcanemc.hubgames.game;

import java.util.UUID;

import net.arcanemc.hubgames.game.team.TeamManager;
import net.arcanemc.hubgames.region.Region;

public abstract class AbstractGame {
	
	protected Region region;
	protected TeamManager teams = new TeamManager();
	
	public TeamManager getTeams() {
		return this.teams;
	}
	
	public void setTeam(TeamManager teams_) {
		this.teams = teams_;
	}
	
	public Region getRegion() {
		return this.region;
	}
	

	public void setRegion(Region _region) {
		this.region = _region;
	}
	
	public abstract void update();
	public abstract void startGame();
	public abstract void stopGame();
	public abstract void joinGame(UUID uuid_, String teamName_);
	
	
}
