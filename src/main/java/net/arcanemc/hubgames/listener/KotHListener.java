package net.arcanemc.hubgames.listener;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import net.arcanemc.hubgames.HubMinigames;
import net.arcanemc.hubgames.game.team.Team;

public class KotHListener implements Listener {
	
	private HubMinigames plugin;
	
	public KotHListener(HubMinigames plugin_) {
		this.plugin = plugin_;
	}
	
	@EventHandler
	public void onPlayerHit(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			e.setCancelled(true);
			Player player = (Player)e.getEntity();
			Player damager = (Player)e.getDamager();
			if (plugin.getGameManager().getGame("koth").getTeams().isPlaying(player.getUniqueId()) && plugin.getGameManager().getGame("koth").getTeams().isPlaying(damager.getUniqueId())) {
				if (!plugin.getGameManager().getGame("koth").getTeams().getTeam(player.getUniqueId()).equals(plugin.getGameManager().getGame("koth").getTeams().getTeam(damager.getUniqueId()))) {
					if (!(player.getHealth() <= 3.0D)) {
						player.damage(3.0D);
						player.setVelocity(player.getVelocity().add(damager.getEyeLocation().getDirection().multiply(1.5D)));
					} else {
						player.setHealth(20.0D);
						player.teleport(Bukkit.getWorld(plugin.HUBWORLD).getSpawnLocation());
						player.setVelocity(new Vector());
					}
				}
			} 
		}
	}

}