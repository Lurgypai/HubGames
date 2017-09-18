package net.arcanemc.hubgames.region;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Region {
	
	private Location loc1;
	private Location loc2;
	private Location maxVertice;
	private Location minVertice;
	private float minY;
	
	public Region(Location _loc1, Location _loc2, float _minY) {
		this.loc1 = _loc1;
		this.loc2 = _loc2;
		this.minY = _minY;
		
		minMax(this.loc1, this.loc2);
	}
	
	public Location getLoc1() {
		return this.loc1;
	}
	
	public Location getLoc2() {
		return this.loc2;
	}
	
	public Location getMinVertice() {
		return this.minVertice;
	}
	
	public Location getMaxVertice() {
		return this.maxVertice;
	}
	
	public float getMinY() {
		return this.minY;
	}
	
	public void setLoc1(Location _loc1) {
		this.loc1 = _loc1;
	}
	
	public void setLoc2(Location _loc2) {
		this.loc2 = _loc2;
	}
	
	public void setMinY(float _minY) {
		this.minY = _minY;
	}
	
	private void minMax(Location loc1, Location loc2) {
		double minX = Math.min(loc1.getX(), loc2.getX());
		double maxX = Math.max(loc1.getX(), loc2.getX());
		double minY = Math.min(loc1.getY(), loc2.getY());
		double maxY = Math.max(loc1.getY(), loc2.getY());
		double minZ = Math.min(loc1.getZ(), loc2.getZ());
		double maxZ = Math.max(loc1.getZ(), loc2.getZ());
		
		this.minVertice = new Location(loc1.getWorld(), minX, minY, minZ);
		this.maxVertice = new Location(loc1.getWorld(), maxX, maxY, maxZ);
	}
	
	public boolean contains(Location loc) {
		return (loc.getX() > this.minVertice.getX() && loc.getZ() > this.minVertice.getZ() &&
				loc.getX() < this.maxVertice.getX() + 1 && loc.getZ() < this.maxVertice.getZ() + 1 &&
				loc.getY() > this.minY);
	}
	
	public boolean contains(Location loc, int outline) {
		return (loc.getX() > this.minVertice.getX() - outline && loc.getZ() > this.minVertice.getZ() - outline &&
				loc.getX() < this.maxVertice.getX() + 1 + outline && loc.getZ() < this.maxVertice.getZ() + 1 + outline &&
				loc.getY() > this.minY);
	}
	
	public boolean containsY(Location loc) {
		return (loc.getX() > this.minVertice.getX() && loc.getZ() > this.minVertice.getZ() &&
				loc.getX() < this.maxVertice.getX() + 1 && loc.getZ() < this.maxVertice.getZ() + 1 &&
				loc.getBlockY() == this.minY);
	}
	
	public boolean containsLessY(Location loc) {
		return (loc.getX() > this.minVertice.getX() && loc.getZ() > this.minVertice.getZ() &&
				loc.getX() < this.maxVertice.getX() + 1 && loc.getZ() < this.maxVertice.getZ() + 1 &&
				loc.getY() <= this.minY);
	}
	
	public Location getCenter() {
		return new Location(minVertice.getWorld(), (this.minVertice.getX() + (((this.maxVertice.getX() - this.minVertice.getX()) / 2))),
												   (this.minVertice.getY() + (((this.maxVertice.getY() - this.minVertice.getY()) / 2))),
												   (this.minVertice.getZ() + (((this.maxVertice.getZ() - this.minVertice.getZ()) / 2))));
	}
	
	public void bootPlayer(Player player) {
		if(this.contains(player.getLocation())) {
			if(player.getLocation().getX() < this.getCenter().getX()) {
				if(player.getLocation().getBlockX() == this.minVertice.getBlockX() || player.getLocation().getBlockX() == this.minVertice.getBlockX() + 1 || player.getLocation().getBlockX() == this.minVertice.getBlockX() + 2)
				player.setVelocity(new Vector(-1, .25, 0));
			}
			if(player.getLocation().getX() > this.getCenter().getX()) {
				if(player.getLocation().getBlockX() == this.maxVertice.getBlockX() || player.getLocation().getBlockX() == this.maxVertice.getBlockX() - 1 || player.getLocation().getBlockX() == this.maxVertice.getBlockX() - 2)
				player.setVelocity(new Vector(1, .25, 0));
			}
			if(player.getLocation().getZ() < this.getCenter().getZ()) {
				if(player.getLocation().getBlockZ() == this.minVertice.getBlockZ() || player.getLocation().getBlockZ() == this.minVertice.getBlockZ() + 1 || player.getLocation().getBlockZ() == this.minVertice.getBlockZ() + 2)
				player.setVelocity(new Vector(0, .25, -1));
			}
			if(player.getLocation().getZ() > this.getCenter().getZ()) {
				if(player.getLocation().getBlockZ() == this.maxVertice.getBlockZ() || player.getLocation().getBlockZ() == this.maxVertice.getBlockZ() - 1 || player.getLocation().getBlockZ() == this.maxVertice.getBlockZ() - 2)
				player.setVelocity(new Vector(0, .25, 1));
			}
		}
		if(this.contains(player.getLocation(), -3)) {
			player.teleport(this.getCenter().getWorld().getSpawnLocation());
		}
	}
}