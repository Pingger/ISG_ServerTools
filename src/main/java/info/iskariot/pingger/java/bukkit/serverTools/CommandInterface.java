package info.iskariot.pingger.java.bukkit.serverTools;

import org.bukkit.command.*;

/**
 * @author Pingger
 *
 */
public interface CommandInterface extends CommandExecutor, TabCompleter
{
	/**
	 * Prints Help for the given Command to the CommandSender
	 * <br>
	 * If false is returned, then the "usage" plugin.yml entry for this command
	 * (if defined) will be sent to the player.
	 *
	 * @param sender
	 *            Source of the command
	 * @param command
	 *            Command which was executed
	 * @param label
	 *            Alias of the command which was used
	 * @param args
	 *            Passed command arguments
	 * @return true if a valid command, otherwise false
	 */
	public boolean getHelp(CommandSender sender, Command command, String label, String[] args);

	/**
	 * @return the Label(s) of the subcommand
	 */
	public String[] getLabel();

	/**
	 * Returns the most specific PermissionNode required to execute the given
	 * Command
	 *
	 * @param sender
	 *            the Command Sender
	 * @param command
	 *            the command
	 * @param label
	 *            the label
	 * @param args
	 *            the args
	 * @return the PermissionNode
	 */
	public String getPermissionNode(CommandSender sender, Command command, String label, String[] args);

	/**
	 * @param newOffset
	 *            the offset to set
	 */
	public void setArgOffset(int newOffset);

	/**
	 * Returns the String Permission, or an explanation of what is required to
	 * execute the given Command
	 *
	 * @param sender
	 *            the Command Sender
	 * @param command
	 *            the command
	 * @param label
	 *            the label
	 * @param args
	 *            the args
	 * @return the PermissionNode or Explanation
	 */
	default String getRequiredPermission(CommandSender sender, Command command, String label, String[] args)
	{
		return "You need Permission: " + getPermissionNode(sender, command, label, args);
	}
}
