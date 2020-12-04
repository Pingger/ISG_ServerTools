package info.iskariot.pingger.java.bukkit.serverTools.api;

import info.iskariot.pingger.java.bukkit.serverTools.Module;

/**
 * @author Pingger
 * @version 0.0.1
 */
public abstract class APIImplementation extends Module
{
	/**
	 * @return <code>true</code> if this API is available and enabled,
	 *         <code>false</code> otherwise
	 */
	public abstract boolean isAvailable();
}
