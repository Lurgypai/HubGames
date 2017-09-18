package net.arcanemc.hubgames.NMS;

import java.lang.reflect.Field;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityTypes;

public enum CustomEntity {
	
	CUSTOMSLIME(CustomSlime.class, 55, "CarrySlime"),
	BBALL(CustomFallingBlock.class, 70, "BBall");
	
	private Class<? extends Entity> classFile;
	private int id;
	private String name;
	
	private CustomEntity(Class<? extends Entity> classFile_, int id_, String name_) {
		classFile = classFile_;
		id = id_;
		name = name_;
	}
	
	public Class<? extends Entity> getClassFile() {
		return this.classFile;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public static void registerEntites() {
		for(CustomEntity cEntity : values()) {
			try {
				registerEntity(cEntity.classFile, cEntity.name, cEntity.id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void registerEntity(Class<? extends Entity> paramClass, String paramString, int paramInt) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field c_ = EntityTypes.class.getDeclaredField("c");
		Field d_ = EntityTypes.class.getDeclaredField("d");
		Field f_ = EntityTypes.class.getDeclaredField("f");
		Field g_ = EntityTypes.class.getDeclaredField("g");
		
		c_.setAccessible(true);
		d_.setAccessible(true);
		f_.setAccessible(true);
		g_.setAccessible(true);

		Map<String, Class<? extends Entity>> c = (Map<String, Class<? extends Entity>>) c_.get(null);
		Map<Class<? extends Entity>, String> d = (Map<Class<? extends Entity>, String>) d_.get(null);
		Map<Class<? extends Entity>, Integer> f = (Map<Class<? extends Entity>, Integer>) f_.get(null);
		Map<String, Integer> g = (Map<String, Integer>) g_.get(null);
		
	      c.put(paramString, paramClass);
	      d.put(paramClass, paramString);
	      f.put(paramClass, Integer.valueOf(paramInt));
	      g.put(paramString, Integer.valueOf(paramInt));
	}
	
	public static Entity spawnEntity(Entity entity, Location location) {
		entity.setPosition(location.getX(), location.getY(), location.getZ());
		((CraftWorld) location.getWorld()).getHandle().addEntity(entity, SpawnReason.CUSTOM);
		return entity;
	}
	
	public static Object getPrivateField(String fieldName, Class clazz, Object object)
    {
        Field field;
        Object o = null;

        try
        {
            field = clazz.getDeclaredField(fieldName);

            field.setAccessible(true);

            o = field.get(object);
        }
        catch(NoSuchFieldException e)
        {
            e.printStackTrace();
        }
        catch(IllegalAccessException e)
        {
            e.printStackTrace();
        }

        return o;
    }
}
