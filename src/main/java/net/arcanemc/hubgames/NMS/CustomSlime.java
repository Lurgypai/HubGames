package net.arcanemc.hubgames.NMS;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.server.v1_8_R3.EntitySlime;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;
import net.minecraft.server.v1_8_R3.World;

public class CustomSlime extends EntitySlime {

	Field bk_;
	boolean bk;
	
	public CustomSlime(World world) {
		super(world);
		
		try {
		bk_ = EntitySlime.class.getDeclaredField("bk");
		bk_.setAccessible(true);
		bk = bk_.getBoolean(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        List goalB = (List)CustomEntity.getPrivateField("b", PathfinderGoalSelector.class, goalSelector); goalB.clear();
        List goalC = (List)CustomEntity.getPrivateField("c", PathfinderGoalSelector.class, goalSelector); goalC.clear();
        List targetB = (List)CustomEntity.getPrivateField("b", PathfinderGoalSelector.class, targetSelector); targetB.clear();
        List targetC = (List)CustomEntity.getPrivateField("c", PathfinderGoalSelector.class, targetSelector); targetC.clear();
	}
}
