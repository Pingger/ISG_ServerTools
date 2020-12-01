package info.iskariot.pingger.java.bukkit.serverTools;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

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
}
