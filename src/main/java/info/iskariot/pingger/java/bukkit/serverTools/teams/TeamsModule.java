package info.iskariot.pingger.java.bukkit.serverTools.teams;

import java.util.ArrayList;
import java.util.HashSet;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import info.iskariot.pingger.java.bukkit.serverTools.Module;
import info.iskariot.pingger.java.bukkit.serverTools.ServerToolsPlugin;
import info.iskariot.pingger.java.bukkit.serverTools.teams.events.EventListener;
import info.iskariot.pingger.java.bukkit.serverTools.teams.events.TeamEvent;

/**
 *
 * @author Pingger
 *
 */
public class TeamsModule extends Module implements Listener
{
	private HashSet<EventListener>	listeners	= new HashSet<>();
	private ArrayList<Team>			teams		= new ArrayList<>();

	/**
	 *
	 * @param evt
	 *            the Event to fire
	 */
	public void fire(TeamEvent evt)
	{
		listeners.parallelStream().forEach(e -> e.onEvent(evt));
	}

	@Override
	public ServerToolsPlugin getServerToolsPlugin()
	{
		return stp;
	}

	/**
	 * @param name
	 *            of the Team
	 * @return the Config for this Team or an empty config
	 */
	public ConfigurationSection getTeamConfig(String name)
	{
		return getConfig(getTeamsConfig(), name);
	}

	@Override
	public void log(String line)
	{
		super.log(line);
	}

	@Override
	public void onDisable()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onEnable()
	{
		ConfigurationSection teamscfg = getTeamsConfig();
		for (String label : teamscfg.getKeys(false)) {
			super.log("Loading Team: " + label);
			teams.add(new Team(this, label));
		}
		stp.getServer().getPluginManager().registerEvents(this, stp);
	}

	/**
	 * @param apce
	 *            {@link AsyncPlayerChatEvent}
	 */
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent apce)
	{

		teams.forEach(t -> t.onPlayerChat(apce));
	}

	/**
	 * @param pde
	 *            {@link PlayerDeathEvent}
	 */
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent pde)
	{
		teams.forEach(t -> t.onPlayerDeath(pde));
	}

	/**
	 * @param pje
	 *            {@link PlayerJoinEvent}
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent pje)
	{
		teams.forEach(t -> t.onPlayerJoin(pje));
	}

	/**
	 *
	 * @param ple
	 *            {@link PlayerLoginEvent}
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(PlayerLoginEvent ple)
	{
		stp.reloadConfig();
		teams.forEach(t -> t.reloadConfig());
		teams.forEach(t -> t.onPlayerLogin(ple));
	}

	/**
	 * @return the {@link ConfigurationSection} containing the Teams
	 */
	protected ConfigurationSection getTeamsConfig()
	{
		return getConfig("teams");
	}

}
