package info.iskariot.pingger.java.bukkit.serverTools.tool;

import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import info.iskariot.pingger.java.bukkit.serverTools.Module;
import info.iskariot.pingger.java.bukkit.serverTools.ServerToolsPlugin;

/**
 * @author Pingger
 *
 */
public class ChunkUnloader extends Module implements Listener
{
    //private static final String cfgAccelerateLabel = "accelerateOnEmpty";
    private static final String cfgTimeout = ServerToolsPlugin.buildKey(ChunkUnloader.class, "timeout");

    /**
     * Ensures that the ConfigDefaults are set
     *
     * @param plg
     *            the {@link ServerToolsPlugin} calling
     */
    public static void loadConfigDefaults(ServerToolsPlugin plg)
    {
        plg
                .ensureConfig(ServerToolsPlugin.buildKey(ChunkUnloader.class, "enabled"), false, "Chunk Unloading can be important when, the Server is only running for a dynmap render, that doesn't unload chunks.");
        plg.ensureConfig(ServerToolsPlugin.buildKey(ChunkUnloader.class, "logging"), true, null);
        plg.ensureConfig(cfgTimeout, 20, "The time in ticks until the chunk should be unloaded.");
    }

    /**
     * Unload Chunk
     * 
     * @param cle
     *            {@link ChunkLoadEvent}
     */
    public void onChunkLoaded(ChunkLoadEvent cle)
    {
        stp.getServer().getScheduler().scheduleSyncDelayedTask(stp, () -> cle.getChunk().unload(true), stp.getConfig().getInt(cfgTimeout));
    }

    @Override
    public void onDisable()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onEnable()
    {
        stp.getServer().getPluginManager().registerEvents(this, stp);
    }
}
