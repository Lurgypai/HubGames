package net.arcanemc.hubgames.NMS;

import org.bukkit.Bukkit;

import net.minecraft.server.v1_8_R3.EntityFallingBlock;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.World;

public class CustomFallingBlock extends EntityFallingBlock {
    
	public CustomFallingBlock(World world) {
		super(world);
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("Block", "stained_hardened_clay");
		nbt.setInt("Data", 1);
		this.a(nbt);
	}
	@Override
	public void t_()  
	  {
	      this.lastX = this.locX;
	      this.lastY = this.locY;
	      this.lastZ = this.locZ;
	      this.motY -= 0.03999999910593033D;
	      move(this.motX, this.motY, this.motZ);
	      this.motX *= 0.9800000190734863D;
	      this.motY *= 0.9800000190734863D;
	      this.motZ *= 0.9800000190734863D;
	        if (this.onGround)
	        {
	          this.motX = 0;
	          this.motZ = 0;
	          this.motY = 0;
	        }
	    }
}
