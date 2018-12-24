package info.iskariot.pingger.java.bukkit.serverTools.monitor;

import java.text.DecimalFormat;
import java.util.LinkedList;

import info.iskariot.pingger.java.bukkit.serverTools.Module;
import info.iskariot.pingger.java.bukkit.serverTools.ServerToolsPlugin;

/**
 * @author Pingger
 *
 */
public class TPSMonitor extends Module implements Runnable
{
    /**
     * Holds the current TPS value of the TPSMonitor. -1 if no tps yet calculated or
     * if disabled.
     */
    public static double        tps               = -1;
    private static final String cfgMessageLabel   = ServerToolsPlugin.buildKey(TPSMonitor.class, "message");
    private static final String cfgThresholdLabel = ServerToolsPlugin.buildKey(TPSMonitor.class, "threshold");

    /**
     * Ensures that the ConfigDefaults are set
     *
     * @param plg
     *            the {@link ServerToolsPlugin} calling
     */
    public static void loadConfigDefaults(ServerToolsPlugin plg)
    {
        Class<?> c = TPSMonitor.class;
        plg.ensureConfig(ServerToolsPlugin.buildKey(TPSMonitor.class, "enabled"), true, null);
        plg.ensureConfig(ServerToolsPlugin.buildKey(TPSMonitor.class, "logging"), false, null);
        plg.ensureConfig(cfgThresholdLabel, 19.5, "the tps limit, below which the warning is triggered");
        plg.ensureConfig(cfgMessageLabel, "[TPSMon] §4{1}§r is below §6{2}§r. Normal is §620.0§r", "{0}unused, {1}CurrentTPS, {2}TPSThreshold");
    }

    private LinkedList<Long> list = new LinkedList<>();

    @Override
    public void onDisable()
    {
        tps = -1;
    }

    @Override
    public void onEnable()
    {
        stp.getServer().getScheduler().runTaskLater(stp, () -> run(), 1);
    }

    @Override
    public void run()
    {
        list.add(System.nanoTime());
        while (list.size() > 20)
        {
            list.removeFirst();
        }
        if (list.size() == 20)
        {
            tps = list.size() * 1e9 / (list.getLast() - list.getFirst());
            log(getClass(), list.getLast() / (long) 1e7 + "-" + list.getFirst() / (long) 1e7 + " => " + tps);
            if (tps < stp.getConfig().getDouble(cfgThresholdLabel))
            {
                String msg = stp.getConfig().getString(cfgMessageLabel);
                msg = msg
                        .replaceAll("{1}", new DecimalFormat("#0.0").format(tps))
                        .replaceAll("{2}", new DecimalFormat("#0.0").format(stp.getConfig().getDouble(cfgThresholdLabel)));
                stp.getLogger().info(msg);
                stp.getServer().broadcastMessage(msg);
            }
        }

        if (stp.isEnabled())
        {
            stp.getServer().getScheduler().runTaskLater(stp, () -> run(), 1);
        }
    }
}
