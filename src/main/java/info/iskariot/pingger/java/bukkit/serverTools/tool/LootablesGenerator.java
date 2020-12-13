package info.iskariot.pingger.java.bukkit.serverTools.tool;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.configuration.ConfigurationSection;

import info.iskariot.pingger.java.bukkit.serverTools.Module;
import info.iskariot.pingger.java.bukkit.serverTools.tool.lootableGenerator.LootableEntry;

/**
 *
 * @author Pingger
 *
 */
public class LootablesGenerator extends Module
{

	private HashMap<LootableEntry, Long>	lastRun				= new HashMap<>();
	private HashMap<LootableEntry, Integer>	scheduledEntries	= new HashMap<>();

	/**
	 * The repeating cron job
	 */
	public void cron()
	{
		ArrayList<LootableEntry> es = new ArrayList<>(scheduledEntries.keySet());
		es.stream().forEach(e -> {
			if (!lastRun.containsKey(e) || lastRun.get(e) != System.currentTimeMillis() / scheduledEntries.get(e) * 60 * 1000) {
				e.onUpdateLoot();
				lastRun.put(e, System.currentTimeMillis() / scheduledEntries.get(e) * 60 * 1000);
			}
		});
		stp.getServer().getScheduler().runTaskLater(stp, () -> cron(), 200);
	}

	/**
	 *
	 * @param name
	 *            the of the LootableEntry
	 * @return The config for the given LootableEntry
	 */
	public ConfigurationSection getEntryConfig(String name)
	{
		return getConfig(getLootablesConfig(), name);
	}

	@SuppressWarnings("unused")
	@Override
	public void loadConfigDefaults()
	{
		ensureConfig("enabled", false, null);
		ensureConfig("logging", true, null);
		new LootableEntry("example", this);
		/*
		 * plg.ensureConfig(CONFIG_AREAS, new String[]
		 * {"0:0:0to0:0:0#mincnt:0,maxcnt:0,minfreq:0,maxfreq:0"}, null);
		 *
		 * plg.ensureConfig(CONFIG_TABLES, new String[] {
		 * "abandoned_mineshaft: 100",
		 * "bastion_bridge: 25",
		 * "bastion_hoglin_stable: 25",
		 * "bastion_other: 25",
		 * "bastion_treasure: 5",
		 * "buried_treasure: 5",
		 * "desert_pyramid: 100",
		 * "end_city_treasure: 100",
		 * "igloo_chest: 100",
		 * "jungle_temple: 100",
		 * "jungle_temple_dispenser: 100",
		 * "nether_bridge: 100",
		 * "pillager_outpost: 100",
		 * "ruined_portal: 100",
		 * "shipwreck_map: 0",
		 * "shipwreck_supply: 100",
		 * "shipwreck_treasure: 25",
		 * "simple_dungeon: 25",
		 * "spawn_bonus_chest: 100",
		 * "stronghold_corridor: 100",
		 * "stronghold_crossing: 100",
		 * "stronghold_library: 100",
		 * "underwater_ruin_big: 25",
		 * "underwater_ruin_small: 100",
		 * "woodland_mansion: 100",
		 * "village: 100",
		 * "village_armorer: 100",
		 * "village_butcher: 100",
		 * "village_cartographer: 100",
		 * "village_mason: 100",
		 * "village_shepherd: 100",
		 * "village_tannery: 100",
		 * "village_weaponsmith: 100",
		 * "village_desert_house: 100",
		 * "village_plains_house: 100",
		 * "village_savanna_house: 100",
		 * "village_snowy_house: 100",
		 * "village_taiga_house: 100",
		 * "village_fisher: 100",
		 * "village_fletcher: 100",
		 * "village_temple: 100",
		 * "village_toolsmith: 100"
		 * }, null);
		 */

		//		ConfigurationSection s = getConfig().getConfigurationSection(CONFIG_AREAS);
		//		s.set("exampleArea.enabled", false);
		//		s.set("exampleArea.type", "area");
		//		s.set("exampleArea.areaStart", "0:0:0");
		//		s.set("exampleArea.areaEnd", "0:0:0");
		//		s.set("exampleArea.minCount", "0");
		//		s.set("exampleArea.maxCount", "0");
		//		s.set("exampleArea.regenerateInterval", "1h");
		//		s.set("exampleArea.autoDeleteOld", true);
		//		s.set("exampleArea.autoDeleteOldTimeout", "2h");
		//		s.set("exampleArea.alignToRTC", true);
		//		s.set("exampleArea.rememberedChests", new String[0]);

	}

	@Override
	public void onDisable()
	{
	}

	@Override
	public void onEnable()
	{
		//stp.getServer().getLootTable(null).fillInventory(inventory, random, context);
		ConfigurationSection lootables = getLootablesConfig();
		for (String label : lootables.getKeys(false)) {
			super.log("Loading LootablesGenerator Job: " + label);
			lastRun.put(new LootableEntry(label, this), 0l);
		}
		stp.getServer().getScheduler().runTaskLater(stp, () -> cron(), 0);
	}

	/**
	 * schedules a {@link LootableEntry}
	 *
	 * @param lootableEntry
	 *            the Entry to schedule
	 * @param interval
	 *            the interval in minutes
	 */
	public void schedule(LootableEntry lootableEntry, int interval)
	{
		if (interval <= 0) {
			scheduledEntries.put(lootableEntry, 1);
			return;
		}
		scheduledEntries.put(lootableEntry, interval);
	}

	/**
	 * Sends a warning message
	 *
	 * @param message
	 *            the warning message
	 */
	public void warn(String message)
	{
		log("[WARN] " + message);
	}

	/**
	 * @return the {@link ConfigurationSection} for the Lootables
	 */
	protected ConfigurationSection getLootablesConfig()
	{
		return getConfig("lootables");
	}

}
