package info.iskariot.pingger.java.bukkit.serverTools.tool;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import info.iskariot.pingger.java.bukkit.serverTools.Module;

/**
 * Dumps the Players inventory and Death Drops to console, when it dies.
 *
 * @author Pingger
 * @since 0.0.5 (2020-12-16)
 */
public class DeathInventoryDumper extends Module implements Listener
{

	@Override
	public void loadConfigDefaults()
	{
		ensureConfig("enabled", true, "Dumps the Players inventory and Death Drops to console, when it dies.");
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
	 * @param pde
	 *            {@link PlayerDeathEvent}
	 */
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent pde)
	{
		if (isEnabled()) {
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
	}

}
