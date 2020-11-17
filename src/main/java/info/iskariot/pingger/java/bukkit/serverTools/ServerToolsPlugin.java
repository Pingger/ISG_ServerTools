package info.iskariot.pingger.java.bukkit.serverTools;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.logging.Level;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import info.iskariot.pingger.java.bukkit.serverTools.monitor.TPSMonitor;
import info.iskariot.pingger.java.bukkit.serverTools.sleepVote.SleepVote;
import info.iskariot.pingger.java.bukkit.serverTools.tool.IdlePregenerator;
import info.iskariot.pingger.java.bukkit.serverTools.tool.VillagerAntiKill;

/**
 * @author Pingger
 *
 */
public class ServerToolsPlugin extends JavaPlugin implements Listener
{
	private static LinkedList<Module>				loadedModules	= new LinkedList<>();
	@SuppressWarnings("unchecked")
	private static final Class<? extends Module>[]	modules			= new Class[] {
			IdlePregenerator.class,
			TPSMonitor.class,
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
	 * Ensures, that the given Key is set.
	 *
	 * @param key
	 *            the sub key
	 * @param def
	 *            the default value if not set
	 * @param desc
	 *            The Description for this node
	 */
	public void ensureConfig(String key, boolean def, String desc)
	{
		if (desc != null && !desc.trim().isEmpty()) {
			getConfig().set(key + "_Desc", desc);
		}
		getConfig().set(key, getConfig().getBoolean(key, def));
	}

	/**
	 * Ensures, that the given Key is set.
	 *
	 * @param key
	 *            the sub key
	 * @param def
	 *            the default value if not set
	 * @param desc
	 *            The Description for this node
	 */
	public void ensureConfig(String key, double def, String desc)
	{
		if (desc != null && !desc.trim().isEmpty()) {
			getConfig().set(key + "_Desc", desc);
		}
		getConfig().set(key, getConfig().getDouble(key, def));
	}

	/**
	 * Ensures, that the given Key is set.
	 *
	 * @param key
	 *            the sub key
	 * @param def
	 *            the default value if not set
	 * @param desc
	 *            The Description for this node
	 */
	public void ensureConfig(String key, int def, String desc)
	{
		if (desc != null && !desc.trim().isEmpty()) {
			getConfig().set(key + "_Desc", desc);
		}
		getConfig().set(key, getConfig().getInt(key, def));
	}

	/**
	 * Ensures, that the given Key is set.
	 *
	 * @param key
	 *            the sub key
	 * @param def
	 *            the default value if not set
	 * @param desc
	 *            The Description for this node
	 */
	public void ensureConfig(String key, long def, String desc)
	{
		if (desc != null && !desc.trim().isEmpty()) {
			getConfig().set(key + "_Desc", desc);
		}
		getConfig().set(key, getConfig().getLong(key, def));
	}

	/**
	 * Ensures, that the given Key is set.
	 *
	 * @param key
	 *            the sub key
	 * @param def
	 *            the default value if not set
	 * @param desc
	 *            The Description for this node
	 */
	public void ensureConfig(String key, String def, String desc)
	{
		if (desc != null && !desc.trim().isEmpty()) {
			getConfig().set(key + "_Desc", desc);
		}
		getConfig().set(key, getConfig().getString(key, def));
	}

	/**
	 * Ensures, that the given Key is set.
	 *
	 * @param key
	 *            the sub key
	 * @param def
	 *            the default value if not set
	 * @param desc
	 *            The Description for this node
	 */
	public void ensureConfig(String key, String[] def, String desc)
	{
		if (desc != null && !desc.trim().isEmpty()) {
			getConfig().set(key + "_Desc", desc);
		}
		if (!getConfig().isList(key)) {
			getConfig().set(key, def);
		}
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
				getLogger().log(Level.SEVERE, "Module couldn't be disabled!", e);
			}
		}
		getLogger().info("Disabled!");
	}

	@Override
	public void onEnable()
	{
		reloadConfig();
		loadConfigDefaults();
		saveConfig();
		for (Class<? extends Module> mc : modules) {
			if (getConfig().getBoolean(buildKey(mc, "enabled"), true)) {
				try {
					getLogger().info("Enabling: " + mc.getCanonicalName());
					Module m = mc.getConstructor().newInstance();
					m.setServerToolsPlugin(this);
					m.onEnable();
					loadedModules.add(m);
				}
				catch (Throwable e) {
					getLogger().log(Level.SEVERE, "Module couldn't be loaded!", e);
				}
			}
		}
		getLogger().info("Enabled!");
	}

	@Override
	public void onLoad()
	{
		getLogger().info("Loading Iskariot Gaming's Server Tools Plugin...");
		getLogger().info("Loaded!");
	}

	/**
	 * Adds unset values to the Config
	 */
	private void loadConfigDefaults()
	{
		SleepVote.loadConfigDefaults(this);
		for (Class<? extends Module> mc : modules) {
			try {
				Method m = mc.getDeclaredMethod("loadConfigDefaults", getClass());
				m.invoke(null, this);
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
}
