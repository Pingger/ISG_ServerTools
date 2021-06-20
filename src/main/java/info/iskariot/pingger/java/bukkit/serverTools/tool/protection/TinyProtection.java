package info.iskariot.pingger.java.bukkit.serverTools.tool.protection;

import java.util.Random;

import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootContext.Builder;
import org.bukkit.loot.LootTable;

import info.iskariot.pingger.java.bukkit.serverTools.Module;

// FIXME Liquids still work!
// FIXME creepers

public class TinyProtection extends Module implements Listener
{
	private static final Location	cloc				= new Location(null, 341, 22, 2049);
	private static final Location	loc					= new Location(null, 341, 32, 2049);
	private static final double		protectionDistance	= 128 * 128;

	@Override
	public void loadConfigDefaults()
	{
	}

	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent event)
	{
		CommandSender p = event.getPlayer();
		if (p == null || !p.isOp()) {
			if (p == null) {
				p = stp.getServer().getConsoleSender();
			}
			if (event.getBlock().getWorld().equals(loc.getWorld())) {
				if (event.getBlock().getLocation().distanceSquared(loc) < protectionDistance) {
					event.setCancelled(true);
					p.sendMessage("This area is protected!");
				}
			}
		}
	}

	@EventHandler
	public void onBlockDamageEvent(BlockDamageEvent event)
	{
		CommandSender p = event.getPlayer();
		if (p == null || !p.isOp()) {
			if (p == null) {
				p = stp.getServer().getConsoleSender();
			}
			if (event.getBlock().getWorld().equals(loc.getWorld())) {
				if (event.getBlock().getLocation().distanceSquared(loc) < protectionDistance) {
					event.setCancelled(true);
					p.sendMessage("This area is protected!");
				}
			}
		}
	}

	@EventHandler
	public void onBlockExplodeEvent(BlockExplodeEvent event)
	{
		if (event.getBlock().getWorld().equals(loc.getWorld())) {
			if (event.getBlock().getLocation().distanceSquared(loc) < protectionDistance) {
				event.blockList().clear();
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBlockPlaceEvent(BlockPlaceEvent event)
	{
		CommandSender p = event.getPlayer();
		if (p == null || !p.isOp()) {
			if (p == null) {
				p = stp.getServer().getConsoleSender();
			}
			if (event.getBlock().getWorld().equals(loc.getWorld())) {
				if (event.getBlock().getLocation().distanceSquared(loc) < protectionDistance) {
					event.setCancelled(true);
					p.sendMessage("This area is protected!");
				}
			}
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
		loc.setWorld(stp.getServer().getWorld("UED"));
		cloc.setWorld(stp.getServer().getWorld("UED"));
		stp.getServer().getPluginManager().registerEvents(this, stp);
		stp.getServer().getScheduler().runTaskTimer(stp, () -> {
			if (cloc.getChunk().isLoaded()) {
				String[] tables = new String[] {
						"chests/abandoned_mineshaft",
						"chests/bastion_treasure",
						"chests/bastion_treasure",
						"chests/buried_treasure",
						"chests/desert_pyramid",
						"chests/end_city_treasure",
						"chests/igloo_chest",
						"chests/jungle_temple",
						"chests/ruined_portal",
						"chests/shipwreck_treasure",
						"chests/simple_dungeon",
						"chests/simple_dungeon",
						"chests/simple_dungeon",
				};
				if (Material.CHEST.equals(cloc.getBlock().getType())) {
					Chest c = (Chest) cloc.getBlock().getState();
					Random r = new Random();
					String table = tables[r.nextInt(tables.length)];
					LootTable lt = stp
							.getServer()
							.getLootTable(NamespacedKey.minecraft(table));
					try {
						if (lt != null) {
							lt.fillInventory(c.getBlockInventory(), r, new LootContext.Builder(cloc).build());
							log("Refilled Chest with " + table);
						}
						else {
							log("Can't find LootTable: " + table);
						}
					}
					catch (Exception e) {
						e.printStackTrace();
						log("table: " + table);
						log("lt: " + lt);
						log("c: " + c);
						log("r: " + r);
						log("cloc: " + cloc);
						Builder i = new LootContext.Builder(cloc);
						log("new LootContext.Builder(cloc): " + i);
						if (i != null) {
							log(".build: " + i.build());
						}
					}
				}
				else {
					log("Not a chest! " + cloc.getBlock().getType());
				}
			}

		}, 2000, 60 * 60 * 20);
	}

	@EventHandler
	public void onEntityChangeBlockEvent(EntityChangeBlockEvent event)
	{
		if (event.getBlock().getWorld().equals(loc.getWorld())) {
			if (event.getBlock().getLocation().distanceSquared(loc) < protectionDistance) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent eee)
	{
		if (eee.getEntity().getWorld().equals(loc.getWorld())) {
			if (eee.getEntity().getLocation().distanceSquared(loc) < protectionDistance) {
				eee.blockList().clear();
			}
		}
	}
}
