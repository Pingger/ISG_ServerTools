package info.iskariot.pingger.java.bukkit.serverTools.tool;

import static info.iskariot.pingger.java.bukkit.serverTools.util.Formatting.*;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import info.iskariot.pingger.java.bukkit.serverTools.CommandInterface;
import info.iskariot.pingger.java.bukkit.serverTools.Module;
import info.iskariot.pingger.java.bukkit.serverTools.util.Formatting;

/**
 * @author Pingger
 * @version 1.1.0
 * @since 2020-11-24
 */
public class FastPregenerator extends Module implements CommandInterface
{
	private LinkedList<Long>			chunkGenerationDuration			= new LinkedList<>();
	private int							chunks							= 0;
	private int							currentAnvilChunks				= 0;
	private int							currentAnvilDuration			= 0;
	private long						currentAnvilStart				= 0;
	private int							currentAnvilTicks				= 0;
	private FastPregenerationJob		currentJob						= null;
	private int							currentJobId					= 0;
	private Method						dynmap_triggerRenderOfVolume	= null;
	private Plugin						dynmapPlugin					= null;
	private List<FastPregenerationJob>	jobs							= new ArrayList<>();
	private long						lastActualDuration				= 0;
	private BukkitTask					previousTask;
	private long						targetDuration					= 50;
	private long						timebank						= 0;
	private long						timeDiff						= 0;
	private long						timeEnd							= 0;
	private long						timeStart						= 0;
	private World						w								= null;

	@Override
	public boolean getHelp(CommandSender sender, Command command, String label, String[] args)
	{
		return false;
	}

	@Override
	public String[] getLabel()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPermissionNode(CommandSender sender, Command command, String label, String[] args)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUsage()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void loadConfigDefaults()
	{
		super.loadConfigDefaults();
		ensureConfig("fine", true, "This module supports 'fine' Logging, which provides a bit more information in logging.");
		ensureConfig("debug", false, null);
		FastPregenerationJob exampleJob = new FastPregenerationJob("exampleJob");
		exampleJob.save();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onDisable()
	{
		previousTask.cancel();
	}

	@Override
	public void onEnable()
	{
		previousTask = stp.getServer().getScheduler().runTaskLater(stp, () -> run(), 1);
		for (World world : stp.getServer().getWorlds()) {
			super.log("Found World: " + formatWorld(world));
		}
		ConfigurationSection jobsCfg = getConfig("jobs");
		for (String label : jobsCfg.getKeys(false)) {
			super.log("Loading Pregeneration Job: " + label);
			jobs.add(new FastPregenerationJob(label));
		}
		super.log(
				"Loaded " + jobs.size() + " Pregeneration Jobs. (" + jobs.stream().filter(j -> j.enabled).count() + " enabled, "
						+ jobs.stream().filter(j -> j.enabled && j.blocking).count() + " also blocking)"
		);
		dynmapPlugin = stp.getServer().getPluginManager().getPlugin("dynmap");
		if (dynmapPlugin != null) {
			try {
				dynmap_triggerRenderOfVolume = dynmapPlugin
						.getClass()
						.getMethod("triggerRenderOfVolume", String.class, int.class, int.class, int.class, int.class, int.class, int.class);
			}
			catch (NoSuchMethodException e) {
				log("Dynmap detected, but API endpoint is missing!");
				dynmapPlugin = null;
			}
			catch (SecurityException e) {
				log("Dynmap detected, but API endpoint is protected by SecurityManager!", e);
				dynmapPlugin = null;
			}
			log("Dynmap detected!");
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 *
	 */
	public void run()
	{
		try {
			timeStart = System.currentTimeMillis();
			if (jobs.parallelStream().filter(job -> job.enabled && !job.isFinished()).count() == 0) { return; }
			// Compute TargetDuration
			timeDiff = timeStart - timeEnd;
			if (timeDiff <= 0) {
				// log("TimeDiff too small: " + timeDiff + "ms");
				return;
			}
			targetDuration = 45 - timeDiff - Math.max(0, targetDuration - lastActualDuration);
			// if timebank wasn't enough last tick, it won't be now with no additional time
			if (targetDuration <= 0) { return; }

			timebank += targetDuration;
			debug(() -> "Timebank is now " + formatMilliseconds(timebank));

			/*
			 * If no Job is loaded and the finished signal (currentJobId=-1) was not
			 * triggered:
			 * -> find the next job, that is enabled and is not finished yet
			 */
			if (currentJob == null) {
				w = null;
				boolean finishedSingal = currentJobId == -1;
				if (currentJobId >= jobs.size()) {
					currentJobId = 0;
				}
				int startCheck = currentJobId == -1 ? 0 : currentJobId;
				currentJobId = startCheck;
				if (!jobs.isEmpty()) {
					do {
						currentJob = jobs.get(currentJobId);
						currentJob.reload();
						// Check if enabled, and work to do
						if (currentJob.enabled
								&& currentJob.hasNextChunk())
						{
							break;
						}
						currentJob = null;
						currentJobId++;
						if (currentJobId >= jobs.size()) {
							currentJobId = 0;
						}
					}
					while (currentJobId != startCheck);
				}
				if (currentJob == null) {
					if (!finishedSingal) {
						log("All enabled Pregeneration Jobs are finished.");
					}
					currentJobId = -1;
					return;
				}
				log("Now active");
			}
			// elseif "all jobs finished"-signal is trigger -> return (or skip )
			else if (currentJobId == -1) { return; }

			chunks = 0;
			double avgChunkGenerationDuration = chunkGenerationDuration
					.size() > 0 ? chunkGenerationDuration.parallelStream().reduce(0l, (sum, add) -> sum + add) / 1000000.0
							/ chunkGenerationDuration.size() : 0.0;
			debug(() -> "Average Chunk Generation Duration is now " + formatMilliseconds((long) avgChunkGenerationDuration));
			// Generate Chunks until Timeout or Limit or Anvil change
			while (currentJob.hasNextChunk() &&
					(currentJob.aggressive && System.currentTimeMillis() - timeStart < targetDuration
							|| !currentJob.aggressive
									&& avgChunkGenerationDuration - timebank < 0))
			{
				long start = System.nanoTime();
				try {
					// When no world is loaded, or an anvil change is detected -> (re-)load world, skip chunk generation until next execution
					if (w == null || currentJob.nextChunk()) {
						currentAnvilStart = System.currentTimeMillis();
						currentAnvilTicks = 0;
						currentAnvilChunks = 0;
						currentAnvilDuration = 0;
						chunks = 0;
						w = currentJob.getCurrentWorld();
						if (w == null) {
							break;
						}
						int anvilX = currentJob.offsetX + currentJob.regionX;
						int anvilZ = currentJob.offsetZ + currentJob.regionZ;
						log(
								"Starting Pregeneration of Region " + formatAnvil(anvilX, anvilZ) + " in World " + formatWorld(w)
						);
						if (currentJob.isFinished()) {
							break;
						}
					}
					// If not a new world or anvil -> generate/verify a chunk
					int anvilX = currentJob.offsetX + currentJob.regionX;
					int anvilZ = currentJob.offsetZ + currentJob.regionZ;
					if (!w.isChunkGenerated(anvilX * 32 + currentJob.chunkX, anvilZ * 32 + currentJob.chunkZ)) {
						w.loadChunk(anvilX * 32 + currentJob.chunkX, anvilZ * 32 + currentJob.chunkZ, true);
						if (currentJob.dynmapRender) {
							triggerRender(w, anvilX, anvilZ, currentJob.chunkX, currentJob.chunkZ);
						}
						chunks++;
					}
					else if (currentJob.dynmapRender && currentJob.dynmapRenderAll) {
						w.loadChunk(anvilX * 32 + currentJob.chunkX, anvilZ * 32 + currentJob.chunkZ, false);
						triggerRender(w, anvilX, anvilZ, currentJob.chunkX, currentJob.chunkZ);
					}
					// if the current Chunk is the last in the current Anvil, do statistics
					if (currentJob.isLastInAnvil()) {
						for (int x = 0; x < 31; x++) {
							for (int z = 0; z < 31; z++) {
								w.unloadChunk(x, z);
							}
						}
						triggerRender(w, anvilX, anvilZ);
						long fullduration = System.currentTimeMillis() - currentAnvilStart;
						// Accumulate Statistics
						currentAnvilTicks++;
						currentAnvilChunks += chunks;
						lastActualDuration = System.currentTimeMillis() - timeStart;
						currentAnvilDuration += lastActualDuration;
						if (currentAnvilChunks > 0) {
							log("Finished Pregenerating Region: " + formatAnvil(anvilX, anvilZ));
							log("in World:                      " + formatWorld(w));
							fine("Took:                          " + formatMilliseconds(fullduration));
							fine("                               " + currentAnvilTicks + " Ticks");
							fine("Time spent Generating Chunks:  " + formatMilliseconds(currentAnvilDuration));
							fine(
									"                               " + formatMilliseconds(currentAnvilDuration / currentAnvilChunks)
											+ " per Chunk"
							);
							fine(
									"                               " + Math.round(1000.0 * currentAnvilDuration / fullduration) / 10.0
											+ "% Time-Efficiency"
							);
							fine("Generated Chunks:              " + currentAnvilChunks + " Chunks");
							fine("Current Timebank:              " + formatMilliseconds(timebank));
							fine("Current Time per Chunk:        " + formatMilliseconds((long) avgChunkGenerationDuration) + " per Chunk");
						}
						else {
							log(
									"Verified Region " + formatAnvil(anvilX, anvilZ) + " in " + formatWorld(w) + Formatting.FC_DARK_Gray
											+ " over " + currentAnvilTicks + " Ticks"
							);
						}
					}
				}
				finally {
					while (chunkGenerationDuration.size() > 200) {
						chunkGenerationDuration.removeFirst();
					}
					chunkGenerationDuration.addLast(System.nanoTime() - start);
					timebank = timebank - chunkGenerationDuration.getLast() / 1_000_000;
					debug(() -> "Timebank is now " + formatMilliseconds(timebank));
				}
			}
			debug(() -> "Generated " + chunks + " this Tick. Now at " + currentJob.chunkX + ":" + currentJob.chunkZ);
			// Accumulate Statistics
			currentAnvilTicks++;
			currentAnvilChunks += chunks;
			lastActualDuration = System.currentTimeMillis() - timeStart;
			currentAnvilDuration += lastActualDuration;
			// if finished deactivate job
			if (currentJob.isFinished()) {
				log("Now inactive");
				currentJob.save();
				currentJob = null;
			}
		}
		finally {
			if (currentJobId >= 0) {
				previousTask = stp.getServer().getScheduler().runTaskLater(stp, () -> run(), currentJob != null && currentJob.blocking ? 0 : 1);
			}
			// When Jobs finished signal was sent, schedule next check in 6000 Ticks (5 Minutes on lag free Server)
			else if (currentJobId == -1) {
				previousTask = stp.getServer().getScheduler().runTaskLater(stp, () -> run(), 6000);
			}
			timeEnd = System.currentTimeMillis();
		}
	}

	@Override
	public void setArgOffset(int newOffset)
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void debug(Supplier<String> line)
	{
		super.debug(() -> (currentJob != null ? "[" + currentJob.label + "] " : "[No Job] ") + line.get());
	}

	@Override
	protected void fine(String line)
	{
		super.fine((currentJob != null ? "[" + currentJob.label + "] " : "[No Job] ") + line);
	}

	@Override
	protected void fine(Supplier<String> line)
	{
		super.fine(() -> (currentJob != null ? "[" + currentJob.label + "] " : "[No Job] ") + line.get());
	}

	@Override
	protected void log(String line)
	{
		super.log((currentJob != null ? "[" + currentJob.label + "] " : "[No Job] ") + line);
	}

	@Override
	protected void log(Supplier<String> line)
	{
		super.log(() -> (currentJob != null ? "[" + currentJob.label + "] " : "[No Job] ") + line.get());
	}

	/**
	 * Triggers rendering the given chunk
	 *
	 * @param world
	 *            the world the chunk is in
	 * @param anvilX
	 *            the anvil the chunk is in (X coordinate)
	 * @param anvilZ
	 *            the anvil the chunk is in (Z coordinate)
	 */
	protected void triggerRender(World world, int anvilX, int anvilZ)
	{
		int x = anvilX * 32 * 16;
		int z = anvilZ * 32 * 16;
		if (dynmapPlugin != null && dynmapPlugin.isEnabled()) {
			try {
				//(String wid, int minx, int miny, int minz, int maxx, int maxy, int maxz)
				dynmap_triggerRenderOfVolume.invoke(dynmapPlugin, world.getName(), x, 0, z, x + 511, 255, z + 511);
			}
			catch (Exception e) {
				log("Couldn't request render of chunk!", e);
			}
		}
	}

	/**
	 * Triggers rendering the given chunk
	 *
	 * @param world
	 *            the world the chunk is in
	 * @param anvilX
	 *            the anvil the chunk is in (X coordinate)
	 * @param anvilZ
	 *            the anvil the chunk is in (Z coordinate)
	 * @param chunkX
	 *            the X coordinate within the anvil
	 * @param chunkZ
	 *            the Z coordinate within the anvil
	 */
	protected void triggerRender(World world, int anvilX, int anvilZ, int chunkX, int chunkZ)
	{
		int x = (anvilX * 32 + chunkX) * 16;
		int z = (anvilZ * 32 + chunkZ) * 16;
		if (dynmapPlugin != null && dynmapPlugin.isEnabled()) {
			try {
				//(String wid, int minx, int miny, int minz, int maxx, int maxy, int maxz)
				dynmap_triggerRenderOfVolume.invoke(dynmapPlugin, world.getName(), x, 0, z, x + 15, 255, z + 15);
			}
			catch (Exception e) {
				log("Couldn't request render of chunk!", e);
			}
		}
	}

	/**
	 * A single {@link FastPregenerationJob}
	 *
	 * @author Pingger
	 *
	 */
	protected class FastPregenerationJob
	{
		/** whether aggressive pregeneration should be used */
		public boolean		aggressive		= false;
		/** whether this job should block startup */
		public boolean		blocking		= false;
		/** the current chunk X coordinate within the current anvil */
		public int			chunkX			= 0;
		/** the current chunk Z coordinate within the current anvil */
		public int			chunkZ			= 0;
		/** whether this job should block startup */
		public boolean		dynmapRender	= false;
		/** whether this job should block startup */
		public boolean		dynmapRenderAll	= false;
		/** whether this job is enabled */
		public boolean		enabled			= false;
		/** the label of the job */
		public String		label;
		/** the start offset X coordinate */
		public int			offsetX;
		/** the start offset Z coordinate */
		public int			offsetZ;
		/** the maximum radius */
		public int			regionMax;
		/**
		 * the id of the currently processed world in this job config. first by UUID,
		 * then by name
		 */
		public int			regionWorld;
		/** the currently processing Anvils X coordinate relative to offsetX */
		public int			regionX;
		/** the currently processing Anvils Z coordinate relative to offsetZ */
		public int			regionZ;

		/** the worlds to process by Name */
		public List<String>	worldsByName;
		/** the worlds to process by UUID */
		public List<String>	worldsByUUID;

		/**
		 * Get/Create the Job with the given label.
		 *
		 * @param label
		 *            the Label of the Job to load or create
		 */
		public FastPregenerationJob(String label)
		{
			this.label = label;
			if (label.trim().isEmpty()) { throw new IllegalArgumentException("label can't be blank"); }
			load();
		}

		/**
		 * @return the current {@link World} or <code>null</code> if the job is finished
		 */
		public World getCurrentWorld()
		{
			if (isFinished()) { return null; }
			if (regionWorld < worldsByUUID.size()) {
				return stp.getServer().getWorld(UUID.fromString(worldsByUUID.get(regionWorld)));
			}
			else if (regionWorld - worldsByUUID.size() < worldsByName.size()) {
				return stp.getServer().getWorld(worldsByName.get(regionWorld - worldsByUUID.size()));
			}
			return null;
		}

		/**
		 *
		 * @return <code>true</code> if there is a next chunk to pregenerate,
		 *         <code>false</code> otherwise.
		 */
		public boolean hasNextChunk()
		{
			return regionWorld < worldsByName.size() + worldsByUUID.size()
					&& !((regionX > regionMax || regionZ > regionMax) && regionWorld + 1 == worldsByName.size() + worldsByUUID.size());
		}

		/**
		 *
		 * @return <code>true</code>, when this Job is finished (aka no more chunks need
		 *         to be pregenerated), <code>false</code> otherwise (essential inverts
		 *         the output of {@link #hasNextChunk()}
		 */
		public boolean isFinished()
		{
			return !hasNextChunk();
		}

		/**
		 * @return <code>true</code> if the current Chunk is the last in the current
		 *         anvil, <code>false</code> (aka, predicts the output of
		 *         {@link #nextChunk()})
		 */
		public boolean isLastInAnvil()
		{
			return chunkX == 31 && chunkZ == 31;
		}

		/**
		 * (Re-)load this job from config. if not existing create.
		 */
		public void load()
		{
			ConfigurationSection cfg = getConfig(getConfig("jobs"), label);
			ensureConfig(cfg, "enabled", false, null);
			ensureConfig(
					cfg,
					"blocking",
					false,
					"Prevents the Server from finishing startup until this pregeneration Job is finished. This also means, that the server does not accept commands or client connections."
			);
			ensureConfig(
					cfg,
					"aggressive",
					false,
					"When true, at least 1 chunk is generated each tick. otherwise the measured duration to generate a chunk is taken into account and causes some ticks to be skipped to prevent the server from lagging. (blocking-mode overrides this setting to true)"
			);
			ensureConfig(cfg, "startX", 0, "Start Anvil (512x512 Blockarea) X-Coordinate");
			ensureConfig(cfg, "startZ", 0, "Start Anvil (512x512 Blockarea) Z-Coordinate");
			ensureConfig(cfg, "radius", 0, "Radius to generate");
			ensureConfig(cfg, "updateDynmap", true, "If Dynmap is detected, tell it to render the newly generated chunks");
			ensureConfig(cfg, "updateDynmapOnVerify", true, "if updateDynmap is true, also trigger rendering existing chunks");
			ensureConfig(
					cfg,
					"worldsByName",
					stp.getServer().getWorlds().stream().map(world -> world.getName()).collect(Collectors.toList()),
					"The worlds to pregenerate by their Name"
			);
			ensureConfig(cfg, "worldsByUUID", new String[0], "The worlds to pregenerate by their UUID");
			ensureConfig(cfg, "continueX", 0, "Internal Progress Storage");
			ensureConfig(cfg, "continueZ", 0, "Internal Progress Storage");
			ensureConfig(cfg, "continueWorld", 0, "Internal Progress Storage");
			stp.saveConfig();
			chunkX = 0;
			chunkZ = 0;
			enabled = cfg.getBoolean("enabled");
			blocking = cfg.getBoolean("blocking");
			aggressive = cfg.getBoolean("aggressive");
			dynmapRender = cfg.getBoolean("updateDynmap");
			dynmapRenderAll = cfg.getBoolean("updateDynmapOnVerify");
			offsetX = cfg.getInt("startX");
			offsetZ = cfg.getInt("startZ");
			regionMax = cfg.getInt("radius");
			regionWorld = cfg.getInt("continueWorld");
			regionX = cfg.getInt("continueX");
			regionZ = cfg.getInt("continueZ");
			worldsByName = cfg.getStringList("worldsByName");
			worldsByUUID = cfg.getStringList("worldsByUUID");

			if (regionWorld < 0) {
				regionWorld = 0;
			}

			stp.saveConfig();
		}

		/**
		 * Sets the progress to the next chunk. If the anvil is complete also updates to
		 * the next anvil. if the world is complete, updates to the next world. When
		 * this method returns <code>true</code> the config is also updated with the
		 * current Progress
		 *
		 * @return <code>true</code>, when the anvil or world changes,
		 *         <code>false</code> otherwise
		 */
		public boolean nextChunk()
		{
			if (regionWorld >= worldsByName.size() + worldsByUUID.size()) { return false; }
			chunkZ++;
			if (chunkZ >= 32) {
				chunkZ = 0;
				chunkX++;
			}
			if (chunkX == 32) {
				chunkX = 0;
				chunkZ = 0;
				long lastX = regionX;
				long lastZ = regionZ;
				reload();
				if (lastX != regionX || lastZ != regionZ) { return true; }
				if (regionX >= -regionZ && regionX <= regionZ) {
					regionX--;
				}

				// From NegX PosZ to NegX NegZ
				else if (regionX < regionZ) {
					regionZ--;
				}

				// From NegX NegZ to PosX NegZ
				else if (regionX < -regionZ) {
					regionX++;
				}

				// From PosX NegZ to PosX PosZ
				else { // regionX >= regionZ
					regionZ++;
				}
				if (regionZ > regionMax || regionX > regionMax) {
					regionX = 0;
					regionZ = 0;
					regionWorld++;
				}
				save();
				return true;
			}
			return false;
		}

		/** Reloads config and then reloads this Job {@link #load()} */
		public void reload()
		{
			stp.reloadConfig();
			load();
		}

		/**
		 * Saves this Job
		 */
		public void save()
		{
			stp.reloadConfig();
			ConfigurationSection cfg = getConfig(getConfig("jobs"), label);
			cfg.set("enabled", enabled);
			cfg.set("blocking", blocking);
			cfg.set("aggressive", aggressive);
			cfg.set("updateDynmap", dynmapRender);
			cfg.set("updateDynmapOnVerify", dynmapRenderAll);
			cfg.set("startX", offsetX);
			cfg.set("startZ", offsetZ);
			cfg.set("radius", regionMax);
			cfg.set("continueWorld", regionWorld);
			cfg.set("continueX", regionX);
			cfg.set("continueZ", regionZ);
			cfg.set("worldsByName", worldsByName);
			cfg.set("worldsByUUID", worldsByUUID);
			stp.saveConfig();
		}
	}
}
