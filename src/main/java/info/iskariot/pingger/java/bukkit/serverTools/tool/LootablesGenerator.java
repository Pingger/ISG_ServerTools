package info.iskariot.pingger.java.bukkit.serverTools.tool;

import org.bukkit.configuration.ConfigurationSection;

import info.iskariot.pingger.java.bukkit.serverTools.Module;
import info.iskariot.pingger.java.bukkit.serverTools.ServerToolsPlugin;

/**
 *
 * @author Pingger
 *
 */
public class LootablesGenerator extends Module
{
	private static final String	CONFIG_AREAS			= ServerToolsPlugin.buildKey(LootablesGenerator.class, "areas");
	private static final String	CONFIG_SAVED_INVETORIES	= ServerToolsPlugin.buildKey(LootablesGenerator.class, "storedChests");
	private static final String	CONFIG_TABLES			= ServerToolsPlugin.buildKey(LootablesGenerator.class, "lootTables");

	@Override
	public void loadConfigDefaults()
	{
		ensureConfig("enabled", false, null);
		ensureConfig("logging", true, null);
		ensureConfig(CONFIG_SAVED_INVETORIES, new String[] {
				"0:0:0"
		}, "Remembers created loot-chests for after restarts. (e.g. for auto delete)");
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
		if (!getConfig().isConfigurationSection(CONFIG_AREAS)) {
			getConfig().createSection(CONFIG_AREAS);
		}

		ConfigurationSection s = getConfig().getConfigurationSection(CONFIG_AREAS);
		s.set("exampleArea.enabled", false);
		s.set("exampleArea.type", "area");
		s.set("exampleArea.areaStart", "0:0:0");
		s.set("exampleArea.areaEnd", "0:0:0");
		s.set("exampleArea.minCount", "0");
		s.set("exampleArea.maxCount", "0");
		s.set("exampleArea.regenerateInterval", "1h");
		s.set("exampleArea.autoDeleteOld", true);
		s.set("exampleArea.autoDeleteOldTimeout", "2h");
		s.set("exampleArea.alignToRTC", true);
		s.set("exampleArea.rememberedChests", new String[0]);

	}

	@Override
	public void onDisable()
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void onEnable()
	{
		//stp.getServer().getLootTable(null).fillInventory(inventory, random, context);
		// TODO Auto-generated method stub

	}

}
