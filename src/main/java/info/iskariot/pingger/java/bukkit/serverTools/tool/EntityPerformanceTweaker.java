package info.iskariot.pingger.java.bukkit.serverTools.tool;

import java.util.*;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import info.iskariot.pingger.java.bukkit.serverTools.Module;

/**
 *
 * @author Pingger
 * @since 0.0.5
 */
public class EntityPerformanceTweaker extends Module implements Listener
{

	private Set<EntityType>	noCollision	= new HashSet<>();
	private Set<EntityType>	noPickup	= new HashSet<>();

	@Override
	public void loadConfigDefaults()
	{
		super.loadConfigDefaults();
		getConfig().set("info", "All these settings only apply to entites spawned AFTER they were enabled!");
		ensureConfig("NoCollision", new String[] {
				"SHEEP", "COW", "MUSHROOM_COW", "PIG", "CHICKEN", "LLAMA", "MULE", "COD", "SALMON", "TROPICAL_FISH", "SQUID", "CAT", "FOX", "WOLF",
				"TURTLE", "RABBIT", "BEE", "HORSE", "DONKEY", "VILLAGER"
		},
				"The types to disable Entity collisions on. Useful for mobs often in Pens or close together. Only disables collisions one sided. e.g. Players with enabled collisions will still be able to push non-collidable entities and vice versa."
		);
		ensureConfig("NoPickup", new String[] {
				"SHEEP", "COW", "MUSHROOM_COW", "PIG", "CHICKEN", "LLAMA", "MULE", "COD", "SALMON", "TROPICAL_FISH", "SQUID", "CAT", "FOX", "WOLF",
				"TURTLE", "RABBIT", "BEE", "HORSE", "DONKEY"
		}, "The types to disable Item Pickup on.");
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

	}

	@Override
	public void onEnable()
	{
		doConfigReload();
		stp.getServer().getPluginManager().registerEvents(this, stp);
	}

	/**
	 * @param ese
	 *            {@link EntitySpawnEvent}
	 */
	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent ese)
	{
		if (isEnabled()) {
			if (ese.getEntity() instanceof LivingEntity) {
				LivingEntity le = (LivingEntity) ese.getEntity();
				if (le.isCollidable() && noCollision.contains(le.getType())) {
					le.setCollidable(false);
				}
				if (le.getCanPickupItems() && noPickup.contains(le.getType())) {
					le.setCanPickupItems(false);
				}
			}
		}
	}

	private void doConfigReload()
	{
		loadSet("NoCollision", noCollision);
		loadSet("NoPickup", noPickup);
	}

	private void loadSet(String key, Set<EntityType> set)
	{
		Set<EntityType> check = new HashSet<>();
		Set<String> ty = new HashSet<>();
		List<String> list = getConfig().getStringList(key);
		if (list.isEmpty()) {
			log(key + "-List is empty!");
			set.clear();
			return;
		}
		for (String s : list) {
			try {
				EntityType et = EntityType.valueOf(s);
				ty.add(et.name());
				check.add(et);
				set.add(et);
			}
			catch (IllegalArgumentException iexc) {
				log(s + " is not a valid EntityType!");
			}
		}
		Set<EntityType> clone = new HashSet<>(set);
		for (EntityType et : clone) {
			if (!check.contains(et)) {
				set.remove(et);
			}
		}
		getConfig().set(key, ty.toArray());
	}

}
