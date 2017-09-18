package net.arcanemc.hubgames.game.team;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import net.arcanemc.hubgames.HubMinigames;
import net.arcanemc.hubgames.region.Region;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;

public class Team {
		
		private String displayName;
		private String name;
		private int rgb[] = new int[3];
		private DyeColor dyeColor;
		private ArrayList<UUID> uuids = new ArrayList<UUID>();
		private int points;
		public final static int LEAVE_DISTANCE = 30;
		
		protected Team(String name_, String displayName_, int r, int g, int b, DyeColor color_) {
			displayName = displayName_;
			name = name_;
			rgb[0] = r;
			rgb[1] = g;
			rgb[2] = b;
			dyeColor = color_;
		}
		
		public String getDisplayName() {
			return ChatColor.translateAlternateColorCodes('&',this.displayName);
		}
		
		public String getName() {
			return this.name;
		}
		
		public Color getColor() {
			return Color.fromRGB(rgb[0], rgb[1], rgb[2]);
		}
		
		public DyeColor getDyeColor() {
			return this.dyeColor;
		}
		
		public void addPlayer(UUID uuid) {
			if (!this.uuids.contains(uuid)) {
				Bukkit.getPlayer(uuid).sendMessage(HubMinigames.formatTextForHub("Joined " + this.getDisplayName() + " Team!"));
				this.uuids.add(uuid);
				this.addTeamArmor(Bukkit.getPlayer(uuid));
			}
		}
		
		public void removePlayer(UUID uuid_) {
			this.uuids.remove(uuid_);
			this.clearTeamArmor(Bukkit.getPlayer(uuid_));
		}
		
		public void clearTeam() {
			for (UUID uuid : this.uuids) {
				this.clearTeamArmor(Bukkit.getPlayer(uuid));
			}
			this.uuids.clear();
		}
		
		public boolean hasPlayer(UUID uuid_) {
				return uuids.contains(uuid_);
		}
		
		public Player getPlayer(UUID uuid_) {
			for (UUID uuid : this.uuids) {
				if (uuid.equals(uuid_))
					return Bukkit.getServer().getPlayer(uuid);
			}
			return null;
		}
		
		public ArrayList<UUID> getPlayers() {
			return this.uuids;
		}
		
		public int getPoints() {
			return this.points;
		}
		
		public void setPoints(int points_) {
			this.points = points_;
		}
		
		public void score(int i) {
			this.points += i;
				for(UUID uuid : uuids) {
			IChatBaseComponent chatTitle = ChatSerializer.a(
					"{\"text\": \"" + this.getDisplayName() + ": " + this.points + "\", \"color\": \"gray\"}");
			PacketPlayOutTitle title = new PacketPlayOutTitle(
					EnumTitleAction.TITLE, chatTitle, 5, 20, 5);
			CraftPlayer craftPlayer = (CraftPlayer) Bukkit.getServer().getPlayer(uuid);
			craftPlayer.getHandle().playerConnection.sendPacket(title);
			}
		}
		
		public void update(Region arena) {
					ArrayList<UUID> toRemove = new ArrayList<UUID>();
					for (UUID uuid : uuids) {
						int playerDistance = Math.round((float)Math.sqrt(Math.pow(Bukkit.getPlayer(uuid).getLocation().getX() - arena.getCenter().getX(), 2) + Math.pow(Bukkit.getPlayer(uuid).getLocation().getZ() - arena.getCenter().getZ(), 2)));
						if (playerDistance > Team.LEAVE_DISTANCE) {
							toRemove.add(uuid);
							Bukkit.getPlayer(uuid).sendMessage(HubMinigames.formatTextForHub("You were removed from " + this.getDisplayName() + " Team because you left the field!"));
							clearTeamArmor(Bukkit.getPlayer(uuid));
						}
					}
					uuids.removeAll(toRemove);
				}
		

	
	private void addTeamArmor(Player player_) {
		
        ItemStack lchest = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
        ItemStack lhelm = new ItemStack(Material.LEATHER_HELMET, 1);
        ItemStack llegs = new ItemStack(Material.LEATHER_LEGGINGS, 1);
        ItemStack lshoes = new ItemStack(Material.LEATHER_BOOTS, 1);
        
        LeatherArmorMeta larmor_ = (LeatherArmorMeta)lchest.getItemMeta();
        
        larmor_.setColor(this.getColor());
        
        lchest.setItemMeta(larmor_);
        lhelm.setItemMeta(larmor_);
        llegs.setItemMeta(larmor_);
        lshoes.setItemMeta(larmor_);
        
        player_.getEquipment().setChestplate(lchest);
        player_.getEquipment().setHelmet(lhelm);
        player_.getEquipment().setLeggings(llegs);
        player_.getEquipment().setBoots(lshoes);
        
	}
	
	private void clearTeamArmor(Player player_) {
        player_.getEquipment().setChestplate(new ItemStack(Material.AIR));
        player_.getEquipment().setHelmet(new ItemStack(Material.AIR));
        player_.getEquipment().setLeggings(new ItemStack(Material.AIR));
        player_.getEquipment().setBoots(new ItemStack(Material.AIR));
	}
}
