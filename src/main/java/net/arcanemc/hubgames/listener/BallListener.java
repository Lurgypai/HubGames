package net.arcanemc.hubgames.listener;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import net.arcanemc.hubgames.HubMinigames;
import net.arcanemc.hubgames.NMS.CustomFallingBlock;
import net.arcanemc.hubgames.NMS.CustomSlime;
import net.arcanemc.hubgames.game.BGame;

public class BallListener implements Listener {
	
	BGame game = ((BGame) HubMinigames.getInstance().getGameManager().getGame("basketball"));
	
	@EventHandler
	public void playerClickEntity(PlayerInteractEntityEvent e) {
		Player player = e.getPlayer();
		Entity entity = e.getRightClicked();
		if(game.getTeams().isPlaying(player.getUniqueId())) {
			if (((CraftEntity)entity).getHandle() instanceof CustomFallingBlock) {
					game.getBall().katch(player);
			}
		}
	}
	
	@EventHandler
	public void playerClick(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if ((player.getPassenger() != null) && (((CraftEntity)player.getPassenger()).getHandle() instanceof CustomSlime)) {
			game.getBall().thruw();
		}
	}
	
	@EventHandler
	public void onHit(EntityDamageByEntityEvent e) {
		if (((CraftEntity)e.getEntity()).getHandle() instanceof CustomSlime) {
			e.setCancelled(true);
			if ((e.getDamager() instanceof Player) && ((e.getEntity().getVehicle() != null) && (e.getEntity().getVehicle() instanceof Player))) {
				if (e.getDamager() == e.getEntity().getVehicle())
				game.getBall().thruw();
			}
		}
	}
	
	@EventHandler
	public void land(EntityDamageEvent e) {
		if (((CraftEntity)e.getEntity()).getHandle() instanceof CustomSlime) {
			e.setCancelled(true);
		}
	}
}
