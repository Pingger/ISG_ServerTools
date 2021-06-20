package info.iskariot.pingger.java.bukkit.serverTools.util;

import org.bukkit.Location;
import org.bukkit.World;

/**
 *
 * @author Pingger
 * @since 2020-11-24
 */
public class Formatting
{
	/** Chat Formatting Code for Aqua Text */
	public static final String	FC_AQUA			= "§b";
	/** Chat Formatting Code for Black Text */
	public static final String	FC_BLACK		= "§0";
	/** Chat Formatting Code for Blue Text */
	public static final String	FC_BLUE			= "§9";
	/** Chat Formatting Code for Bold Text */
	public static final String	FC_BOLD			= "§l";
	/** Chat Formatting Code for Dark-Aqua Text */
	public static final String	FC_DARK_AQUA	= "§3";
	/** Chat Formatting Code for Dark-Blue Text */
	public static final String	FC_DARK_BLUE	= "§1";
	/** Chat Formatting Code for Dark-Gray Text */
	public static final String	FC_DARK_Gray	= "§8";
	/** Chat Formatting Code for Dark-Green Text */
	public static final String	FC_DARK_GREEN	= "§2";
	/** Chat Formatting Code for Dark-Purple Text */
	public static final String	FC_DARK_PURPLE	= "§5";
	/** Chat Formatting Code for Dark-Red Text */
	public static final String	FC_DARK_RED		= "§4";
	/** Chat Formatting Code for Dark-Yellow Text */
	public static final String	FC_DARK_YELLOW	= "§6";
	/** Chat Formatting Code for Gray Text */
	public static final String	FC_GRAY			= "§7";
	/** Chat Formatting Code for Green Text */
	public static final String	FC_GREEN		= "§a";
	/** Chat Formatting Code for White Text */
	public static final String	FC_ITALIC		= "§o";
	/** Chat Formatting Code for Obfuscated Text */
	public static final String	FC_OBFUSCATED	= "§k";
	/** Chat Formatting Code for Blue Text */
	public static final String	FC_PURPLE		= "§d";
	/** Chat Formatting Code for Red Text */
	public static final String	FC_RED			= "§c";
	/** Chat Formatting Code to reset formatting to default */
	public static final String	FC_RESET		= "§r";
	/** Chat Formatting Code for Strikethrough Text */
	public static final String	FC_STRIKE		= "§m";
	/** Chat Formatting Code for Underlined Text */
	public static final String	FC_UNDERLINE	= "§n";
	/** Chat Formatting Code for White Text */
	public static final String	FC_WHITE		= "§f";
	/** Chat Formatting Code for Yellow Text */
	public static final String	FC_YELLOW		= "§e";

	/**
	 * Alias for {@link #formatLocation(Location)}
	 *
	 * @param l
	 *            the Location to format
	 * @return see {@link #formatLocation(Location)}
	 */
	public static String format(Location l)
	{
		return formatLocation(l);
	}

	/**
	 * Alias for {@link #formatWorld(World)}
	 *
	 * @param w
	 *            the World to format
	 * @return see {@link #formatWorld(World)}
	 */
	public static String format(World w)
	{
		return formatWorld(w);
	}

	/**
	 * Formats a Anvil coordinate to a user recognizable String
	 *
	 * @param x
	 *            x coordinate
	 * @param z
	 *            z coordinate
	 * @return the resulting String
	 */
	public static String formatAnvil(int x, int z)
	{
		return FC_DARK_YELLOW + String.format("A(% ,4d,% ,4d)", x, z) + FC_RESET;
	}

	/**
	 * Formats a Location
	 *
	 * @param location
	 *            the location to format
	 * @return a String for the location like this "formatWorld()
	 *         formatShortLocation()"
	 */
	public static String formatLocation(Location location)
	{
		return formatWorld(location.getWorld()) + formatShortLocation(location);
	}

	/**
	 * Formats a Milliseconds
	 *
	 * @param ms
	 *            the value to Format
	 * @return the resulting String
	 */
	public static String formatMilliseconds(long ms)
	{
		return String.format("%,d", ms) + "ms";
	}

	/**
	 * Formats a Milliseconds
	 *
	 * @param ms
	 *            the value to Format
	 * @param align
	 *            the amount of alignment
	 * @return the resulting String
	 */
	public static String formatMilliseconds(long ms, int align)
	{
		return String.format("%," + align + "d", ms) + "ms";
	}

	/**
	 * Format only the coordinates of the given location
	 *
	 * @param location
	 *            the location to format
	 * @return x.## y.## z.##
	 */
	public static String formatShortLocation(Location location)
	{
		return String.format("[%.2f:%.2f:%.2f]", location.getX(), location.getY(), location.getZ());
	}

	/**
	 * Formats a World to a user recognizable String
	 *
	 * @param w
	 *            the World to Format
	 * @return the resulting String
	 */
	public static String formatWorld(World w)
	{
		return FC_DARK_AQUA + w.getName() + FC_RESET + " (" + FC_BLUE + w.getUID().toString() + FC_RESET + ")";
	}

}
