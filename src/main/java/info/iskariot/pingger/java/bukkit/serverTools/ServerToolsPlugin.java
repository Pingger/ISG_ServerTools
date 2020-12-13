package info.iskariot.pingger.java.bukkit.serverTools;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import info.iskariot.pingger.java.bukkit.serverTools.monitor.TPSMonitor;
import info.iskariot.pingger.java.bukkit.serverTools.teams.TeamsModule;
import info.iskariot.pingger.java.bukkit.serverTools.tool.FastPregenerator;
import info.iskariot.pingger.java.bukkit.serverTools.tool.LootablesGenerator;
import info.iskariot.pingger.java.bukkit.serverTools.tool.SleepVote;
import info.iskariot.pingger.java.bukkit.serverTools.tool.VillagerAntiKill;
import info.iskariot.pingger.java.bukkit.serverTools.util.Formatting;

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

	private HashMap<String, CommandInterface> knownSubcommands = new HashMap<>();

	/**
	 * Enables the given Module
	 *
	 * @param m
	 *            the Module to enable
	 */
	public void enableModule(Module m)
	{
		getLogger().info(Formatting.FC_DARK_YELLOW + "Enabling:     §r" + m.getClass().getCanonicalName());
		m.setServerToolsPlugin(this);
		m.loadConfigDefaults();
		m.onEnable();
		if (m instanceof CommandInterface) {
			String[] labels = ((CommandInterface) m).getLabel();
			if (labels != null) {
				for (String l : labels) {
					knownSubcommands.put(l.toLowerCase().intern(), (CommandInterface) m);
				}
			}
		}
		loadedModules.add(m);
		getLogger().info(Formatting.FC_DARK_GREEN + "Enabled:      §r" + m.getClass().getCanonicalName());
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

	/**
	 *
	 * @param mc
	 *            the class of Moudle to load and possibly enable
	 */
	public void loadAndEnableModule(Class<? extends Module> mc)
	{
		try {
			getLogger().info(Formatting.FC_DARK_YELLOW + "Constructing: §r" + mc.getCanonicalName());
			Module m = mc.getConstructor().newInstance();
			m.setServerToolsPlugin(this);
			m.loadConfigDefaults();
			if (m.getConfig().getBoolean("enabled", true)) {
				enableModule(m);
			}
		}
		catch (IllegalStateException ise) {
			getLogger().log(Level.SEVERE, Formatting.FC_DARK_RED + "Module couldn't be loaded!" + ise.toString());
		}
		catch (Throwable e) {
			getLogger().log(Level.SEVERE, Formatting.FC_DARK_RED + "Module couldn't be loaded!", e);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		// TODO Auto-generated method stub
		return super.onCommand(sender, command, label, args);
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
		knownSubcommands.clear();
		reloadConfig();
		saveConfig();
		for (Class<? extends Module> mc : modules) {
			loadAndEnableModule(mc);
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

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
	{
		// TODO Auto-generated method stub
		return super.onTabComplete(sender, command, alias, args);
	}
}
