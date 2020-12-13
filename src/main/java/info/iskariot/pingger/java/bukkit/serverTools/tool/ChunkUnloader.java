package info.iskariot.pingger.java.bukkit.serverTools.tool;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import info.iskariot.pingger.java.bukkit.serverTools.Module;

/**
 * @author Pingger
 * @version 1.0.0
 * @since 0.0.5 2020-12-13
 */
public class ChunkUnloader extends Module implements Listener
{
	@Override
	public void loadConfigDefaults()
	{
		ensureConfig(
				"enabled",
				false,
				"Chunk Unloading can be important when, the Server is only running for a dynmap render, that doesn't unload chunks."
		);
		ensureConfig("logging", true, null);
		ensureConfig("timeout", 20, "The time in ticks until the chunk should be unloaded.");
	}

	/**
	 * Unload Chunk
	 *
	 * @param cle
	 *            {@link ChunkLoadEvent}
	 */
	@EventHandler
	public void onChunkLoaded(ChunkLoadEvent cle)
	{
		if (isEnabled()) {
			stp.getServer().getScheduler().scheduleSyncDelayedTask(stp, () -> cle.getChunk().unload(true), stp.getConfig().getInt("timeout"));
		}
	}

	@Override
	public void onDisable()
	{

	}

	@Override
	public void onEnable()
	{
		stp.getServer().getPluginManager().registerEvents(this, stp);
	}
}
