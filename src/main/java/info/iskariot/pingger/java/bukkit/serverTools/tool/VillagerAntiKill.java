package info.iskariot.pingger.java.bukkit.serverTools.tool;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;

import info.iskariot.pingger.java.bukkit.serverTools.Module;

/**
 *
 * @author Pingger
 *
 */
public class VillagerAntiKill extends Module implements Listener
{
	@Override
	public void loadConfigDefaults()
	{
		ensureConfig("enabled", true, null);
		ensureConfig("logging", false, null);
	}

	@Override
	public void onDisable()
	{

	}

	@Override
	public void onEnable()
	{
		stp.getServer().getPluginManager().registerEvents(this, stp);
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

				log(
						() -> "Cancelling Damage to " + ede.getEntity().toString() + "; Cause: " + ede.getCause().toString() + "; Amount: "
								+ ede.getDamage()
				);
				ede.setCancelled(true);
				ede.setDamage(0);
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
