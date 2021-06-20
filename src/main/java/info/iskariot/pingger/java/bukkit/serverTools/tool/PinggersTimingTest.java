package info.iskariot.pingger.java.bukkit.serverTools.tool;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.TimeZone;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import info.iskariot.pingger.java.bukkit.serverTools.Module;

public class PinggersTimingTest extends Module
{

	private static final DateTimeFormatter	dtf	= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss (Z, z, X)");
	private static final SimpleDateFormat	sdf	= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss (Z, z, X)");

	@Override
	public void onConfigReload()
	{
		super.onConfigReload();
		if (!isLogging()) { return; }
		CommandSender cs = stp.getServer().getConsoleSender();
		Player p = stp.getServer().getPlayer("fa4564f7-01e6-43c7-ba56-7d6ed830edb4");
		if (p != null) {
			cs = p;
		}
		cs.sendMessage("Timestamps:");
		cs.sendMessage(sdf.format(Calendar.getInstance().getTime()));
		cs.sendMessage("GMT+1: " + sdf.format(Calendar.getInstance(TimeZone.getTimeZone("GMT+1")).getTime()));
		cs.sendMessage("UTC:   " + sdf.format(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime()));
		cs.sendMessage("CET:   " + sdf.format(Calendar.getInstance(TimeZone.getTimeZone("CET")).getTime()));
		cs.sendMessage("CEST:  " + sdf.format(Calendar.getInstance(TimeZone.getTimeZone("CEST")).getTime()));
		cs.sendMessage("GMT+1: " + dtf.withZone(ZoneId.of("GMT+1")).format(Instant.now()));
		cs.sendMessage("UTC:   " + dtf.withZone(ZoneId.of("UTC")).format(Instant.now()));
		cs.sendMessage("CET:   " + dtf.withZone(ZoneId.of("CET")).format(Instant.now()));
		cs.sendMessage("Europe/Berlin:  " + dtf.withZone(TimeZone.getTimeZone("Europe/Berlin").toZoneId()).format(Instant.now()));
		cs.sendMessage("done");
	}

	@Override
	public void onDisable()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onEnable()
	{

	}
}
