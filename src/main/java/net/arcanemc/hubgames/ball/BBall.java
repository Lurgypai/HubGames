package net.arcanemc.hubgames.ball;

import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import net.arcanemc.hubgames.HubMinigames;
import net.arcanemc.hubgames.NMS.CustomEntity;
import net.arcanemc.hubgames.NMS.CustomFallingBlock;
import net.arcanemc.hubgames.NMS.CustomSlime;
import net.arcanemc.hubgames.game.BGame;

public class BBall {
	private HubMinigames plugin;
	private CustomFallingBlock ball;
	private CustomSlime carrier;
	private Optional<Player> holder;
	private Optional<Player> lastHolder;
	private final int LAUNCHSPEED = 2;
	private Location thrownFrom;
	private Location current = null;
	private Location last = null;
	private int respawnTime;
	private final int RESPAWNDELAY = 20;
	private final int THREEPOINTER = 13;

	public BBall(HubMinigames plugin_, BGame game) {
		respawnTime = 0;
		this.plugin = plugin_;
		this.ball = (CustomFallingBlock) CustomEntity.spawnEntity(
				new CustomFallingBlock(((CraftWorld) game.getRegion().getCenter().getWorld()).getHandle()),
				new Location(game.getRegion().getCenter().getWorld(), game.getRegion().getCenter().getX(),
						game.getRegion().getCenter().getY() + 1, game.getRegion().getCenter().getZ()));
		this.carrier = (CustomSlime) CustomEntity.spawnEntity(
				new CustomSlime(((CraftWorld) game.getRegion().getCenter().getWorld()).getHandle()),
				new Location(game.getRegion().getCenter().getWorld(), game.getRegion().getCenter().getX(),
						game.getRegion().getCenter().getY() + 1, game.getRegion().getCenter().getZ()));
		this.carrier.setSize(1);
		Slime carrier_ = (Slime) this.carrier.getBukkitEntity();
		carrier_.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 10, false));
		this.carrier.getBukkitEntity().setPassenger(ball.getBukkitEntity());
		((Slime) this.carrier.getBukkitEntity()).setRemoveWhenFarAway(false);
		this.respawn();
	}

	public void katch(Player player) {
		player.setPassenger(this.carrier.getBukkitEntity());
		this.lastHolder = Optional.of(player);
		this.holder = Optional.of(player);
	}

	public void thruw() {
		if (this.carrier.getBukkitEntity().getVehicle() instanceof Player) {
			Player player = (Player) this.carrier.getBukkitEntity().getVehicle();
			double pitch = ((player.getLocation().getPitch() + 90) * Math.PI) / 180;
			double yaw = ((player.getLocation().getYaw() + 90) * Math.PI) / 180;

			double x = Math.sin(pitch) * Math.cos(yaw);
			double y = Math.sin(pitch) * Math.sin(yaw);
			double z = Math.cos(pitch);
			Vector vector = new Vector(x * LAUNCHSPEED, z * LAUNCHSPEED, y * LAUNCHSPEED);
			this.carrier.getBukkitEntity().leaveVehicle();
			this.carrier.getBukkitEntity().setVelocity(vector);
			this.holder = Optional.ofNullable(null);
			this.lastHolder = Optional.of(player);
			this.thrownFrom = player.getLocation();
		}
	}

	public Optional<Player> getHolder() {
		return this.holder;
	}

	public void update() {

		((Slime) this.carrier.getBukkitEntity())
				.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 10, true));

		if (respawnTime < RESPAWNDELAY) {
			respawnTime++;
			this.carrier.setLocation(this.plugin.getGameManager().getGame("basketball").getRegion().getCenter().getX(),
					this.plugin.getGameManager().getGame("basketball").getRegion().getCenter().getY() + 20,
					this.plugin.getGameManager().getGame("basketball").getRegion().getCenter().getZ(), 0, 0);
			this.carrier.getBukkitEntity().setVelocity(new Vector(0, 0, 0));
		} else {
			if (!this.plugin.getGameManager().getGame("basketball").getRegion()
					.contains(this.carrier.getBukkitEntity().getLocation(), -1)) {
				if (this.carrier.getBukkitEntity().getVehicle() != null) {
					this.carrier.getBukkitEntity().leaveVehicle();
				}
				if (this.carrier.getBukkitEntity().getLocation()
						.getX() < this.plugin.getGameManager().getGame("basketball").getRegion().getMinVertice().getX()
								+ 1) {
					this.carrier.getBukkitEntity()
							.setVelocity(new Vector(this.carrier.getBukkitEntity().getVelocity().getX() + 1,
									this.carrier.getBukkitEntity().getVelocity().getY(),
									this.carrier.getBukkitEntity().getVelocity().getZ()));
				} else if (this.carrier.getBukkitEntity().getLocation()
						.getX() > this.plugin.getGameManager().getGame("basketball").getRegion().getMaxVertice().getX()
								- 1) {
					this.carrier.getBukkitEntity()
							.setVelocity(new Vector(this.carrier.getBukkitEntity().getVelocity().getX() - 1,
									this.carrier.getBukkitEntity().getVelocity().getY(),
									this.carrier.getBukkitEntity().getVelocity().getZ()));
				} else if (this.carrier.getBukkitEntity().getLocation()
						.getZ() < this.plugin.getGameManager().getGame("basketball").getRegion().getMinVertice().getZ()
								+ 1) {
					this.carrier.getBukkitEntity()
							.setVelocity(new Vector(this.carrier.getBukkitEntity().getVelocity().getX(),
									this.carrier.getBukkitEntity().getVelocity().getY(),
									this.carrier.getBukkitEntity().getVelocity().getZ() + 1));
				} else if (this.carrier.getBukkitEntity().getLocation()
						.getZ() > this.plugin.getGameManager().getGame("basketball").getRegion().getMaxVertice().getZ()
								- 1) {
					this.carrier.getBukkitEntity()
							.setVelocity(new Vector(this.carrier.getBukkitEntity().getVelocity().getX(),
									this.carrier.getBukkitEntity().getVelocity().getY(),
									this.carrier.getBukkitEntity().getVelocity().getZ() - 1));
				}
				if (this.carrier.getBukkitEntity().getLocation().getY() < this.plugin.getGameManager()
						.getGame("basketball").getRegion().getMinY()) {
					this.respawn();
				}

			}
			if (this.current != null)
				this.last = this.current;
			this.current = this.ball.getBukkitEntity().getLocation();
			if (this.carrier.getBukkitEntity().getVehicle() == null) {
				if (this.current != null && this.last != null) {
					if (((BGame) this.plugin.getGameManager().getGame("basketball")).getHoop1().contains(this.last)
							&& ((BGame) this.plugin.getGameManager().getGame("basketball")).getHoop1()
									.getMinY() > this.current.getY()
							&& ((BGame) this.plugin.getGameManager().getGame("basketball")).getTeams()
									.getTeam("red").hasPlayer(this.lastHolder.get().getUniqueId())) {
						
						int playerDistance = Math
								.round((float) Math
										.sqrt(Math
												.pow(this.thrownFrom.getX()
														- ((BGame) this.plugin.getGameManager().getGame("basketball"))
																.getHoop1().getCenter().getX(),
														2)
												+ Math.pow(
														this.thrownFrom.getZ() - ((BGame) this.plugin.getGameManager()
																.getGame("basketball")).getHoop1().getCenter().getZ(),
														2)));
						int score = 1;
						if (playerDistance > THREEPOINTER) {
							score = 3;
						}
						
						((BGame) this.plugin.getGameManager().getGame("basketball")).getTeams().getTeam("red")
								.score(score);
						this.respawn();
					}
					if (((BGame) this.plugin.getGameManager().getGame("basketball")).getHoop2().contains(this.last)
							&& ((BGame) this.plugin.getGameManager().getGame("basketball")).getHoop2()
									.getMinY() > this.current.getY()
							&& ((BGame) this.plugin.getGameManager().getGame("basketball")).getTeams()
									.getTeam("blue").hasPlayer(this.lastHolder.get().getUniqueId())) {
						
						int playerDistance = Math
								.round((float) Math
										.sqrt(Math
												.pow(this.thrownFrom.getX()
														- ((BGame) this.plugin.getGameManager().getGame("basketball"))
																.getHoop2().getCenter().getX(),
														2)
												+ Math.pow(
														this.thrownFrom.getZ() - ((BGame) this.plugin.getGameManager()
																.getGame("basketball")).getHoop2().getCenter().getZ(),
														2)));
						int score = 1;
						if (playerDistance > THREEPOINTER) {
							score = 3;
						}
						
						((BGame) this.plugin.getGameManager().getGame("basketball")).getTeams().getTeam("blue")
								.score(score);
						this.respawn();
					}
				}
			}
		}
	}

	public Location getThrownFrom() {
		return this.thrownFrom;
	}

	private void respawn() {
		this.carrier.setLocation(this.plugin.getGameManager().getGame("basketball").getRegion().getCenter().getX(),
				this.plugin.getGameManager().getGame("basketball").getRegion().getCenter().getY() + 20,
				this.plugin.getGameManager().getGame("basketball").getRegion().getCenter().getZ(), 0, 0);
		this.carrier.getBukkitEntity().setVelocity(new Vector(0, 0, 0));
		respawnTime = 0;
	}
}