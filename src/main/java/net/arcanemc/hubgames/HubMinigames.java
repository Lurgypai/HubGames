package net.arcanemc.hubgames;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import net.arcanemc.core.api.plugins.Plugin;
import net.arcanemc.hubgames.NMS.CustomEntity;
import net.arcanemc.hubgames.command.TeamCommand;
import net.arcanemc.hubgames.game.BGame;
import net.arcanemc.hubgames.game.GameManager;
import net.arcanemc.hubgames.game.KotHGame;
import net.arcanemc.hubgames.listener.BallListener;
import net.arcanemc.hubgames.listener.KotHListener;
import net.arcanemc.hubgames.listener.PlayerListener;
import net.arcanemc.hubgames.packet.PacketHandler;
import net.arcanemc.hubgames.region.Region;
import net.md_5.bungee.api.ChatColor;

public class HubMinigames extends Plugin {
	private static HubMinigames instance;
	private GameManager manager;
	private BGame bgame;
	private KotHGame kothgame;
	private PacketHandler packetHandler;
	
	public final String HUBWORLD = "world";
	
	public HubMinigames() {
		instance = this;
	}
	
	public static HubMinigames getInstance() {
		return instance;
	}
	
	public GameManager getGameManager() {
		return manager;
	}
	
	@Override
	public void onStart() {		CustomEntity.registerEntites();
	prepareConfig();
	
	bgame = new BGame(this, loadRegion("basketball.region"), loadRegion("basketball.hoop1"), loadRegion("basketball.hoop2"));
	kothgame = new KotHGame(this, loadRegion("koth.region"));

	manager = new GameManager(this);
	manager.addGame("basketball", bgame);
	manager.addGame("koth", kothgame);
	
	packetHandler = new PacketHandler(this);
	packetHandler.startListening();
	
	Bukkit.getServer().getPluginManager().registerEvents(new BallListener(), this);
	Bukkit.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
	Bukkit.getServer().getPluginManager().registerEvents(new KotHListener(this), this);
	register(new TeamCommand());
	
	manager.startGames();	
	}

	@Override
	public void onStop() {
		manager.stopGames();
		
	}
	
	private void prepareConfig() {
		this.saveDefaultConfig();
	}
	
	private Region loadRegion(String path) {
		FileConfiguration config = this.getConfig();
		Location loc1 = new Location(this.getServer().getWorld(config.getString(path + ".world")), config.getDouble(path + ".loc1.x"),
				(float)config.getDouble(path + ".loc1.y"), (float)config.getDouble(path + ".loc1.z"));
		Location loc2 = new Location(this.getServer().getWorld(config.getString(path + ".world")), (float)config.getDouble(path + ".loc2.x"),
				(float)config.getDouble(path + ".loc2.y"), (float)config.getDouble(path + ".loc2.z"));
		float minY = (float)config.getDouble(path + ".y");
		Region region = new Region(loc1, loc2, minY);
		return region;
	}
	
	public static String formatTextForHub(String toFormat) {
		toFormat = ChatColor.DARK_GRAY + "[" +  ChatColor.BLUE + "Hub" + ChatColor.DARK_GRAY + "] > " + ChatColor.RESET + toFormat;
		return toFormat;
	}
}

/*TODO:
 * Make sure that game and region are prepared
 * Game
 * -listeners
 * --clicking to pick up/throw
 * --click to shove
 * --click to steal
 * -points
 * --red/blue teams
 * --points to be stored until active players reaches 0/server restart
 * -player registration
 * --upon entering region assign to team with lowest player count, biased towards blue
 * --full leather
 * -regions
 * --permanently defined areas with easily checkable bounds
 * --include x y z but ways to check if its contained in exactly or above
 * -ball
 * --permanent falling sand block. possible necessity to implement custom physics
 * --possible necessity to keep track of positions
 * -hoop
 * --permanent regions for each hoop
 * --updater to check if a ball is between max/minimum ys and within regions. If so add points.
 */
