package info.iskariot.pingger.java.bukkit.serverTools.teams.events;

/**
 *
 * @author Pingger
 *
 */
public interface EventListener
{
	/**
	 * Called when a {@link TeamEvent} is fired.
	 *
	 * @param te
	 *            the fired {@link TeamEvent}
	 */
	public void onEvent(TeamEvent te);
}
