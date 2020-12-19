package info.iskariot.pingger.java.bukkit.serverTools.tool;

import java.util.*;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.plugin.RegisteredListener;

import info.iskariot.pingger.java.bukkit.serverTools.Module;

/**
 *
 * @author Pingger
 *
 */
public class VillagerAntiKill extends Module implements Listener
{

	private int				minVillagers	= 0;

	private double			radius			= 0;

	private HashSet<UUID>	resets			= new HashSet<>();

	@Override
	public void loadConfigDefaults()
	{
		ensureConfig("enabled", true, null);
		ensureConfig("logging", false, null);
		ensureConfig("minVillagers", 4, "Required Amount of Villagers in the Area to allow damage");
		ensureConfig("checkRadius", 24, "The radius to search for other villagers. Higher Values WILL affect performance");
	}

	@Override
	public void onConfigReload()
	{
		super.onConfigReload();
		List<String> uuids = getConfig().getStringList("resetVillagers");
		for (String uuid : uuids) {
			resets.add(UUID.fromString(uuid));
		}
		getConfig().set("resetVillagers", resets.stream().map(u -> u.toString()).toArray());
		radius = getConfig().getDouble("radius");
		minVillagers = getConfig().getInt("minVillagers");
	}

	@Override
	public void onDisable()
	{

	}

	@Override
	public void onEnable()
	{
		stp.getServer().getPluginManager().registerEvents(this, stp);
		List<String> uuids = getConfig().getStringList("resetVillagers");
		for (String uuid : uuids) {
			resets.add(UUID.fromString(uuid));
		}
	}

	/**
	 * On Damage to Villager, cancel that damage!
	 *
	 * @param ede
	 *            EntityDamageEvent
	 */
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onEntityDamageEvent(EntityDamageEvent ede)
	{
		if (isEnabled()) {
			if (ede.getEntityType() != null && ede.getEntityType().equals(EntityType.VILLAGER)) {
				Location l = ede.getEntity().getLocation();
				double r = radius;
				if (l.getWorld().getNearbyEntities(l, r, r, r, e -> e.getType().equals(EntityType.VILLAGER)).size() < minVillagers) {
					log(
							() -> "Cancelling Damage to " + ede.getEntity().toString() + "; Cause: " + ede.getCause().toString() + "; Amount: "
									+ ede.getDamage()
					);
					ede.setCancelled(true);
					ede.setDamage(0);
				}
				if (isDebug()) {
					for (RegisteredListener rl : EntityDamageEvent.getHandlerList().getRegisteredListeners()) {
						debug(
								() -> "Listener: " + rl.getPlugin().getClass().getCanonicalName() + " -> "
										+ rl.getListener().getClass().getCanonicalName()
						);
					}
				}
				Villager v = (Villager) ede.getEntity();
				if (v.getVillagerExperience() == 1) {
					if (!resets.contains(v.getUniqueId())) {
						v.setVillagerExperience(0);
						resets.add(v.getUniqueId());
						stp.reloadConfig();
					}
				}
			}
		}
	}

	/**
	 * On Anything targets Villager, cancel!
	 *
	 * @param ete
	 *            EntityTargetEvent
	 */
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onEntityTargetEvent(EntityTargetEvent ete)
	{
		if (isEnabled()) {
			if (ete.getTarget() != null && ete.getTarget().getType() != null && ete.getTarget().getType().equals(EntityType.VILLAGER)) {
				log(() -> "Cancelling Targeting " + ete.getTarget().toString() + " by " + ete.getEntity().toString());
				ete.setCancelled(true);
			}
		}
	}
}
