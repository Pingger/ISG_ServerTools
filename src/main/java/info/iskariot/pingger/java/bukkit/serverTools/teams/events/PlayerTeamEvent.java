package info.iskariot.pingger.java.bukkit.serverTools.teams.events;

import org.bukkit.OfflinePlayer;

import info.iskariot.pingger.java.bukkit.serverTools.teams.Team;

/**
 * A generic Player + Team Event. Fired when something relating a player happens
 * to a team.
 *
 * @author Pingger
 *
 */
public class PlayerTeamEvent extends TeamEvent
{
	/** The affected Player */
	public final OfflinePlayer player;

	/**
	 * Constructs a generic PlayerTeamEvent
	 *
	 * @param t
	 *            the affected Team
	 * @param p
	 *            the affected Player
	 */
	public PlayerTeamEvent(Team t, OfflinePlayer p)
	{
		super(t);
		player = p;
	}

}
