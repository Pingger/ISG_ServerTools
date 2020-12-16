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
	/** Holds TPS over last 1200 ticks (1 minute) */
	public static double		tps1200		= -1;
	/** Holds TPS over last 20 ticks (1 second) */
	public static double		tps20		= -1;
	/** Holds TPS over last 200 ticks (10 seconds) */
	public static double		tps200		= -1;
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
		onTick();
		if (tps20 >= 0) {
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

	private void onTick()
	{
		list.add(System.nanoTime());
		while (list.size() > 1200) {
			list.removeFirst();
		}
		if (list.size() >= 20) {
			tps20 = list.getLast() - list.get(list.size() - 20);
			tps20 /= 1e9;
			tps20 *= 20;
			tps = tps20;
		}
		if (list.size() >= 200) {
			tps200 = list.getLast() - list.get(list.size() - 200);
			tps200 /= 1e9;
			tps200 /= 10;
			tps200 *= 20;
		}
		if (list.size() >= 1200) {
			tps1200 = list.getLast() - list.get(list.size() - 1200);
			tps1200 /= 1e9;
			tps1200 /= 60;
			tps1200 *= 20;
		}
	}
}
