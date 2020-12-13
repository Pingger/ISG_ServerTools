package info.iskariot.pingger.java.bukkit.serverTools.monitor;

import java.text.DecimalFormat;
import java.util.LinkedList;

import info.iskariot.pingger.java.bukkit.serverTools.Module;

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
	public static double		tps			= -1;
	private long				lastNotify	= 0;

	private LinkedList<Long>	list		= new LinkedList<>();

	@Override
	public void loadConfigDefaults()
	{
		ensureConfig("enabled", true, null);
		ensureConfig("logging", false, null);
		ensureConfig("threshold", 19.5, "the tps limit, below which the warning is triggered");
		ensureConfig("warningCooldown", 5000, "delay (in ms) between warnings");
		ensureConfig("message", "[TPSMon] TPS drop! §4{1}§r/§620.0§r. Threshold: §6{2}§r", "{0}unused, {1}CurrentTPS, {2}TPSThreshold");
	}

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
		while (list.size() > 20) {
			list.removeFirst();
		}
		if (list.size() == 20) {
			tps = list.size() * 1e9 / (list.getLast() - list.getFirst());
			//log(getClass(), list.getLast() / (long) 1e7 + "-" + list.getFirst() / (long) 1e7 + " => " + tps);
			if (tps < getConfig().getDouble("threshold")
					&& lastNotify + getConfig().getInt("warningCooldown") < System.currentTimeMillis())
			{
				lastNotify = System.currentTimeMillis();
				String msg = getConfig().getString("message");
				msg = msg
						.replaceAll("\\{1\\}", new DecimalFormat("#0.0").format(tps))
						.replaceAll("\\{2\\}", new DecimalFormat("#0.0").format(getConfig().getDouble("threshold")));
				stp.getServer().broadcastMessage(msg);
				stp.reloadConfig();
			}
		}

		if (stp.isEnabled()) {
			stp.getServer().getScheduler().runTaskLater(stp, () -> run(), 1);
		}
	}
}
