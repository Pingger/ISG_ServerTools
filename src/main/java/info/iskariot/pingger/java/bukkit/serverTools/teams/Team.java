package info.iskariot.pingger.java.bukkit.serverTools.teams;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import info.iskariot.pingger.java.bukkit.serverTools.Module;
import info.iskariot.pingger.java.bukkit.serverTools.teams.events.PlayerJoinedTeamEvent;

/**
 *
 * @author Pingger
 *
 */
public class Team
{
	private static SimpleDateFormat	sdf	= new SimpleDateFormat("HH:mm:ss");
	private Set<OfflinePlayer>		members;
	private String					name;
	private final TeamsModule		parent;
	private Location				spawnLocation;

	/**
	 * Creates a Team
	 *
	 * @param tm
	 *            parent TeamsModule
	 * @param name
	 *            the Teamname
	 */
	public Team(TeamsModule tm, String name)
	{
		parent = tm;
		members = new HashSet<>();
		this.name = name;
		ensureConfig();
		reloadConfig();
		updateTeam();
	}

	/**
	 * Adds the given Player to this Team
	 *
	 * @param p
	 *            the Player to add
	 */
	public void addMember(OfflinePlayer p)
	{
		if (members.add(p)) {
			parent.fire(new PlayerJoinedTeamEvent(this, p));
			getBukkitTeam().addEntry(p.getName());
		}
	}

	/**
	 *
	 * @return TextChat Formatting code-char. Basically [0-9a-f] depending on which
	 *         color
	 */
	public char getTextColor()
	{
		return getConfig().getString("colorCode").charAt(0);
	}

	/**
	 * Whether the given Player is part of this Team
	 *
	 * @param p
	 *            the Player to test
	 * @return <code>true</code> if the player is part of this Team
	 */
	public boolean isPartOfTeam(OfflinePlayer p)
	{
		return members.contains(p) || members.parallelStream().anyMatch(op -> op.getUniqueId().equals(p.getUniqueId()));
	}

	/**
	 *
	 * @param apce
	 *            {@link AsyncPlayerChatEvent}
	 */
	public void onPlayerChat(AsyncPlayerChatEvent apce)
	{
		if (isPartOfTeam(apce.getPlayer())) {
			apce
					.setFormat(
							"[" + sdf.format(Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00")).getTime()) + "] [§" + getTextColor() + name
									+ "§r] %s: %s"
					);
		}
	}

	/**
	 *
	 * @param pde
	 *            {@link PlayerDeathEvent}
	 */
	public void onPlayerDeath(PlayerDeathEvent pde)
	{
		if (isPartOfTeam(pde.getEntity())) {
			if (pde.getEntity().getBedSpawnLocation() == null) {
				if (spawnLocation != null) {
					pde.getEntity().setBedSpawnLocation(spawnLocation, true);
				}
			}
		}
	}

	/**
	 * Called when the TeamsModule receives a PlayerJoinEvent
	 *
	 * @param pje
	 *            {@link PlayerJoinEvent}
	 */
	public void onPlayerJoin(PlayerJoinEvent pje)
	{
		updateTeam();
		if (isPartOfTeam(pje.getPlayer())) {
			org.bukkit.scoreboard.Team t = getBukkitTeam();
			t.addEntry(pje.getPlayer().getName());
			pje
					.setJoinMessage(
							"§" + getTextColor() + pje.getPlayer().getName() + "§r joined and is part of " + "§" + getTextColor()
									+ name + "§r"
					);
			pje.getPlayer().setWhitelisted(true);
			pje.getPlayer().setCustomName("§" + getTextColor() + pje.getPlayer().getName() + "§r");
			pje.getPlayer().setCustomNameVisible(true);

			if (pje.getPlayer().getBedSpawnLocation() == null) {
				if (spawnLocation != null) {
					pje.getPlayer().setBedSpawnLocation(spawnLocation, true);
				}
			}
		}
	}

	/**
	 * Called when the TeamsModule receives a PlayerJoinEvent
	 *
	 * @param ple
	 *            {@link PlayerLoginEvent}
	 */
	public void onPlayerLogin(PlayerLoginEvent ple)
	{
		if (isPartOfTeam(ple.getPlayer())) {
			parent.getServerToolsPlugin().getServer().getWhitelistedPlayers().add(ple.getPlayer());
			ple.getPlayer().setWhitelisted(true);
			ple.allow();

			if (ple.getPlayer().getBedSpawnLocation() == null) {
				if (spawnLocation != null) {
					ple.getPlayer().setBedSpawnLocation(spawnLocation, true);
				}
			}
		}
	}

	/**
	 * (re-)Loads the config
	 */
	public void reloadConfig()
	{
		//parent.getServerToolsPlugin().getServer().getO
		ConfigurationSection cs = getConfig();
		String[] loc = cs.getString("spawnlocation", "world:0:64:0").split(":");
		int x = Integer.parseInt(loc[1]);
		int y = Integer.parseInt(loc[2]);
		int z = Integer.parseInt(loc[3]);
		World w = parent.getServerToolsPlugin().getServer().getWorld(loc[0]);
		try {
			w = w == null ? parent.getServerToolsPlugin().getServer().getWorld(UUID.fromString(loc[0])) : w;
		}
		catch (IllegalArgumentException ignore) {
		}
		spawnLocation = w == null ? null : w.getBlockAt(x, y, z).getLocation();
		if (spawnLocation == null) {
			parent.log("Bad Spawnlocation! " + cs.getString("spawnlocation"));
		}
		else {
			spawnLocation = spawnLocation.add(0.5, 0.5, 0.5);
		}
		List<String> ms = cs.getStringList("members");
		ms.forEach(m -> members.add(parent.getServerToolsPlugin().getServer().getOfflinePlayer(UUID.fromString(m))));
		updateTeam();
	}

	/**
	 * ensures the given Config-Values exist in the config
	 */
	protected void ensureConfig()
	{
		Module.ensureConfig(getConfig(), "members", new ArrayList<String>(), null);
		Module.ensureConfig(getConfig(), "spawnlocation", "world:0:64:0", null);
		Module.ensureConfig(getConfig(), "colorCode", "f", null);
		Module.ensureConfig(getConfig(), "teamColorCode", "f", null);
		Module.ensureConfig(getConfig(), "canSeeFriendlyInvisibles", true, null);
		Module.ensureConfig(getConfig(), "friendlyFire", true, null);
		Module.ensureConfig(getConfig(), "nameTagVisibility", "FOR_OWN_TEAM", "ALWAYS, FOR_OWN_TEAM, FOR_OTHER_TEAMS, NEVER");
	}

	/**
	 * @return the Bukkit {@link org.bukkit.scoreboard.Team}
	 */
	protected org.bukkit.scoreboard.Team getBukkitTeam()
	{
		org.bukkit.scoreboard.Team t = parent.getServerToolsPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam(name);
		if (t == null) {
			t = parent.getServerToolsPlugin().getServer().getScoreboardManager().getMainScoreboard().registerNewTeam(name);
			updateTeam();
		}
		return t;
	}

	/**
	 * @return this Teams Config
	 */
	protected ConfigurationSection getConfig()
	{
		return parent.getTeamConfig(name);
	}

	/**
	 *
	 */
	protected void updateTeam()
	{
		org.bukkit.scoreboard.Team t = getBukkitTeam();
		t.setColor(ChatColor.getByChar(getConfig().getString("teamColorCode").charAt(0)));
		t.setCanSeeFriendlyInvisibles(getConfig().getBoolean("canSeeFriendlyInvisibles"));
		t.setAllowFriendlyFire(getConfig().getBoolean("friendlyFire"));
		t.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.valueOf(getConfig().getString("nameTagVisibility", "FOR_OWN_TEAM")));
	}
}
