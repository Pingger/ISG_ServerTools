package info.iskariot.pingger.java.bukkit.serverTools.tool;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;

import info.iskariot.pingger.java.bukkit.serverTools.Module;
import info.iskariot.pingger.java.bukkit.serverTools.ServerToolsPlugin;

/**
 *
 * @author Pingger
 *
 */
public class VillagerAntiKill extends Module implements Listener
{
	/**
	 * Ensures that the ConfigDefaults are set
	 *
	 * @param plg
	 *            the {@link ServerToolsPlugin} calling
	 */
	public static void loadConfigDefaults(ServerToolsPlugin plg)
	{
		plg.ensureConfig(ServerToolsPlugin.buildKey(VillagerAntiKill.class, "enabled"), true, null);
		plg.ensureConfig(ServerToolsPlugin.buildKey(VillagerAntiKill.class, "logging"), false, null);
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
		if (stp.isEnabled() && stp.getConfig().getBoolean(ServerToolsPlugin.buildKey(VillagerAntiKill.class, "enabled"), true)) {
			if (ede.getEntityType() != null && ede.getEntityType().equals(EntityType.VILLAGER)) {
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
		if (stp.isEnabled() && stp.getConfig().getBoolean(ServerToolsPlugin.buildKey(VillagerAntiKill.class, "enabled"), true)) {
			if (ete.getTarget() != null && ete.getTarget().getType() != null && ete.getTarget().getType().equals(EntityType.VILLAGER)) {
				ete.setCancelled(true);
			}
		}
	}
}
