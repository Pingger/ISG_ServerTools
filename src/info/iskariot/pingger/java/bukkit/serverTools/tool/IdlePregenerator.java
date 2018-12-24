package info.iskariot.pingger.java.bukkit.serverTools.tool;

import java.awt.Point;
import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.World;

import info.iskariot.pingger.java.bukkit.serverTools.Module;
import info.iskariot.pingger.java.bukkit.serverTools.ServerToolsPlugin;
import info.iskariot.pingger.java.bukkit.serverTools.monitor.TPSMonitor;

/**
 * @author Pingger
 *
 */
public class IdlePregenerator extends Module implements Runnable
{
    //private static final String cfgAccelerateLabel = "accelerateOnEmpty";
    private static final String cfgMaxLabel        = "blockLimit";
    private static final String cfgMaxPlayersLabel = "maxPlayers";
    private static final String cfgSkipLabel       = "startWithRadius";
    private static final String cfgThresholdLabel  = "tpsThreshold";
    private static final String cfgTPTLabel        = "tpt";
    private static final String cfgWorldsLabel     = "worlds";

    /**
     * Ensures that the ConfigDefaults are set
     *
     * @param plg
     *            the {@link ServerToolsPlugin} calling
     */
    public static void loadConfigDefaults(ServerToolsPlugin plg)
    {
        Class<?> c = IdlePregenerator.class;
        plg.ensureConfig(c, "enabled", true, null);
        plg.ensureConfig(c, "logging", true, null);
        plg.ensureConfig(c, cfgThresholdLabel, 19.5, "the tps limit, below which chunks are no longer pregenerated. Depends on TPSMonitor!");
        plg.ensureConfig(c, cfgTPTLabel, 10, "the Time Per Tick (in ms) to work on pregenerating chunks. Minimum generation of 1 Chunk per tick");
        plg.ensureConfig(c, cfgMaxPlayersLabel, 4, "the maximum count of logged in players to autogenerate");
        //plg.ensureConfig(c, cfgAccelerateLabel, false, "Accelerate if the server is empty? Tries to fully fill a tick with pregenerating");
        plg.ensureConfig(c, cfgMaxLabel, 4096, "The maximum Dimension in blocks (from 0,0) to pregenerate. Might be slightly more");
        plg.ensureConfig(c, cfgSkipLabel, 0, "Skips all generating within this radius from (0,0) in Blocks");
        ArrayList<String> worlds = new ArrayList<>();
        for (World w : plg.getServer().getWorlds())
        {
            worlds.add(w.getName());
        }
        plg.ensureConfig(c, cfgWorldsLabel, worlds.toArray(new String[0]), "The Worlds to pregenerate");
    }

    private Point current     = new Point();
    private int   direction   = 0;

    private int   lastFailure = 0;

    @Override
    public void onDisable()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onEnable()
    {
        stp.getServer().getScheduler().runTaskLater(stp, () -> run(), 1);
        if (!stp.getConfig().getBoolean(ServerToolsPlugin.buildKey(TPSMonitor.class, "enabled")))
        {
            stp.getLogger().severe("Idle Pregenerator can't work without the TPSMonitor!");
        }
        if (current.x == 0)
        {
            current.x = stp.getConfig().getInt(ServerToolsPlugin.buildKey(getClass(), cfgSkipLabel));
        }
    }

    @Override
    public void run()
    {
        long start = System.nanoTime();
        if (current.x * 16l >= stp.getConfig().getInt(ServerToolsPlugin.buildKey(getClass(), cfgMaxLabel))
                && current.y * 16l >= stp.getConfig().getInt(ServerToolsPlugin.buildKey(getClass(), cfgMaxLabel)))
        {
            if (lastFailure != 3)
            {
                log(getClass(), "[IdlePregenerator] Maximum area generated");
            }
            lastFailure = 3;
        }
        else if (stp.getServer().getOnlinePlayers().size() > stp.getConfig().getInt(ServerToolsPlugin.buildKey(getClass(), cfgMaxPlayersLabel)))
        {
            if (lastFailure != 1)
            {
                log(getClass(), "[IdlePregenerator] Too many Players");
            }
            lastFailure = 1;
        }
        else if (TPSMonitor.tps < stp.getConfig().getDouble(ServerToolsPlugin.buildKey(getClass(), cfgThresholdLabel)))
        {
            if (lastFailure != 2)
            {
                log(getClass(), "[IdlePregenerator] TPS too low (Current: " + TPSMonitor.tps + ")");
            }
            lastFailure = 2;
        }
        else
        {
            if (lastFailure != 0)
            {
                log(getClass(), "[IdlePregenerator] Continue");
            }
            lastFailure = 0;
            long max = stp.getConfig().getInt(ServerToolsPlugin.buildKey(getClass(), cfgTPTLabel));
            ArrayList<World> worlds = new ArrayList<>();
            for (String worldName : stp.getConfig().getStringList(ServerToolsPlugin.buildKey(getClass(), cfgWorldsLabel)))
            {
                World w = stp.getServer().getWorld(worldName);
                if (w != null)
                {
                    worlds.add(w);
                }
            }

            do
            {
                switch (direction)
                {
                    case 0: /* Top Row */
                        current.x += 1;
                        if (current.x > current.y)
                        {
                            direction = 1;
                        }
                        break;
                    case 1: /* Right Column */
                        current.y -= 1;
                        if (current.y == -current.x)
                        {
                            direction = 2;
                        }
                        break;
                    case 2: /* Bottom Row */
                        current.x -= 1;
                        if (current.x == current.y)
                        {
                            direction = 3;
                        }
                        break;
                    case 3: /* Left Column */
                        current.y += 1;
                        if (current.y == -current.x)
                        {
                            direction = 0;
                            stp.reloadConfig();
                            stp.getConfig().set(ServerToolsPlugin.buildKey(getClass(), cfgSkipLabel), current.y * 16);
                            stp.saveConfig();
                            log(getClass(), "[IdlePregenerator] Radius now: " + (current.y + 1) * 16);
                        }
                        break;
                    default: /* WTF?! */
                        direction = 0;
                }
                for (World world : worlds)
                {
                    Chunk chunk = world.getChunkAt(current.x, current.y);
                    chunk.load(true);
                    chunk.unload(true);
                }
            }
            while (max > (System.nanoTime() - start) / 1e6);
            if (stp.isEnabled())
            {
                stp.getServer().getScheduler().runTaskLater(stp, () -> run(), 1);
            }
            return;
        }
        if (stp.isEnabled())
        {
            stp.getServer().getScheduler().runTaskLater(stp, () -> run(), 20);
        }
    }
}
