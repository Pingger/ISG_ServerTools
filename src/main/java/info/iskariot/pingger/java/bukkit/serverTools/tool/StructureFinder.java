package info.iskariot.pingger.java.bukkit.serverTools.tool;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.StructureType;
import org.bukkit.block.Banner;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import info.iskariot.pingger.java.bukkit.serverTools.Module;

/**
 * @author Pingger
 * @version 0.0.1
 * @since 2020-11-26
 */
public class StructureFinder extends Module
{

	private Plugin									dynmapPlugin;
	private HashMap<StructureType, Object>			dynmapSets		= new HashMap<>();
	private YamlConfiguration						markersConfig;
	private File									markersYaml;
	private HashMap<StructureType, Set<Location>>	StructureSets	= new HashMap<>();

	@Override
	public void loadConfigDefaults()
	{
		super.loadConfigDefaults();
		ConfigurationSection strcfg = getConfig("structures");
		for (StructureType st : StructureType.getStructureTypes().values()) {
			ConfigurationSection cs = getConfig(strcfg, st.getName().replace(" ", ""));
			ensureConfig(
					cs,
					"find",
					true,
					"Should Markers for this type of structure be found? This should always be true. If you want users to not see markers for a specific structure you can choose to hide them with the show-setting"
			);
			ensureConfig(
					cs,
					"show",
					true,
					"If Markers of this structure Type should be visible on the map. If false, the entire Set will be hidden from the map can't be made visible from the Web-UI."
			);
			ensureConfig(cs, "log", false, "If true, this will create a message in the Server-console/-log. This might be helpful");
			ensureConfig(cs, "minzoom", true, "The minimum zoomlevel for icons to appear on the dynmap");
			ensureConfig(cs, "maxzoom", true, "The maximum zoomlevel for icons to appear on the dynmap");
			ensureConfig(
					cs,
					"visibleByDefault",
					false,
					"Should the markers be shown by default on the map? (Sets the default for the checkbox in the top-left corner of the web-ui)"
			);
			ensureConfig(cs, "setLabel", st.getName() + "s", "The Label shown in the Web-UI for this Set");
			ensureConfig(cs, "markerLabel", st.getName(), "The Label shown at each marker on the Map");
			ensureConfig(cs, "coordsInDescription", false, "Put the exakt Coordinates in the Description of each marker");
			ensureConfig(
					cs,
					"previousSetLabel",
					st.getName() + "s",
					"If you changed the setLabel value, this ensures that the previous set is renamed and the progress maintained."
			);
			ensureConfig(
					cs,
					"previousMarkerLabel",
					st.getName(),
					"If you changed the markerLabel value, this ensures the previous markers are renamed properly"
			);
			Banner b;

		}
	}

	@Override
	public void onDisable()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onEnable()
	{
		dynmapPlugin = stp.getServer().getPluginManager().getPlugin("dynmap");
		markersYaml = new File(stp.getDataFolder(), "structures.yml");
		if (dynmapPlugin != null) {
			try {
				//				dynmap_triggerRenderOfVolume = dynmapPlugin
				//						.getClass()
				//						.getMethod("triggerRenderOfVolume", String.class, int.class, int.class, int.class, int.class, int.class, int.class);
			}
			/*
			 * catch (NoSuchMethodException e) {
			 * log("Dynmap detected, but API endpoint is missing!");
			 * dynmapPlugin = null;
			 * }
			 */
			catch (SecurityException e) {
				log("Dynmap detected, but API endpoint is protected by SecurityManager!", e);
				dynmapPlugin = null;
			}
			log("Dynmap detected!");
		}
		reloadMarkers();
	}

	private void reloadMarkers()
	{
		if (markersConfig != null) {
			try {
				markersConfig.save(markersYaml);
			}
			catch (IOException e) {
				log("Failed saving markers.yml", e);
			}
		}
		if (markersYaml.isFile() && markersYaml.canRead()) {
			markersConfig = YamlConfiguration.loadConfiguration(markersYaml);
		}
	}
}
