package net.arcanemc.hubgames.packet;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

import net.arcanemc.hubgames.HubMinigames;
import net.arcanemc.hubgames.NMS.CustomSlime;

public class PacketHandler {
	
	private HubMinigames basketball;
	
	public PacketHandler(HubMinigames basketball_) {
		this.basketball = basketball_;
	}
	
	public void startListening() {
		ProtocolLibrary.getProtocolManager().addPacketListener(
			    new PacketAdapter(basketball, ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_METADATA) {
			    	
			    	@Override
			        public void onPacketSending(PacketEvent event) {
			            PacketContainer packet = event.getPacket();
			            Entity entity = packet.getEntityModifier(event.getPlayer().getWorld()).read(0);
			 
			            if (entity != null && ((CraftEntity)entity).getHandle() instanceof CustomSlime) {
			                for (WrappedWatchableObject obj : packet.getWatchableCollectionModifier().read(0)) {
			                    if (obj.getIndex() == 7) {
			                        obj.setValue((int) 0);
			                    }
			                }
			            }
			        }
			    }
			);
	}
}
