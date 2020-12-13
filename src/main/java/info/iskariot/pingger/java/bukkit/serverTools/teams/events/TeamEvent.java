package info.iskariot.pingger.java.bukkit.serverTools.teams.events;

import info.iskariot.pingger.java.bukkit.serverTools.teams.Team;

public class TeamEvent
{
	/** the affected Team */
	public final Team team;

	/**
	 * Construct a generic TeamEvent
	 *
	 * @param t
	 *            the affected Team
	 */
	public TeamEvent(Team t)
	{
		team = t;
	}
}
