package net.arcanemc.hubgames.game;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;

import net.arcanemc.hubgames.HubMinigames;
import net.arcanemc.hubgames.game.team.Team;
import net.arcanemc.hubgames.region.Region;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.NBTTagCompound;

public class KotHGame extends AbstractGame {

	private Team lastTeamIn = null;
	private Team currentTeamIn = null;
	private Team currentCapturedTeam;
	private Player currentPlayerIn = null;
	private RegionState state = RegionState.EMPTY;
	private final int WAIT_TIME = 20;
	private int delay = 0;
	private ArrayList<Wool> wool = new ArrayList<Wool>();
	private ArrayList<BlockState> blocks = new ArrayList<BlockState>();
	
	private enum RegionState {
		CAPTURED,
		EMPTY,
		CAPTURING;
	}
	
	public KotHGame (HubMinigames plugin_, Region region_) {
		this.region = region_;
		for (int x = 0; x != 3; x++) {
			for (int z = 0; z != 3; z++) {
				Location current = new Location(this.region.getCenter().getWorld(), this.region.getMinVertice().getBlockX() + x + 1,
						this.region.getCenter().getBlockY() - 1,
						this.region.getMinVertice().getBlockZ() + z + 1);
				if (current.getBlock().getState().getData() instanceof Wool) {
					BlockState state = current.getBlock().getState();
					blocks.add(state);
					Wool woolBlock = (Wool) state.getData();
					wool.add(woolBlock);
				}
			}
		}
	}
	
	@Override
	public void update() {
		for (Team team : this.teams.getTeams()) {
			for (UUID uuid : team.getPlayers()) {
				float distance = Math.round((float)Math.sqrt(Math.pow(Bukkit.getPlayer(uuid).getLocation().getX() - this.region.getCenter().getX(), 2) + Math.pow(Bukkit.getPlayer(uuid).getLocation().getZ() - this.region.getCenter().getZ(), 2)));
				if (distance > Team.LEAVE_DISTANCE) {
					if (CraftItemStack.asNMSCopy(Bukkit.getServer().getPlayer(uuid).getInventory().getItem(1)).getTag().hasKey("kothSword")) {
						Bukkit.getServer().getPlayer(uuid).getInventory().setItem(1, new ItemStack(Material.AIR));
					}
				}
			}
		}
		
		if (delay == WAIT_TIME - 1) {
			int numberIn = 0;
			for (Team team : this.teams.getTeams()) {
				boolean in = false;
				for (UUID uuid : team.getPlayers()) {
					if (this.region.contains(Bukkit.getPlayer(uuid).getLocation())) {
						in = true;
					}
				}
				if (in) {
					numberIn++;
				}
			}
			if (numberIn == 1) {
				boolean isPlayerIn = false;
				for (Team team: this.teams.getTeams()) {
					for (UUID uuid : team.getPlayers()) {
						if (this.region.contains(Bukkit.getPlayer(uuid).getLocation())) {
							currentTeamIn = team;
							currentPlayerIn = Bukkit.getPlayer(uuid);
							isPlayerIn = true;
						}
					}
				}
				if (!isPlayerIn) {
					currentTeamIn = null;
				}
			} else {
				currentTeamIn = null;
				currentPlayerIn = null;
			}
			
			//if someone is capturing
			if (currentPlayerIn != null && currentTeamIn != null ) {
				switch(state) {
				//if it was already captured make the last guys who got it the lastTeamIn,
				//and start capturing by others, but not if the same person is inside
				case CAPTURED:
					if (currentTeamIn.equals(currentCapturedTeam)) {
						break;
					} else if (!currentTeamIn.equals(currentCapturedTeam)) {
						lastTeamIn = currentCapturedTeam;
						currentCapturedTeam = null;
						lastTeamIn.setPoints(4);
						state = RegionState.CAPTURING;
					}
					break;
				//if it is being captured and there are still other guys in it, then decay them if they aren't recapping
				//if there is no lastTeamIn, capture by current team
				case CAPTURING:
					if (lastTeamIn != null) {
						if (lastTeamIn.equals(currentTeamIn)) {
							currentTeamIn.setPoints(currentTeamIn.getPoints() + 1);
							if (currentTeamIn.getPoints() > 5) {
								currentTeamIn.setPoints(5);
							}
						} else {
							lastTeamIn.setPoints(lastTeamIn.getPoints() - 1);
							if (lastTeamIn.getPoints() <= 0) {
								lastTeamIn.setPoints(0);
								lastTeamIn = null;
								state = RegionState.EMPTY;
								for (int i = 0; i < wool.size(); i++) {
									wool.get(i).setColor(DyeColor.WHITE);
									blocks.get(i).update();
									setFlagHeight(0, DyeColor.WHITE);
								}
							}
						}
					}
					break;
				//if it is down to normal, set the last guys in to the current guys in, and start them capping
				case EMPTY:
					lastTeamIn = currentTeamIn;
					currentTeamIn.setPoints(currentTeamIn.getPoints() + 1);
					state = RegionState.CAPTURING;
					break;
				}
			}
			
			
			//if anyone got it, capture it
			for (Team team : teams.getTeams()) {
				if (team.getPoints() == 5) {
					currentCapturedTeam = team;
					state = RegionState.CAPTURED;
				} else if (team.getPoints() < 5 && state == RegionState.CAPTURED) {
					state = RegionState.CAPTURING;
				}
			}
			
			//if no one is inside, and someone was in it last, decay it
			if (currentTeamIn == null && currentPlayerIn == null && lastTeamIn != null && numberIn == 0) {
				lastTeamIn.setPoints(lastTeamIn.getPoints() - 1);
				if (lastTeamIn.getPoints() <= 0) {
					lastTeamIn.setPoints(0);
					lastTeamIn = null;
					state = RegionState.EMPTY;
					for (int i = 0; i < wool.size(); i++) {
						wool.get(i).setColor(DyeColor.WHITE);
						blocks.get(i).update();
						setFlagHeight(0, DyeColor.WHITE);
					}
				}
			}
			
			//color it
			for (Team team : teams.getTeams()) {
				if (team.getPoints() > 0) {
					int points = team.getPoints();
					switch(points) {
					case 1:
						setWoolColor(0, team.getDyeColor());
						setWoolColor(1, DyeColor.WHITE);
						setWoolColor(2, DyeColor.WHITE);
						setWoolColor(3, DyeColor.WHITE);
						setWoolColor(4, DyeColor.WHITE);
						setWoolColor(5, DyeColor.WHITE);
						setWoolColor(6, DyeColor.WHITE);
						setWoolColor(7, DyeColor.WHITE);
						setWoolColor(8, DyeColor.WHITE);
						break;
					case 2:
						setWoolColor(0, team.getDyeColor());
						setWoolColor(1, DyeColor.WHITE);
						setWoolColor(2, DyeColor.WHITE);
						setWoolColor(3, DyeColor.WHITE);
						setWoolColor(4, team.getDyeColor());
						setWoolColor(5, DyeColor.WHITE);
						setWoolColor(6, DyeColor.WHITE);
						setWoolColor(7, team.getDyeColor());
						setWoolColor(8, DyeColor.WHITE);
						break;
					case 3:
						setWoolColor(0, team.getDyeColor());
						setWoolColor(1, DyeColor.WHITE);
						setWoolColor(2, team.getDyeColor());
						setWoolColor(3, DyeColor.WHITE);
						setWoolColor(4, team.getDyeColor());
						setWoolColor(5, team.getDyeColor());
						setWoolColor(6, DyeColor.WHITE);
						setWoolColor(7, team.getDyeColor());
						setWoolColor(8, DyeColor.WHITE);
						break;
					case 4:
						setWoolColor(0, team.getDyeColor());
						setWoolColor(1, DyeColor.WHITE);
						setWoolColor(2, team.getDyeColor());
						setWoolColor(3, team.getDyeColor());
						setWoolColor(4, team.getDyeColor());
						setWoolColor(5, team.getDyeColor());
						setWoolColor(6, DyeColor.WHITE);
						setWoolColor(7, team.getDyeColor());
						setWoolColor(8, team.getDyeColor());
						break;
					case 5:
						setWoolColor(0, team.getDyeColor());
						setWoolColor(1, team.getDyeColor());
						setWoolColor(2, team.getDyeColor());
						setWoolColor(3, team.getDyeColor());
						setWoolColor(4, team.getDyeColor());
						setWoolColor(5, team.getDyeColor());
						setWoolColor(6, team.getDyeColor());
						setWoolColor(7, team.getDyeColor());
						setWoolColor(8, team.getDyeColor());
						break;
					}
					setFlagHeight(points, team.getDyeColor());
				}
			}
			
			delay = 0;
		} else {
			delay++;
		}
	}

	private void setWoolColor(int woolNumber, DyeColor color) {
		wool.get(woolNumber).setColor(color);
		blocks.get(woolNumber).update();
	}
	
	private void setFlagHeight(int height, DyeColor color) {
		Location base = this.region.getMinVertice().clone();
		ArrayList<Block> pole = new ArrayList<Block>();
		for (int i = 0; i != 6; i++) {
			pole.add(new Location(base.getWorld(), base.getBlockX(), base.getBlockY() + i + 2, base.getBlockZ()).getBlock());
		}
		
		Location flagLocation = pole.get(4).getLocation().clone();
		flagLocation.setZ(flagLocation.getBlockZ() - 1);
		Location flagLocation2 = flagLocation.clone();
		Block flagBlockOne = flagLocation.getBlock();
		flagLocation2.setZ(flagLocation.getBlockZ() - 1);
		Block flagBlockTwo = flagLocation2.getBlock();
		
		switch(height) {
		case 0:
			pole.get(0).setType(Material.STEP);
			pole.get(0).setData((byte)3);
			pole.get(1).setType(Material.AIR);
			pole.get(2).setType(Material.AIR);
			pole.get(3).setType(Material.AIR);
			pole.get(4).setType(Material.AIR);
			pole.get(5).setType(Material.AIR);
			break;
		case 1:
			pole.get(0).setType(Material.DARK_OAK_FENCE);
			pole.get(1).setType(Material.STEP);
			pole.get(1).setData((byte)3);
			pole.get(2).setType(Material.AIR);
			pole.get(3).setType(Material.AIR);
			pole.get(4).setType(Material.AIR);
			pole.get(5).setType(Material.AIR);
			break;
		case 2:
			pole.get(0).setType(Material.DARK_OAK_FENCE);
			pole.get(1).setType(Material.DARK_OAK_FENCE);
			pole.get(2).setType(Material.STEP);
			pole.get(2).setData((byte)3);
			pole.get(3).setType(Material.AIR);
			pole.get(4).setType(Material.AIR);
			pole.get(5).setType(Material.AIR);
			break;
		case 3:
			pole.get(0).setType(Material.DARK_OAK_FENCE);
			pole.get(1).setType(Material.DARK_OAK_FENCE);
			pole.get(2).setType(Material.DARK_OAK_FENCE);
			pole.get(3).setType(Material.STEP);
			pole.get(3).setData((byte)3);
			pole.get(4).setType(Material.AIR);
			pole.get(5).setType(Material.AIR);
			break;
		case 4:
			pole.get(0).setType(Material.DARK_OAK_FENCE);
			pole.get(1).setType(Material.DARK_OAK_FENCE);
			pole.get(2).setType(Material.DARK_OAK_FENCE);
			pole.get(3).setType(Material.DARK_OAK_FENCE);
			pole.get(4).setType(Material.STEP);
			pole.get(4).setData((byte)3);
			pole.get(5).setType(Material.AIR);
			
			flagBlockOne.setType(Material.AIR);
			flagBlockTwo.setType(Material.AIR);
			break;
		case 5:
			pole.get(0).setType(Material.DARK_OAK_FENCE);
			pole.get(1).setType(Material.DARK_OAK_FENCE);
			pole.get(2).setType(Material.DARK_OAK_FENCE);
			pole.get(3).setType(Material.DARK_OAK_FENCE);
			pole.get(4).setType(Material.DARK_OAK_FENCE);
			pole.get(5).setType(Material.STEP);
			pole.get(5).setData((byte)3);
			
			makeWool(flagBlockOne, color);
			makeWool(flagBlockTwo, color);
			
			break;
		}
	}
	
	private void makeWool(Block block, DyeColor color) {
		block.setType(Material.WOOL);
		BlockState state = block.getState();
		Wool wool_ = (Wool)state.getData();
		wool_.setColor(color);
		state.update();
	}
	
	@Override
	public void startGame() {
		
	}

	@Override
	public void stopGame() {
		for(Team team : teams.getTeams()) {
			team.clearTeam();
			team.setPoints(0);
		}
	}
	@Override
	public void joinGame(UUID uuid_, String name_) {
		Team team = teams.getTeam(name_);
		if (team != null) {
			team.addPlayer(uuid_);
			net.minecraft.server.v1_8_R3.ItemStack sword = CraftItemStack.asNMSCopy(new ItemStack(Material.WOOD_SWORD));
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setBoolean("Unbreakable", true);
			nbt.setBoolean("kothSword", true);
			sword.setTag(nbt);
			
			Bukkit.getPlayer(uuid_).getInventory().setItem(1, CraftItemStack.asBukkitCopy(sword));
		} else {
			Bukkit.getPlayer(uuid_).sendMessage(HubMinigames.formatTextForHub("Couldn't find that " + ChatColor.GOLD + "Team" + ChatColor.RESET + "!"));
		}
	}
}
