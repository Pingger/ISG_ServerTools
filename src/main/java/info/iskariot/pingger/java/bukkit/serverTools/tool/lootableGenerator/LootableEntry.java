package info.iskariot.pingger.java.bukkit.serverTools.tool.lootableGenerator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;

import info.iskariot.pingger.java.bukkit.serverTools.Module;
import info.iskariot.pingger.java.bukkit.serverTools.tool.LootablesGenerator;

/**
 *
 * @author Pingger
 *
 */
public class LootableEntry
{
	private Set<Location>				locations	= new HashSet<>();
	private final String				name;
	private final LootablesGenerator	parent;

	/**
	 *
	 * @param name
	 *            of this entry
	 * @param p
	 *            parent Generator
	 */
	public LootableEntry(String name, LootablesGenerator p)
	{
		this.name = name;
		parent = p;
		ensureDefaults();
		p.schedule(this, p.getEntryConfig("name").getInt("interval", 60));
	}

	/**
	 * called, when the cron job does the next iteration of loot spawning
	 */
	public void onUpdateLoot()
	{
		try {
			if (!parent.getEntryConfig(name).getBoolean("enabled", false)) { return; }
			cleanupLootchests();
			ArrayList<String> tables = createLootProbabilityList();
			Random r = new Random();
			tryPopulateLocations(l -> generateLootContainer(l, tables, r));
			getConfig().set("locations", new ArrayList<>(locations));
			parent.getServerToolsPlugin().saveConfig();
		}
		finally {
			parent.schedule(this, getConfig().getInt("interval"));
		}
	}

	/**
	 * <ol>
	 * <li>if the locations set is empty, populate.</li>
	 * <li>for each location, if chest, make air</li>
	 * </ol>
	 */
	protected void cleanupLootchests()
	{
		if (locations.isEmpty()) {
			tryPopulateLocations(l -> {
				;
			});
		}
		for (Location l : locations) {
			if (l.getBlock().getType() == Material.CHEST) {
				l.getBlock().setBlockData(parent.getServerToolsPlugin().getServer().createBlockData(Material.AIR));
			}
		}
	}

	/**
	 * Builds the lootprobability list
	 *
	 * @return the lootprobability list
	 */
	protected ArrayList<String> createLootProbabilityList()
	{
		ArrayList<String> tables = new ArrayList<>();
		parent.getServerToolsPlugin().reloadConfig();
		for (String t : getConfig().getStringList("tables")) {
			String category = t.split(":", 2)[0];
			String prob = t.substring(t.lastIndexOf(':'));
			String table = t.substring(category.length() + 1, t.lastIndexOf(':'));
			int p = Integer.parseInt(prob.trim());
			for (int i = 0; i < p; i++) {
				tables.add(category.trim() + ":" + table.trim());
			}
		}
		return tables;
	}

	/**
	 * ensures the default values
	 */
	protected void ensureDefaults()
	{
		ConfigurationSection cs = getConfig();
		cs.set("enabled", false);
		Module.ensureConfig(cs, "enabled", false, null);
		Module.ensureConfig(cs, "tables", new String[] {
				"mc:abandoned_mineshaft: 100",
				"mc:bastion_bridge: 25",
				"mc:bastion_hoglin_stable: 25",
				"mc:bastion_other: 25",
				"mc:bastion_treasure: 5",
				"mc:buried_treasure: 5",
				"mc:desert_pyramid: 100",
				"mc:end_city_treasure: 100",
				"mc:igloo_chest: 100",
				"mc:jungle_temple: 100",
				"mc:jungle_temple_dispenser: 100",
				"mc:nether_bridge: 100",
				"mc:pillager_outpost: 100",
				"mc:ruined_portal: 100",
				"mc:shipwreck_map: 0",
				"mc:shipwreck_supply: 100",
				"mc:shipwreck_treasure: 25",
				"mc:simple_dungeon: 25",
				"mc:spawn_bonus_chest: 100",
				"mc:stronghold_corridor: 100",
				"mc:stronghold_crossing: 100",
				"mc:stronghold_library: 100",
				"mc:underwater_ruin_big: 25",
				"mc:underwater_ruin_small: 100",
				"mc:woodland_mansion: 100",
				"mc:village: 100",
				"mc:village_armorer: 100",
				"mc:village_butcher: 100",
				"mc:village_cartographer: 100",
				"mc:village_mason: 100",
				"mc:village_shepherd: 100",
				"mc:village_tannery: 100",
				"mc:village_weaponsmith: 100",
				"mc:village_desert_house: 100",
				"mc:village_plains_house: 100",
				"mc:village_savanna_house: 100",
				"mc:village_snowy_house: 100",
				"mc:village_taiga_house: 100",
				"mc:village_fisher: 100",
				"mc:village_fletcher: 100",
				"mc:village_temple: 25",
				"mc:village_toolsmith: 100"
		}, "The Loottables with probabilities, which one to pick from. High Number -> high probability");
		Module.ensureConfig(cs, "locations", new String[] {
				"world:0:0:0",
				"world_nether:32:64:96"
		}, "Coordinates, worldname:x:y:z");
		Module.ensureConfig(cs, "intervalMin", 60, "Interval in minutes to regenerate the loot");
		Module.ensureConfig(cs, "defend", true, "Should enemies be spawned, the first time the chest is opened?");
		Module.ensureConfig(cs, "defense", new String[] {
				"SKELETON", "SKELETON", "SKELETON", "SKELETON"
		}, "List of mobs to spawn to defend");
	}

	/**
	 * Generates and fills a Chest with a random loottable from the loottable list
	 *
	 * @param l
	 *            the location
	 * @param tables
	 *            the loottable list
	 * @param r
	 *            the RNG
	 */
	protected void generateLootContainer(Location l, ArrayList<String> tables, Random r)
	{
		l.getBlock().setType(Material.CHEST);
		l.getBlock().setBlockData(parent.getServerToolsPlugin().getServer().createBlockData(Material.CHEST));
		Chest c = (Chest) l.getBlock().getState();
		String table = tables.get(r.nextInt(tables.size()));
		if (table.startsWith("mc:")) {
			LootTable lt = parent
					.getServerToolsPlugin()
					.getServer()
					.getLootTable(NamespacedKey.minecraft(table.substring(3)));
			if (lt != null) {
				lt.fillInventory(c.getBlockInventory(), r, new LootContext.Builder(l).build());
			}
			else {
				parent.warn("Can't find LootTable: " + table.substring(3));
			}
		}
		parent.warn("Can't handle LootTable: " + table);
	}

	private ConfigurationSection getConfig()
	{
		return parent.getEntryConfig(name);
	}

	private void tryPopulateLocations(Consumer<Location> con)
	{
		locations.clear();
		for (String ls : getConfig().getStringList("locations")) {
			Location l = null;
			try {
				String[] s = ls.split(":", 4);
				l = new Location(
						parent.getServerToolsPlugin().getServer().getWorld(s[0].trim()), Integer.parseInt(s[1]), Integer.parseInt(s[2]),
						Integer.parseInt(s[3])
				);
				locations.add(l);
				con.accept(l);
			}
			catch (Exception exc) {
				if (l != null) {
					locations.remove(l);
				}
				parent.warn("Removed location: " + ls + "! Reason: " + exc.getMessage());
			}
		}
	}
}
