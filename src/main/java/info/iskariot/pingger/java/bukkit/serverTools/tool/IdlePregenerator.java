package info.iskariot.pingger.java.bukkit.serverTools.tool;

import java.awt.Point;
import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.World;

import info.iskariot.pingger.java.bukkit.serverTools.Module;
import info.iskariot.pingger.java.bukkit.serverTools.monitor.TPSMonitor;

/**
 * @author Pingger
 * @version 0.1.0
 * @deprecated since 2020-11-24 (replaced by {@link FastPregenerator})
 */
@Deprecated
public class IdlePregenerator extends Module implements Runnable
{

	private Point	current		= new Point();

	private int		direction	= 0;
	private int		lastFailure	= 0;

	@Override
	public void loadConfigDefaults()
	{
		ensureConfig("enabled", false, "Do not use! Deprecated! Use FastPregenerator instead!");
		ensureConfig("logging", true, null);
		ensureConfig("tpsThreshold", 19.5, "the tps limit, below which chunks are no longer pregenerated. Depends on TPSMonitor!");
		ensureConfig("tpt", 10, "the Time Per Tick (in ms) to work on pregenerating chunks. Minimum generation of 1 Chunk per tick");
		ensureConfig("maxPlayers", 4, "the maximum count of logged in players to autogenerate");
		//plg.ensureConfig(c, cfgAccelerateLabel, false, "Accelerate if the server is empty? Tries to fully fill a tick with pregenerating");
		ensureConfig("blockLimit", 4096, "The maximum Dimension in blocks (from 0,0) to pregenerate. Might be slightly more");
		ensureConfig("startWithRadius", 0, "Skips all generating within this radius from (0,0) in Blocks");

		String[] worlds = stp.getServer().getWorlds().stream().map(w -> w.getName()).toArray(String[]::new);
		ensureConfig("worlds", worlds, "The Worlds to pregenerate");
	}

	@Override
	public void onDisable()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onEnable()
	{
		if (!getConfigSection(stp.getConfig(), TPSMonitor.class.getCanonicalName()).getBoolean("enabled", false)) {
			stp.getLogger().severe("Idle Pregenerator can't work without the TPSMonitor!");
			throw new IllegalStateException("Idle Pregenerator can't work without the TPSMonitor!");
		}
		stp.getServer().getScheduler().runTaskLater(stp, () -> run(), 1);
		if (current.x == 0) {
			current.x = getConfig().getInt("startWithRadius") / 16;
			current.y = getConfig().getInt("startWithRadius") / 16;
		}
		for (String worldName : getConfig().getStringList("worlds")) {
			World w = stp.getServer().getWorld(worldName);
			if (w != null) {
				log("[IdlePregenerator] Enabled for: " + w.getName());
			}
			else {
				log("[IdlePregenerator] FAILED for: " + worldName);
			}
		}
	}

	@Override
	public void run()
	{
		long start = System.nanoTime();
		if (current.x * 16l >= getConfig().getInt("blockLimit")
				|| current.y * 16l >= getConfig().getInt("blockLimit"))
		{
			if (lastFailure != 3) {
				log("[IdlePregenerator] Maximum area generated");
			}
			lastFailure = 3;
		}
		else if (stp.getServer().getOnlinePlayers().size() > getConfig().getInt("maxPlayers")) {
			if (lastFailure != 1) {
				log("[IdlePregenerator] Too many Players");
			}
			lastFailure = 1;
		}
		else if (TPSMonitor.tps < getConfig().getDouble("tpsThreshold")) {
			if (lastFailure != 2) {
				log("[IdlePregenerator] TPS too low (Current: " + TPSMonitor.tps + ")");
			}
			lastFailure = 2;
		}
		else {
			if (lastFailure != 0) {
				log("[IdlePregenerator] Continue");
			}
			lastFailure = 0;
			long max = getConfig().getInt("tpt");
			ArrayList<World> worlds = new ArrayList<>();
			for (String worldName : getConfig().getStringList("worlds")) {
				World w = stp.getServer().getWorld(worldName);
				if (w != null) {
					worlds.add(w);
				}
			}

			do {
				switch (direction)
				{
					case 0: /* Top Row */
						current.x += 1;
						if (current.x > current.y) {
							direction = 1;
						}
						break;

					case 1: /* Right Column */
						current.y -= 1;
						if (current.y == -current.x) {
							direction = 2;
						}
						break;

					case 2: /* Bottom Row */
						current.x -= 1;
						if (current.x == current.y) {
							direction = 3;
						}
						break;

					case 3: /* Left Column */
						current.y += 1;
						if (current.y == -current.x) {
							direction = 0;
							stp.reloadConfig();
							getConfig().set("startWithRadius", (current.y - 1) * 16);
							stp.saveConfig();
							log("[IdlePregenerator] Radius now: " + (current.y + 1) * 16);
						}
						break;

					default: /* WTF?! */
						direction = 0;
				}
				for (World world : worlds) {
					if (world.isChunkGenerated(current.x, current.y)) {
						continue;
					}
					Chunk chunk = world.getChunkAt(current.x, current.y);
					if (!chunk.load(true)) {
						log("Failed to load chunk: " + chunk.getWorld() + ":" + chunk.getX() + ":" + chunk.getZ());
					}
					chunk.unload(true);
				}
			}
			while (max > (System.nanoTime() - start) / 1e6);
			if (stp.isEnabled()) {
				stp.getServer().getScheduler().runTaskLater(stp, () -> run(), 1);
			}
			return;
		}
		if (stp.isEnabled()) {
			stp.getServer().getScheduler().runTaskLater(stp, () -> run(), 100);
		}
	}
}
