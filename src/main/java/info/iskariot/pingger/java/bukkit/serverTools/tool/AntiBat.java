package info.iskariot.pingger.java.bukkit.serverTools.tool;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import info.iskariot.pingger.java.bukkit.serverTools.Module;
import info.iskariot.pingger.java.bukkit.serverTools.util.Formatting;

public class AntiBat extends Module implements Listener
{

	@Override
	public void onDisable()
	{

	}

	@Override
	public void onEnable()
	{
		stp.getServer().getPluginManager().registerEvents(this, stp);
	}

	@EventHandler
	public void onEntitySpawned(EntitySpawnEvent ese)
	{
		if (isEnabled()) {
			if (EntityType.BAT.equals(ese.getEntityType())) {
				debug(() -> "Cancelled spawning of Bat at " + Formatting.formatLocation(ese.getLocation()));
				ese.setCancelled(true);
			}
		}
	}
}
