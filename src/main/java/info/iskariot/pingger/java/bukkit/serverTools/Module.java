package info.iskariot.pingger.java.bukkit.serverTools;

import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Abstract class for Modules
 *
 * @author Pingger
 * @version 1.3.0
 * @since 0.0.1
 */
public abstract class Module
{
	/**
	 * Ensures, that the given Key is set.
	 *
	 * @param config
	 *            The {@link ConfigurationSection} to ensure the Config for
	 * @param key
	 *            the sub key
	 * @param def
	 *            the default value if not set
	 * @param desc
	 *            The Description for this node
	 */
	public static void ensureConfig(ConfigurationSection config, String key, boolean def, String desc)
	{
		if (desc != null && !desc.trim().isEmpty()) {
			config.set(key + "_Desc", desc);
		}
		config.set(key, config.getBoolean(key, def));
		config.addDefault(key, def);
	}

	/**
	 * Ensures, that the given Key is set.
	 *
	 * @param config
	 *            The {@link ConfigurationSection} to ensure the Config for
	 * @param key
	 *            the sub key
	 * @param def
	 *            the default value if not set
	 * @param desc
	 *            The Description for this node
	 */
	public static void ensureConfig(ConfigurationSection config, String key, double def, String desc)
	{
		if (desc != null && !desc.trim().isEmpty()) {
			config.set(key + "_Desc", desc);
		}
		config.set(key, config.getDouble(key, def));
		config.addDefault(key, def);
	}

	/**
	 * Ensures, that the given Key is set.
	 *
	 * @param config
	 *            The {@link ConfigurationSection} to ensure the Config for
	 * @param key
	 *            the sub key
	 * @param def
	 *            the default value if not set
	 * @param desc
	 *            The Description for this node
	 */
	public static void ensureConfig(ConfigurationSection config, String key, int def, String desc)
	{
		if (desc != null && !desc.trim().isEmpty()) {
			config.set(key + "_Desc", desc);
		}
		config.set(key, config.getInt(key, def));
		config.addDefault(key, def);
	}

	/**
	 * Ensures, that the given Key is set.
	 *
	 * @param config
	 *            The {@link ConfigurationSection} to ensure the Config for
	 *
	 * @param key
	 *            the sub key
	 * @param def
	 *            the default value if not set
	 * @param desc
	 *            The Description for this node
	 */
	public static void ensureConfig(ConfigurationSection config, String key, List<String> def, String desc)
	{
		if (desc != null && !desc.trim().isEmpty()) {
			config.set(key + "_Desc", desc);
		}
		if (!config.isList(key)) {
			config.set(key, def);
		}
		config.addDefault(key, def);
	}

	/**
	 * Ensures, that the given Key is set.
	 *
	 * @param config
	 *            The {@link ConfigurationSection} to ensure the Config for
	 * @param key
	 *            the sub key
	 * @param def
	 *            the default value if not set
	 * @param desc
	 *            The Description for this node
	 */
	public static void ensureConfig(ConfigurationSection config, String key, long def, String desc)
	{
		if (desc != null && !desc.trim().isEmpty()) {
			config.set(key + "_Desc", desc);
		}
		config.set(key, config.getLong(key, def));
		config.addDefault(key, def);
	}

	/**
	 * Ensures, that the given Key is set.
	 *
	 * @param config
	 *            The {@link ConfigurationSection} to ensure the Config for
	 * @param key
	 *            the sub key
	 * @param def
	 *            the default value if not set
	 * @param desc
	 *            The Description for this node
	 */
	public static void ensureConfig(ConfigurationSection config, String key, String def, String desc)
	{
		if (desc != null && !desc.trim().isEmpty()) {
			config.set(key + "_Desc", desc);
		}
		config.set(key, config.getString(key, def));
		config.addDefault(key, def);
	}

	/**
	 * Ensures, that the given Key is set.
	 *
	 * @param config
	 *            The {@link ConfigurationSection} to ensure the Config for
	 *
	 * @param key
	 *            the sub key
	 * @param def
	 *            the default value if not set
	 * @param desc
	 *            The Description for this node
	 */
	public static void ensureConfig(ConfigurationSection config, String key, String[] def, String desc)
	{
		if (desc != null && !desc.trim().isEmpty()) {
			config.set(key + "_Desc", desc);
		}
		if (!config.isList(key)) {
			config.set(key, def);
		}
		config.addDefault(key, def);
	}

	/**
	 * @param cfg
	 *            The {@link ConfigurationSection} to get the Sub-Section from
	 * @param key
	 *            the Key of the Configuration to get
	 * @return The Sub-Configuration for this Module
	 */
	protected static ConfigurationSection getConfigSection(ConfigurationSection cfg, String key)
	{
		ConfigurationSection cs = cfg.getConfigurationSection(key);

		if (cs == null) {
			cs = cfg.createSection(key);
		}
		return cs;
	}

	/**
	 * The ServerToolsPlugin, to be set in onEnable
	 */
	protected ServerToolsPlugin	stp;
	private boolean				debug	= false;
	private boolean				enabled	= false;
	private boolean				fine	= false;
	private boolean				logging	= true;

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
		ensureConfig(getConfig(), key, def, desc);
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
		ensureConfig(getConfig(), key, def, desc);
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
		ensureConfig(getConfig(), key, def, desc);
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
	public void ensureConfig(String key, List<String> def, String desc)
	{
		ensureConfig(getConfig(), key, def, desc);
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
		ensureConfig(getConfig(), key, def, desc);
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
		ensureConfig(getConfig(), key, def, desc);
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
		ensureConfig(getConfig(), key, def, desc);
	}

	/**
	 * @return The Configuration for this Module
	 */
	public ConfigurationSection getConfig()
	{
		if (stp != null) { return getConfigSection(stp.getConfig(), getClass().getCanonicalName()); }
		return null;
	}

	/**
	 * @return the parent {@link ServerToolsPlugin}
	 */
	public ServerToolsPlugin getServerToolsPlugin()
	{
		return stp;
	}

	/**
	 * @return if this Module is supposed to be enabled
	 */
	public final boolean isEnabled()
	{
		if (stp == null) { return false; }
		return stp.isEnabled() && enabled;
	}

	/**
	 * @return <code>true</code> if fine logging is enabled.
	 * @apiNote {@link #isLogging()} might return <code>false</code>, in which case
	 *          the {@link #fine} methods don't output
	 */
	public boolean isFineLogging()
	{
		return fine;
	}

	/**
	 * @return <code>true</code> if logging is enabled
	 */
	public boolean isLogging()
	{
		return logging;
	}

	/**
	 * Ensures that the ConfigDefaults are set
	 *
	 */
	public void loadConfigDefaults()
	{
		ensureConfig("enabled", true, null);
		ensureConfig("logging", true, null);
	}

	/**
	 * Called, when the config is reloaded
	 */
	public void onConfigReload()
	{
		// TODO call onEnable / onDisable when this changes
		enabled = getConfig().getBoolean("enabled", enabled);
		debug = getConfig().getBoolean("debug", false);
		fine = debug || getConfig().getBoolean("fine", false);
		logging = debug || getConfig().getBoolean("logging", true);
	}

	/**
	 * Run on plugin disable
	 */
	public abstract void onDisable();

	/**
	 * Run on plugin enable
	 */
	public abstract void onEnable();

	/**
	 * Run before plugin enable
	 *
	 * @param s
	 *            the current {@link ServerToolsPlugin}
	 */
	public final void setServerToolsPlugin(ServerToolsPlugin s)
	{
		if (s != null) {
			stp = s;
		}
	}

	/**
	 * Log a given line, if the 'logging' setting is true
	 *
	 * @param line
	 *            the line to log
	 */
	protected void debug(Supplier<String> line)
	{
		if (isDebug()) {
			stp.getLogger().info("[" + getClass().getSimpleName() + "] §d[DEBUG] §8" + line.get());
		}
	}

	/**
	 * Log a given line, if the 'logging' setting is true
	 *
	 * @param line
	 *            the line to log
	 */
	protected void fine(String line)
	{
		if (isLogging() && isFineLogging()) {
			stp.getLogger().info("[" + getClass().getSimpleName() + "] §8" + line);
		}
	}

	/**
	 * Log a given line, if the 'logging' setting is true. (This is intended for
	 * scenarios, where building the log message might be somewhat expansive)
	 *
	 * @param line
	 *            the Supplier for the line to log
	 */
	protected void fine(Supplier<String> line)
	{
		if (isLogging() && isFineLogging()) {
			stp.getLogger().info("[" + getClass().getSimpleName() + "] §8" + line.get());
		}
	}

	/**
	 * @param config
	 *            The {@link ConfigurationSection} to get the SubSection from
	 * @param subkey
	 *            the Subkey of the Configuration to get
	 * @return The Sub-Configuration for this Module
	 */
	protected ConfigurationSection getConfig(ConfigurationSection config, String subkey)
	{
		return getConfigSection(config, subkey);
	}

	/**
	 * @param subkey
	 *            the Subkey of the Configuration to get
	 * @return The Sub-Configuration for this Module
	 */
	protected ConfigurationSection getConfig(String subkey)
	{
		return getConfig(getConfig(), subkey);
	}

	/**
	 * @return <code>true</code> when the debug mode is enabled
	 */
	protected boolean isDebug()
	{
		return debug;
	}

	/**
	 * Log a given line, if the 'logging' setting is true
	 *
	 * @param line
	 *            the line to log
	 */
	protected void log(String line)
	{
		if (isLogging()) {
			stp.getLogger().info("[" + getClass().getSimpleName() + "] " + line);
		}
	}

	/**
	 * Log a given line, if the 'logging' setting is true
	 *
	 * @param line
	 *            the line to log
	 * @param t
	 *            the Throwable to log
	 */
	protected void log(String line, Throwable t)
	{
		if (isLogging()) {
			stp.getLogger().log(Level.INFO, "[" + getClass().getSimpleName() + "] " + line, t);
		}
	}

	/**
	 * Log a given line, if the 'logging' setting is true. (This is intended for
	 * scenarios, where building the log message might be somewhat expansive)
	 *
	 * @param line
	 *            the Supplier for the line to log
	 */
	protected void log(Supplier<String> line)
	{
		if (isLogging()) {
			stp.getLogger().info("[" + getClass().getSimpleName() + "] " + line.get());
		}
	}
}
