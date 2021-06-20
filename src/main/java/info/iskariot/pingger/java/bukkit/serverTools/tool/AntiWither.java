package info.iskariot.pingger.java.bukkit.serverTools.tool;

import java.util.*;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;

import info.iskariot.pingger.java.bukkit.serverTools.Module;
import info.iskariot.pingger.java.bukkit.serverTools.util.Formatting;

/**
 * Prevents Wither Spawning in specified Worlds
 *
 * @author Pingger
 * @since 0.0.5
 */
public class AntiWither extends Module implements Listener
{
	private Set<String>	worldsByName	= new HashSet<>();
	private Set<UUID>	worldsByUUID	= new HashSet<>();

	@Override
	public void loadConfigDefaults()
	{
		super.loadConfigDefaults();
		ensureConfig("worldsByName", new String[] {
				"world"
		}, null);
		ensureConfig("worldsByUUID", new String[] {}, null);
	}

	/**
	 * Interrupts placing Skulls to prevent loosing them
	 *
	 * @param bpe
	 *            {@link BlockPlaceEvent}
	 */
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent bpe)
	{
		if (isEnabled()) {
			if (bpe.getBlock().getLocation().getBlockY() > 2
					&& Material.WITHER_SKELETON_SKULL.equals(bpe.getBlock().getType())
					&& Material.SOUL_SAND.equals(bpe.getBlock().getRelative(0, -1, 0).getType())
					&& (worldsByUUID.contains(bpe.getBlock().getLocation().getWorld().getUID())
							|| worldsByName.contains(bpe.getBlock().getWorld().getName().toLowerCase())))
			{
				bpe.setCancelled(true);
				log("Prevented Wither Skull at " + Formatting.format(bpe.getBlock().getLocation()) + " by " + bpe.getPlayer());
				Player p = bpe.getPlayer();
				if (p != null) {
					p.sendMessage("§4You may not place a Wither Skull on top of Soulsand in this world!");
				}
			}
		}
	}

	@Override
	public void onConfigReload()
	{
		super.onConfigReload();
		stp.getServer().getScheduler().runTaskLater(stp, () -> doConfigReload(), 0);

	}

	@Override
	public void onDisable()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onEnable()
	{
		doConfigReload();
		stp.getServer().getPluginManager().registerEvents(this, stp);
	}

	/**
	 * Interrupts Wither Spawning entirely, in case someone tries to circumvent the
	 * Block Placement interrupt
	 *
	 * @param cse
	 *            {@link CreatureSpawnEvent}
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onWitherSpawn(CreatureSpawnEvent cse)
	{
		if (isEnabled()) {
			if (EntityType.WITHER.equals(cse.getEntityType())
					&& (worldsByUUID.contains(cse.getLocation().getWorld().getUID())
							|| worldsByName.contains(cse.getLocation().getWorld().getName().toLowerCase())))
			{
				cse.setCancelled(true);
				stp.getServer().broadcastMessage("§4Someone tried to circumvent the Wither-Spawn-Protection!");
				stp.getServer().broadcastMessage("World:          " + cse.getLocation().getWorld().getName());
				stp.getServer().broadcastMessage("Location:       " + Formatting.formatShortLocation(cse.getLocation()));
				stp.getServer().broadcastMessage("SpawnReason:    " + cse.getSpawnReason());
				Collection<Entity> playersCol = cse
						.getLocation()
						.getWorld()
						.getNearbyEntities(cse.getLocation(), 256, 256, 256, e -> EntityType.PLAYER.equals(e.getType()));
				ArrayList<Entity> players = new ArrayList<>(playersCol);
				players
						.sort(
								(a, b) -> Double
										.compare(
												a.getLocation().distanceSquared(cse.getLocation()),
												b.getLocation().distanceSquared(cse.getLocation())
										)
						);
				stp.getServer().broadcastMessage("Nearest Player: " + (players.isEmpty() ? "§iNone" : players.get(0).getName()));
			}
		}
	}

	private void doConfigReload()
	{
		worldsByName.clear();
		worldsByUUID.clear();
		List<String> list = getConfig().getStringList("worldsByName");
		list.forEach(s -> worldsByName.add(s));
		list = getConfig().getStringList("worldsByUUID");
		list.forEach(s -> {
			try {
				worldsByUUID.add(UUID.fromString(s));
			}
			catch (Exception e) {
				// ignore
			}
		});
		getConfig().set("worldsByName", worldsByName.toArray());
		getConfig().set("worldsByUUID", worldsByUUID.parallelStream().map(u -> u.toString()).collect(Collectors.toList()).toArray());
	}
}
