package info.iskariot.pingger.java.bukkit.serverTools.monitor;

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.*;

import org.bukkit.command.CommandSender;

import info.iskariot.pingger.java.bukkit.serverTools.Module;
import info.iskariot.pingger.java.bukkit.serverTools.util.ConfigParser;

/**
 * @author Pingger
 *
 */
public class TPSMonitor extends Module implements Runnable
{
	/** The duration it took to calculate the last tps round in ns */
	public static long									took	= -1;
	/**
	 * Holds the current TPS value of the TPSMonitor. -1 if no tps yet calculated or
	 * if disabled.
	 */
	public static double								tps		= -1;
	private static TreeMap<Integer, LinkedList<Long>>	tickMap	= new TreeMap<>();
	private static TreeMap<Integer, Double>				tpsMap	= new TreeMap<>();

	/**
	 * Send the current TPS as Messages to the given {@link CommandSender}
	 *
	 * @param s
	 *            the target
	 */
	public static void sendTPS(CommandSender s)
	{
		for (Integer interval : tickMap.keySet()) {
			s.sendMessage("TPS " + interval + ": " + String.format("%.2f", tpsMap.get(interval)));
		}
	}

	private long		lastDebug		= 0;

	private long		lastNotify		= 0;

	private String		message			= "{1}/20 ({2})";

	private double		threshold		= 15;

	private Duration	warningCooldown	= Duration.ofSeconds(5);

	@Override
	public void loadConfigDefaults()
	{
		ensureConfig("enabled", true, null);
		ensureConfig("logging", false, null);
		ensureConfig("threshold", 19.5, "the tps limit, below which the warning is triggered");
		ensureConfig(
				"warningCooldown",
				ConfigParser.storeDuration(Duration.ofSeconds(5)),
				"delay (as Duration, no unit == milliseconds) between warnings"
		);
		ensureConfig("message", "[TPSMon] TPS drop! §4{1}§r/§620.0§r. Threshold: §6{2}§r", "{0}unused, {1}CurrentTPS, {2}TPSThreshold");
		ensureConfig("intervals", new String[] {
				"20", "200", "1200"
		}, "The intervals to calculate tps over (in ticks)");
	}

	@Override
	public void onConfigReload()
	{
		super.onConfigReload();
		List<Integer> ints = getConfig().getIntegerList("intervals");
		// Force tps over 20 ticks
		tickMap.putIfAbsent(20, new LinkedList<>());
		tpsMap.putIfAbsent(20, -1d);
		// Add configured intervals
		for (int i : ints) {
			if (i < 20) {
				continue;
			}
			tickMap.putIfAbsent(i, new LinkedList<>());
			tpsMap.putIfAbsent(i, -1d);
		}
		// Remove no longer configured intervals
		for (int i : tickMap.keySet()) {
			if (!ints.contains(i) && i != 20) {
				tickMap.remove(i);
				tpsMap.remove(i);
			}
		}
		// Update and write-back the other settings
		threshold = getConfig().getDouble("threshold");
		warningCooldown = ConfigParser.loadDuration(getConfig().getString("warningCooldown"), Duration.ofSeconds(5));
		getConfig().set("warningCooldown", ConfigParser.storeDuration(warningCooldown));
		message = getConfig().getString("message");
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
		onConfigReload();
	}

	@Override
	public void run()
	{
		onTick();
		if (tps >= 0) {
			if (tps < threshold
					&& lastNotify + warningCooldown.toMillis() < System.currentTimeMillis())
			{
				lastNotify = System.currentTimeMillis();
				String msg = message;
				msg = msg
						.replaceAll("\\{1\\}", new DecimalFormat("#0.0").format(tps))
						.replaceAll("\\{2\\}", new DecimalFormat("#0.0").format(threshold));
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
		long s = System.nanoTime();
		for (Integer interval : tickMap.keySet()) {
			LinkedList<Long> list = tickMap.get(interval);
			list.addLast(s);
			while (list.size() > interval + 1) {
				list.removeFirst();
			}
			if (list.size() > interval) {
				double newTPS = 20 * (list.getLast() - list.getFirst()) / (1e9 * interval / 20);
				tpsMap.put(interval, newTPS);
			}
		}
		tps = tpsMap.get(20);
		took = System.nanoTime() - s;
		if (isDebug() && lastDebug + 1e3 < System.currentTimeMillis()) {
			lastDebug = System.currentTimeMillis();
			debug(() -> "<<<<<<<<<< TPS >>>>>>>>>>");
			debug(() -> "TPS: " + String.format("%.2f", tps));
			for (Integer interval : tickMap.keySet()) {
				debug(() -> "TPS " + interval + ": " + String.format("%.2f", tpsMap.get(interval)));
			}
			debug(() -> "Took: " + took / 1e6 + "ms");
		}
	}
}
