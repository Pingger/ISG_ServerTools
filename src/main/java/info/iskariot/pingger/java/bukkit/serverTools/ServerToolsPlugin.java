package info.iskariot.pingger.java.bukkit.serverTools;

import java.time.Duration;
import java.util.*;
import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import info.iskariot.pingger.java.bukkit.serverTools.monitor.TPSMonitor;
import info.iskariot.pingger.java.bukkit.serverTools.teams.TeamsModule;
import info.iskariot.pingger.java.bukkit.serverTools.tool.*;
import info.iskariot.pingger.java.bukkit.serverTools.tool.protection.TinyProtection;
import info.iskariot.pingger.java.bukkit.serverTools.util.ConfigParser;
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
			// Teams
			TeamsModule.class,
			// tools
			AntiBat.class,
			AntiWither.class,
			DeathInventoryDumper.class,
			EntityPerformanceTweaker.class,
			FastPregenerator.class,
			// IdlePregenerator.class,
			LootablesGenerator.class,
			PinggersTimingTest.class,
			SleepVote.class,
			VillagerAntiKill.class,
			TinyProtection.class
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

	private boolean								configLoaded		= false;

	private HashMap<String, CommandInterface>	knownSubcommands	= new HashMap<>();

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
		if (sender.isOp() && args.length > 0) {
			Duration d;
			switch (args[0].toLowerCase())
			{
				case "events":
					HandlerList.getHandlerLists().forEach(hl -> {
						for (RegisteredListener rl : hl.getRegisteredListeners()) {
							System.out
									.println(
											rl.getPlugin().getClass().getCanonicalName() + " \t" + rl.getListener().getClass().getCanonicalName()
													+ " \t" + rl.getPriority()
									);
						}
					});
					return true;

				case "tps":
					TPSMonitor.sendTPS(sender);
					sender.sendMessage("Took: " + TPSMonitor.took / 1e6);
					return true;

				case "tryload":
					try {
						Class<?> c = ServerToolsPlugin.class.getClassLoader().loadClass(args[1]);
						loadAndEnableModule((Class<? extends Module>) c);
					}
					catch (Throwable t) {
						t.printStackTrace();
					}
					return true;

				case "forcelagg":
					d = ConfigParser.loadDuration(args.length > 1 ? args[1] : null, Duration.ofSeconds(1));
					getServer().broadcastMessage("§4Admin is forcing a Lagspike in 5 Seconds with a duration of " + d.toString().substring(2));

				{
					Duration df = d;
					getServer()
							.getScheduler()
							.runTaskLater(
									this,
									() ->
									{
										try {
											Thread.sleep(df.toMillis());
										}
										catch (InterruptedException e) {
											e.printStackTrace();
										}
									},
									100
							);
				}
					return true;

				case "reload":
					reloadConfig();
					return true;

				case "shutdown":
					d = ConfigParser.loadDuration(args.length > 1 ? args[1] : null, Duration.ofMinutes(5));
					if (d.toSeconds() < 30) {
						sender.sendMessage("§4Too short duration!");
						return true;
					}
					getServer().broadcastMessage("§4A Server reboot is about to commence! Time until reboot: " + d.toString().substring(2));
					long ticksFromNow = 0;
					while (d.toHours() > 24) {
						Duration df = d.minusDays(1);
						d = df;
						ticksFromNow += 24 * 60 * 60 * 20;
						getServer()
								.getScheduler()
								.runTaskLater(
										this,
										() -> getServer()
												.broadcastMessage(
														"§4A Server reboot is about to commence! Time until reboot: " + df.toString().substring(2)
												),
										ticksFromNow
								);
					}

					while (d.toMinutes() > 60) {
						Duration df = d.minusHours(1);
						d = df;
						ticksFromNow += 60 * 60 * 20;
						getServer()
								.getScheduler()
								.runTaskLater(
										this,
										() -> getServer()
												.broadcastMessage(
														"§4A Server reboot is about to commence! Time until reboot: " + df.toString().substring(2)
												),
										ticksFromNow
								);
					}
					while (d.toMinutes() > 10) {
						Duration df = d.minusMinutes(10);
						d = df;
						ticksFromNow += 10 * 60 * 20;
						getServer()
								.getScheduler()
								.runTaskLater(
										this,
										() -> getServer()
												.broadcastMessage(
														"§4A Server reboot is about to commence! Time until reboot: " + df.toString().substring(2)
												),
										ticksFromNow
								);
					}
					while (d.toSeconds() > 60) {
						Duration df = d.minusMinutes(1);
						d = df;
						ticksFromNow += 60 * 20;
						getServer()
								.getScheduler()
								.runTaskLater(
										this,
										() -> getServer()
												.broadcastMessage(
														"§4A Server reboot is about to commence! Time until reboot: " + df.toString().substring(2)
												),
										ticksFromNow
								);
					}
					while (d.toSeconds() > 15) {
						Duration df = d.minusSeconds(10);
						d = df;
						ticksFromNow += 10 * 20;
						getServer()
								.getScheduler()
								.runTaskLater(
										this,
										() -> getServer()
												.broadcastMessage(
														"§4A Server reboot is about to commence! Time until reboot: " + df.toString().substring(2)
												),
										ticksFromNow
								);
					}
					while (d.toSeconds() > 0) {
						Duration df = d.minusSeconds(1);
						d = df;
						ticksFromNow += 20;
						getServer()
								.getScheduler()
								.runTaskLater(
										this,
										() -> getServer()
												.broadcastMessage(
														"§4A Server reboot is about to commence! Time until reboot: " + df.toString().substring(2)
												),
										ticksFromNow
								);
					}
					getServer()
							.getScheduler()
							.runTaskLater(
									this,
									() ->
									{
										getServer().broadcastMessage("§4Commencing reboot!");
										getServer().shutdown();
									},
									ticksFromNow
							);
					getServer().broadcastMessage("§4Ticks until reboot: " + ticksFromNow);
					return true;
			}
		}
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
		super.reloadConfig();
		configLoaded = true;
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
		// Touch classes, so they are loaded into memory
		info.iskariot.pingger.java.bukkit.serverTools.api.dynmap.DynmapAPII.class.getClass();
		info.iskariot.pingger.java.bukkit.serverTools.api.griefPrevention.GriefPreventionAPII.class.getClass();
		info.iskariot.pingger.java.bukkit.serverTools.monitor.TPSMonitor.class.getClass();
		info.iskariot.pingger.java.bukkit.serverTools.teams.Team.class.getClass();
		info.iskariot.pingger.java.bukkit.serverTools.teams.TeamsModule.class.getClass();
		info.iskariot.pingger.java.bukkit.serverTools.teams.events.EventListener.class.getClass();
		info.iskariot.pingger.java.bukkit.serverTools.teams.events.PlayerJoinedTeamEvent.class.getClass();
		info.iskariot.pingger.java.bukkit.serverTools.teams.events.PlayerTeamEvent.class.getClass();
		info.iskariot.pingger.java.bukkit.serverTools.teams.events.TeamEvent.class.getClass();
		info.iskariot.pingger.java.bukkit.serverTools.tool.AntiBat.class.getClass();
		info.iskariot.pingger.java.bukkit.serverTools.tool.AntiWither.class.getClass();
		info.iskariot.pingger.java.bukkit.serverTools.tool.ChunkUnloader.class.getClass();
		info.iskariot.pingger.java.bukkit.serverTools.tool.FastPregenerator.class.getClass();
		info.iskariot.pingger.java.bukkit.serverTools.tool.LootablesGenerator.class.getClass();
		info.iskariot.pingger.java.bukkit.serverTools.tool.lootableGenerator.LootableEntry.class.getClass();
		info.iskariot.pingger.java.bukkit.serverTools.tool.SleepVote.class.getClass();
		info.iskariot.pingger.java.bukkit.serverTools.tool.VillagerAntiKill.class.getClass();
		info.iskariot.pingger.java.bukkit.serverTools.tool.WebMapGenerator.class.getClass();
		info.iskariot.pingger.java.bukkit.serverTools.util.ConfigParser.class.getClass();
		info.iskariot.pingger.java.bukkit.serverTools.util.Formatting.class.getClass();

		// Done touching
		getLogger().info("Loaded!");
		super.reloadConfig();
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
	{
		// TODO Auto-generated method stub
		return super.onTabComplete(sender, command, alias, args);
	}

	@Override
	public void reloadConfig()
	{
		if (configLoaded) {
			getServer().getScheduler().runTaskAsynchronously(this, () -> {
				super.reloadConfig();
				loadedModules.forEach(m -> {
					try {
						m.onConfigReload();
					}
					catch (Throwable t) {
						t.printStackTrace();
					}
				});
				super.saveConfig();
			});
		}
	}
}
