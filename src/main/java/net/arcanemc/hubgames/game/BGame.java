package net.arcanemc.hubgames.game;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftFallingSand;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftSlime;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Slime;

import net.arcanemc.hubgames.HubMinigames;
import net.arcanemc.hubgames.NMS.CustomFallingBlock;
import net.arcanemc.hubgames.NMS.CustomSlime;
import net.arcanemc.hubgames.ball.BBall;
import net.arcanemc.hubgames.game.team.Team;
import net.arcanemc.hubgames.region.Region;
import net.md_5.bungee.api.ChatColor;

public class BGame extends AbstractGame{
	
	private HubMinigames plugin;
	private Region hoop1;
	private Region hoop2;
	private BBall ball;
	
	public BGame(HubMinigames plugin_, Region _region, Region hoop1_, Region hoop2_) {
		this.plugin = plugin_;
		this.region = _region;
		this.hoop1 = hoop1_;
		this.hoop2 = hoop2_;
	}
	
	public Region getHoop1() {
		return this.hoop1;
	}
	
	public Region getHoop2 () {
		return this.hoop2;
	}
	
	public BBall getBall() {
		return this.ball;
	}
	
	@Override
	public void update() {
		this.ball.update();
	}
	
	@Override
	public void startGame() {
		ball = new BBall(plugin, this);
	}

	@Override
	public void stopGame() {
		for(Team team : teams.getTeams()) {
			team.clearTeam();
			team.setPoints(0);
		}
		
		for (Entity entity : Bukkit.getWorld(plugin.HUBWORLD).getEntitiesByClass(Slime.class)) {
			if(((CraftSlime)entity).getHandle() instanceof CustomSlime) {
				((Slime)entity).remove();
			}
		}
		
		for (Entity entity : Bukkit.getWorld(plugin.HUBWORLD).getEntitiesByClass(FallingBlock.class)) {
			if(((CraftFallingSand)entity).getHandle() instanceof CustomFallingBlock) {
				((FallingBlock)entity).remove();
			}
		}
	}
	@Override
	public void joinGame(UUID uuid_, String teamName_) {
		Team team = teams.getTeam(teamName_);
		if (team != null) {
			if (team.getName() == "red" || team.getName() == "blue") {
				team.addPlayer(uuid_);
			} else {
				Bukkit.getPlayer(uuid_).sendMessage(HubMinigames.formatTextForHub("That " + ChatColor.GOLD + "Team " + ChatColor.RESET + "isn't used!"));
			}
		} else {
			Bukkit.getPlayer(uuid_).sendMessage(HubMinigames.formatTextForHub("Couldn't find that " + ChatColor.GOLD + "Team" + ChatColor.RESET + "!"));
		}
	}
}
