package info.iskariot.pingger.java.bukkit.serverTools.tool;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import info.iskariot.pingger.java.bukkit.serverTools.Module;

/**
 * The SleepVote Module
 *
 * @author Pingger
 * @version 0.2.0
 * @since 2018-12-23
 */
public class SleepVote extends Module implements Listener
{
	private static final String	cfgDayDelay				= "SuccessActionDelayTicks";
	private static final String	cfgDayTime				= "SuccessActionDayTime";
	private static final String	cfgMessageFailureLabel	= "FailureMessage";
	private static final String	cfgMessageSuccessLabel	= "SuccessMessage";
	private static final String	cfgThresholdLabel		= "SleepingThreshold";

	/**
	 * Checks the sleep status for the given {@link World}
	 *
	 * @param world
	 *            the {@link World} to check
	 */
	public void checkSleep(World world)
	{
		// Count Sleeping, remove not sleeping w/ sleeping ignored
		int playerCount = world.getPlayers().size();
		int sleeping = 0;
		for (Player p : world.getPlayers()) {
			sleeping += p.isSleeping() ? 1 : 0;
			playerCount -= p.isSleepingIgnored() & !p.isSleeping() ? 1 : 0;
		}
		// No one sleeping, no output
		if (sleeping == 0 || playerCount <= 0) { return; }
		String percentString = "" + (int) Math.floor(100.0 * sleeping / playerCount);
		String percentNeededString = "" + (int) Math.ceil(100 * getConfig().getDouble(cfgThresholdLabel, 0.5));
		if (1.0 * sleeping / playerCount >= getConfig().getDouble(cfgThresholdLabel, 0.5)) {
			String msg = getConfig().getString(cfgMessageSuccessLabel);
			msg = msg
					.replaceAll("\\{0\\}", "")
					.replaceAll("\\{1\\}", world.getName())
					.replaceAll("\\{2\\}", percentString + "%")
					.replaceAll("\\{3\\}", percentNeededString + "%");
			for (Player p : world.getPlayers()) {
				p.sendMessage(msg);
			}
			msg = getConfig().getString(cfgMessageSuccessLabel);
			msg = msg
					.replaceAll("\\{0\\}", "." + world.getName())
					.replaceAll("\\{1\\}", world.getName())
					.replaceAll("\\{2\\}", percentString + "%")
					.replaceAll("\\{3\\}", percentNeededString + "%");
			log(msg);
			stp
					.getServer()
					.getScheduler()
					.scheduleSyncDelayedTask(stp, () -> world.setTime(getConfig().getInt(cfgDayTime)), getConfig().getInt(cfgDayDelay));
		}
		else {
			String msg = getConfig().getString(cfgMessageFailureLabel);
			msg = msg
					.replaceAll("\\{0\\}", "")
					.replaceAll("\\{1\\}", world.getName())
					.replaceAll("\\{2\\}", percentString + "%")
					.replaceAll("\\{3\\}", percentNeededString + "%");
			for (Player p : world.getPlayers()) {
				p.sendMessage(msg);
			}
			msg = getConfig().getString(cfgMessageFailureLabel);
			msg = msg
					.replaceAll("\\{0\\}", "." + world.getName())
					.replaceAll("\\{1\\}", world.getName())
					.replaceAll("\\{2\\}", percentString + "%")
					.replaceAll("\\{3\\}", percentNeededString + "%");
			log(msg.replaceAll("§[0-9a-fA-F]", ""));
		}
	}

	@Override
	public void loadConfigDefaults()
	{
		super.loadConfigDefaults();
		ensureConfig(cfgThresholdLabel, 0.5, "the minimum percentage sleeping to forward, from 0.0 to 1.0");
		ensureConfig(cfgDayDelay, 80, "the time in ticks change the time to " + cfgDayTime);
		ensureConfig(cfgDayTime, 23900, "the time to set on successful sleep vote");

		ensureConfig(
				cfgMessageFailureLabel,
				"[SleepVote{0}] §6{2} sleeping.§r Need at least §4{3}§r",
				"failure message, {0}logging info, {1}WorldName, {2}PercentSleeping, {3}PercentNeeded"
		);

		ensureConfig(
				cfgMessageSuccessLabel,
				"[SleepVote{0}] §2{2} sleeping. Forwarding night.§r",
				"success message, {0}logging info, {1}WorldName, {2}PercentSleeping, {3}PercentNeeded"
		);
	}

	@Override
	public void onDisable()
	{
	}

	@Override
	public void onEnable()
	{
		stp.getServer().getPluginManager().registerEvents(this, stp);
		log("Required Level:" + (int) Math.ceil(100 * getConfig().getDouble(cfgThresholdLabel, 0.5)) + "%");
		log("Delay:" + getConfig().getInt(cfgDayDelay) + " ticks");
		log("Target Time:" + getConfig().getInt(cfgDayTime) + " DayTime-ticks");
	}

	/**
	 * Listens for players starting to sleep
	 *
	 * @param pie
	 *            the interact event
	 */
	@EventHandler
	public void sleepListener(PlayerInteractEvent pie)
	{
		if (!stp.isEnabled(getClass())) { return; }
		if (pie.getClickedBlock() != null) {
			switch (pie.getClickedBlock().getBlockData().getMaterial())
			{
				case BLACK_BED:
				case BLUE_BED:
				case BROWN_BED:
				case CYAN_BED:
				case GRAY_BED:
				case GREEN_BED:
				case LIGHT_BLUE_BED:
				case LIGHT_GRAY_BED:
				case LIME_BED:
				case MAGENTA_BED:
				case ORANGE_BED:
				case PINK_BED:
				case PURPLE_BED:
				case RED_BED:
				case WHITE_BED:
				case YELLOW_BED:
					stp.getServer().getScheduler().scheduleSyncDelayedTask(stp, () -> checkSleep(pie.getPlayer().getWorld()), 1);
					break;

				default:
					break;

			}
		}
	}
}
