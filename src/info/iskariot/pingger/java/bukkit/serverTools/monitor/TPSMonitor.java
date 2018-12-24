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
    private static final String cfgMessageLabel   = "message";
    private static final String cfgThresholdLabel = "threshold";

    /**
     * Ensures that the ConfigDefaults are set
     *
     * @param plg
     *            the {@link ServerToolsPlugin} calling
     */
    public static void loadConfigDefaults(ServerToolsPlugin plg)
    {
        Class<?> c = TPSMonitor.class;
        plg.ensureConfig(c, "enabled", true, "if the module is enabled");
        plg.ensureConfig(c, "logging", false, "if messages of this module are logged to the console");
        plg.ensureConfig(c, cfgThresholdLabel, 19.5, "the tps limit, below which the warning is triggered");
        plg.ensureConfig(c, cfgMessageLabel, "[TPSMon] §4{1}§r is below §6{2}§r. Normal is §620.0§r", "{0}unused, {1}CurrentTPS, {2}TPSThreshold");
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
            log(getClass(), list.size() + list.toString());
            log(getClass(), list.getLast() / (long) 1e7 + "-" + list.getFirst() / (long) 1e7 + " => " + tps);
            if (tps < stp.getConfig().getDouble(cfgThresholdLabel))
            {

                stp
                        .getServer()
                        .broadcastMessage(
                                cfgMessageLabel
                                        .replaceAll("{1}", new DecimalFormat("#0.0").format(tps))
                                        .replaceAll("{2}", new DecimalFormat("#0.0").format(stp.getConfig().getDouble(cfgThresholdLabel)))
                        );
            }
        }

        if (stp.isEnabled())
        {
            stp.getServer().getScheduler().runTaskLater(stp, () -> run(), 1);
        }
    }
}
