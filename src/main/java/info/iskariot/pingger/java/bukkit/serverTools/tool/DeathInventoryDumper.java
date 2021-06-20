package info.iskariot.pingger.java.bukkit.serverTools.tool;

import java.util.HashSet;

import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import info.iskariot.pingger.java.bukkit.serverTools.Module;
import info.iskariot.pingger.java.bukkit.serverTools.util.Formatting;

/**
 * Dumps the Players inventory and Death Drops to console, when it dies.
 *
 * @author Pingger
 * @since 0.0.5 (2020-12-16)
 */
public class DeathInventoryDumper extends Module implements Listener
{

	private HashSet<Player> players = new HashSet<>();

	@Override
	public void loadConfigDefaults()
	{
		ensureConfig("enabled", true, "Dumps the Players inventory and Death Drops to console, when it dies.");
	}

	@Override
	public void onConfigReload()
	{
		super.onConfigReload();
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

	@EventHandler
	public void onItemDropped(EntityDropItemEvent edie)
	{
		if (players.contains(edie.getEntity())) {
			log("Would prevent " + edie.getItemDrop().toString() + " at " + edie.getEntity().getLocation());
		}
	}

	/**
	 * @param pde
	 *            {@link PlayerDeathEvent}
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent pde)
	{
		if (isEnabled()) {
			log("Player died: " + pde.getEntity().getName() + " at " + Formatting.formatLocation(pde.getEntity().getLocation()));
			try {
				Inventory inv = pde.getEntity().getInventory();
				for (ItemStack is : inv.getContents()) {
					if (is != null) {
						log("[Inventory] " + is.toString());
					}
				}
				for (ItemStack is : pde.getDrops()) {
					if (is != null) {
						log("[Drop] " + is.toString());
					}
				}
			}
			catch (Exception exc) {
				log("Something went wrong dumping a players Inventory!", exc);
			}
		}
		players.add(pde.getEntity());
		stp.getServer().getScheduler().runTaskLater(stp, () -> players.remove(pde.getEntity()), 2);
	}
}
