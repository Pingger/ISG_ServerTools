package info.iskariot.pingger.java.bukkit.serverTools.tool;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;

import info.iskariot.pingger.java.bukkit.serverTools.Module;

/**
 * @author Pingger
 *
 */
public class WebMapGenerator extends Module
{
	private static Hashtable<Material, BufferedImage> blocks = new Hashtable<>();

	@Override
	public void onDisable()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onEnable()
	{
		// TODO Auto-generated method stub
		// ChunkSnapshot cs = stp.getServer().getWorlds().get(0).getChunkAt(0, 0).getChunkSnapshot()
	}

	/**
	 * Render a chunk top down
	 *
	 * @param cs
	 *            the ChunkSnapshot
	 * @return the resulting Image
	 */
	public BufferedImage renderChunk(ChunkSnapshot cs)
	{
		int d = 16;
		BufferedImage bi = new BufferedImage(16 * d, 16 * d, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = bi.getGraphics();
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = 255; y >= 0; y--) {
					Material m = cs.getBlockType(x, y, z);
					if (m.isAir()) {
						continue;
					}
					if (m.isOccluding()) {
						g.drawImage(getBlockImage(m), x * d, z * d, d, d, null);
						for (; y < 256; y++) {
							m = cs.getBlockType(x, y, z);
							if (m.isAir()) {
								continue;
							}
							g.drawImage(getBlockImage(m), x * d, z * d, d, d, null);
						}
						break;
					}
				}
			}
		}
		return bi;
	}

	/**
	 *
	 * @param m
	 * @return The Image for the given Material
	 */
	protected BufferedImage getBlockImage(Material m)
	{
		return blocks.get(m);
	}

}
