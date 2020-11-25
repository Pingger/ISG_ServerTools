package info.iskariot.pingger.java.bukkit.serverTools;

import java.util.LinkedList;
import java.util.logging.Level;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import info.iskariot.pingger.java.bukkit.serverTools.monitor.TPSMonitor;
import info.iskariot.pingger.java.bukkit.serverTools.tool.FastPregenerator;
import info.iskariot.pingger.java.bukkit.serverTools.tool.SleepVote;
import info.iskariot.pingger.java.bukkit.serverTools.tool.VillagerAntiKill;

/**
 * @author Pingger
 */
public class ServerToolsPlugin extends JavaPlugin implements Listener
{
	private static LinkedList<Module>				loadedModules	= new LinkedList<>();
	@SuppressWarnings("unchecked")
	private static final Class<? extends Module>[]	modules			= new Class[] {

			// monitors
			TPSMonitor.class,

			// tools
			FastPregenerator.class,
			// IdlePregenerator.class,
			// LootablesGenerator.class,
			SleepVote.class,
			VillagerAntiKill.class
	};

	/**
	 * Builds a Config Key based on the class and requested sub-key
	 *
	 * @param cl
	 *            the class
	 * @param key
	 *            the sub key
	 * @return the resulting full key
	 */
	public static String buildKey(Class<?> cl, String key)
	{
		return cl.getCanonicalName() + "." + key;
	}

	/**
	 * Returns if the given module should be enabled. if the Plugin is disabled, the
	 * modules should also be disabled
	 *
	 * @param module
	 *            the module to check
	 * @return <code>true</code> if supposed to be enabled, <code>false</code>
	 *         otherwise
	 */
	public boolean isEnabled(Class<? extends Module> module)
	{
		return isEnabled() && getConfig().getBoolean(ServerToolsPlugin.buildKey(module, "enabled"), true);
	}

	@Override
	public void onDisable()
	{
		while (!loadedModules.isEmpty()) {
			try {
				Module m = loadedModules.removeLast();
				getLogger().info("Disabling: " + m.getClass().getCanonicalName());
				m.onDisable();
			}
			catch (Throwable e) {
				getLogger().log(Level.SEVERE, "§4Module couldn't be disabled!", e);
			}
		}
		getLogger().info("Disabled!");
	}

	@Override
	public void onEnable()
	{
		reloadConfig();
		saveConfig();
		for (Class<? extends Module> mc : modules) {
			if (getConfig().getBoolean(buildKey(mc, "enabled"), true)) {
				try {
					getLogger().info("§2Enabling: §r" + mc.getCanonicalName());
					Module m = mc.getConstructor().newInstance();
					m.setServerToolsPlugin(this);
					m.loadConfigDefaults();
					m.onEnable();
					loadedModules.add(m);
				}
				catch (IllegalStateException ise) {
					getLogger().log(Level.SEVERE, "§4Module couldn't be loaded!" + ise.toString());
				}
				catch (Throwable e) {
					getLogger().log(Level.SEVERE, "§4Module couldn't be loaded!", e);
				}
			}
		}
		saveConfig();
		getLogger().info("§2§lEnabled!");
	}

	@Override
	public void onLoad()
	{
		getLogger().info("Loading Iskariot Gaming's Server Tools Plugin...");
		getLogger().info("Loaded!");
	}
}
