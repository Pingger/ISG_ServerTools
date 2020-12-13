package info.iskariot.pingger.java.bukkit.serverTools.teams.events;

import org.bukkit.OfflinePlayer;

import info.iskariot.pingger.java.bukkit.serverTools.teams.Team;

/**
 * Fired when a Player joins a Team
 *
 * @author Pingger
 *
 */
public class PlayerJoinedTeamEvent extends PlayerTeamEvent
{

	/**
	 * Constructs a PlayerJoinedTeamEvent..
	 *
	 * @param t
	 *            the affected Team
	 * @param p
	 *            the affected Player
	 */
	public PlayerJoinedTeamEvent(Team t, OfflinePlayer p)
	{
		super(t, p);
	}

}
