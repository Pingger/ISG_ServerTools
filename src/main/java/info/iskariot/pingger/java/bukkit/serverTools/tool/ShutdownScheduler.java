package info.iskariot.pingger.java.bukkit.serverTools.tool;

import static info.iskariot.pingger.java.bukkit.serverTools.util.Formatting.*;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import info.iskariot.pingger.java.bukkit.serverTools.CommandInterface;
import info.iskariot.pingger.java.bukkit.serverTools.Module;

public class ShutdownScheduler extends Module implements CommandInterface
{
	private static final String[]	LABEL			= new String[] {
			"shutdown", "s", "stop"
	};

	private boolean					canReady		= false;
	private DateTimeFormatter		dtf				= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private boolean					isRestart		= false;
	private HashSet<Player>			readyPlayers	= new HashSet<>();
	private String					reason			= "";
	private Instant					stopRequest		= null;

	private TreeSet<Instant>		warningTimes	= new TreeSet<>((a, b) -> a.compareTo(b));

	public void abortShutdown()
	{
		if (!stp.getServer().isPrimaryThread()) {
			stp.getServer().getScheduler().runTask(stp, () -> abortShutdown());
			return;
		}
		warningTimes.clear();
		stopRequest = null;
		stp.getServer().broadcastMessage((isRestart ? "Restart" : "Shutdown") + " has been aborted!");
	}

	@Override
	public boolean getHelp(CommandSender sender, Command command, String label, String[] args)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String[] getLabel()
	{
		return LABEL;
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
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (args.length == 1) {
			initiateShutdown(Duration.of(5, ChronoUnit.MINUTES), FC_ITALIC + "none given" + FC_RESET, true, true);
		}
		else if (args.length == 2) {
			if (args[2].equalsIgnoreCase("cancel")
					|| args[2].equalsIgnoreCase("abort"))
			{

			}
		}
		return false;
	}

	@Override
	public void onDisable()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onEnable()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setArgOffset(int newOffset)
	{
		// TODO Auto-generated method stub

	}

	protected void broadcastReadyMessage()
	{
		if (this.canReady) {

			stp
					.getServer()
					.broadcastMessage(
							"§2" + getPlayersReady() + " of " + getRequiredPlayers() + " are ready for the " + (isRestart ? "restart" : "shutdown")
									+ "!"
					);
		}
	}

	protected void broadcastReadyMessages()
	{

		if (this.canReady) {
			broadcastReadyMessage();
			for (Player p : stp.getServer().getOnlinePlayers()) {

				if (readyPlayers.contains(p)) {
					p.sendMessage("§2You are marked as ready!");
				}
				else {
					p.sendMessage("§4You are NOT marked as ready!");
				}
			}
		}
	}

	protected void broadcastStatus()
	{
		stp.getServer().broadcastMessage("§4The Server is about to " + (isRestart ? "§2RESTART" : "§6HUTDOWN") + "§4!");
		stp.getServer().broadcastMessage("§3REASON: " + this.reason);
		stp.getServer().broadcastMessage("§3IN: " + formatDuration(Duration.between(Instant.now(), stopRequest).abs()));
		stp.getServer().broadcastMessage("§3AT: " + dtf.format(stopRequest));
		if (this.canReady) {
			stp.getServer().broadcastMessage("§2READY-MODE is enabled! Use §3/ist s ready§2 to mark yourself as ready!");
			broadcastReadyMessages();
		}
	}

	protected void cron()
	{
		Instant now = Instant.now();
		while (warningTimes.first().compareTo(now) <= 0) {
			Instant I = warningTimes.pollFirst();
			Duration d = Duration.between(stopRequest, I).abs();
			if (d.toHours() > 0) {

			}
		}
	}

	protected String formatDuration(Duration d)
	{
		StringBuilder sb = new StringBuilder();
		if (d.toDaysPart() > 0) {
			sb.append(d.toDaysPart());
			sb.append(d.toDaysPart() != 1 ? " Days" : " Day");
		}
		if (d.toHoursPart() > 0) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(d.toHoursPart());
			sb.append(d.toHoursPart() != 1 ? " Hours" : " Hour");
		}
		if (d.toMinutesPart() > 0) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(d.toMinutesPart());
			sb.append(d.toMinutesPart() != 1 ? " Minutes" : " Minute");
		}
		if (d.toSecondsPart() > 0) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(d.toMinutesPart());
			sb.append(d.toMinutesPart() != 1 ? " Seconds" : " Second");
		}
		return sb.toString();
	}

	protected int getPlayersReady()
	{
		return readyPlayers.parallelStream().reduce(0, (i, p) -> i + (p.isOnline() ? 1 : 0), (a, b) -> a + b);
	}

	protected int getRequiredPlayers()
	{
		return stp.getServer().getOnlinePlayers().size();
	}

	protected void initiateShutdown(Duration d, String reason, boolean isRestart, boolean canReady)
	{
		Instant now = Instant.now();
		Instant newStopRequest = now.plus(d);
		if (stopRequest == null || stopRequest.compareTo(newStopRequest) > 0) {
			stopRequest = newStopRequest;
			this.reason = reason.replaceAll("&([0-9a-F])", "§$1");
			this.canReady = canReady;
			this.isRestart = isRestart;
			warningTimes.clear();
			Instant stamp = stopRequest;
			for (int i = 0; i < 15 && stamp.compareTo(now) < 0; i++) {
				warningTimes.add(stamp);
				stamp = stamp.plusSeconds(1);
			}
			for (int i = 0; i < 3 && stamp.compareTo(now) < 0; i++) {
				warningTimes.add(stamp);
				stamp = stamp.plusSeconds(15);
			}
			for (int i = 0; i < 15 && stamp.compareTo(now) < 0; i++) {
				warningTimes.add(stamp);
				stamp = stamp.plus(1, ChronoUnit.MINUTES);
			}
			for (int i = 0; i < 3 && stamp.compareTo(now) < 0; i++) {
				warningTimes.add(stamp);
				stamp = stamp.plus(15, ChronoUnit.MINUTES);
			}
			while (stamp.compareTo(now) < 0) {
				warningTimes.add(stamp);
				stamp = stamp.plus(1, ChronoUnit.HOURS);
			}
			broadcastStatus();
		}
	}
}
