package info.iskariot.pingger.java.bukkit.serverTools.util;

import static info.iskariot.pingger.java.bukkit.serverTools.util.Formatting.*;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import info.iskariot.pingger.java.bukkit.serverTools.CommandInterface;

/**
 * The CommandHelper is used to facilitate easier coding and maintainability of
 * Commands
 *
 * @author Pingger
 * @since 0.0.5 (2020-12-16)
 */
public class CommandHelper implements CommandInterface
{
	private static final Logger						log						= Logger.getLogger("ISG CommandHelper");
	protected final String[]						aliases;
	protected int									argOffset				= 0;

	protected SortedMap<String, CommandInterface>	subCommands;
	protected SortedSet<CommandInterface>			subCommandsSet;

	private boolean									subCommandAliasCreated	= false;

	public CommandHelper(String... aliases)
	{
		if (aliases == null || aliases.length == 0) { throw new IllegalArgumentException("Need to provide at least 1 alias!"); }
		this.aliases = aliases;
		subCommands = new TreeMap<>();
		subCommandsSet = new TreeSet<>((a, b) -> a.getLabel()[0].compareTo(b.getLabel()[0]));
	}

	/**
	 * Adds the given Subcommand to this {@link CommandHelper}
	 *
	 * @param ci
	 *            the {@link CommandInterface} to add
	 */
	public synchronized void addSubCommand(CommandInterface ci)
	{
		if (subCommandsSet.add(ci)) {
			for (String alias : ci.getLabel()) {
				String a = alias.toLowerCase();
				if (subCommands.containsKey(a)) {
					log.info("Overwriting alias \"" + a + (subCommandAliasCreated ? "\" Aliases had been created" : "\""));
					subCommands.put(a, ci);
				}
			}
			if (subCommandAliasCreated) {
				rebuildSubCommandAliases();
			}
		}
	}

	@Override
	public boolean getHelp(CommandSender sender, Command command, String label, String[] args)
	{
		// No Subcommand given,
		// or ?,
		// or h,
		// or help
		// or unknown Subcommand
		if (args.length <= argOffset
				|| args[argOffset].equals("?")
				|| args[argOffset].equalsIgnoreCase("h")
				|| args[argOffset].equalsIgnoreCase("help")
				|| getSubcommand(sender, command, label, args) == null)
		{
			sender.sendMessage("Command Category: " + FC_DARK_YELLOW + args[argOffset - 1] + FC_RESET);
			if (getShortHelp() != null) {
				sender.sendMessage(getShortHelp());
			}
			sender.sendMessage("SubCommands:");
			for (CommandInterface ci : subCommandsSet) {
				sender
						.sendMessage(
								"  " + FC_DARK_YELLOW + ci.getLabel()[0] + FC_DARK_AQUA
										+ (ci.getUsage() != null && !ci.getUsage().isBlank() ? " " + ci.getUsage() : "") + FC_RESET
										+ (ci.getShortHelp() == null ? ": " + ci.getShortHelp() : "")
						);
			}
			return true;
		}
		// else the Subcommand exists
		CommandInterface ci = getSubcommand(sender, command, label, args);
		ci.setArgOffset(argOffset + 1);
		return ci.getHelp(sender, command, label, args);
	}

	@Override
	public String[] getLabel()
	{
		return aliases;
	}

	@Override
	public String getPermissionNode(CommandSender sender, Command command, String label, String[] args)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUsage()
	{
		return "?";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		CommandInterface ci = getSubcommand(sender, command, label, args);
		if (ci == null) {
			boolean x = getHelp(sender, command, label, args);
			sender.sendMessage(FC_RED + "Unknown Subcommand: " + (args.length > argOffset ? args[argOffset] : ""));
			return x;
		}
		ci.setArgOffset(argOffset + 1);
		return ci.onCommand(sender, command, label, args);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Rebuilds the internal map for command aliases. This clears cached shortened
	 * aliases, that might have been created by subCommands.
	 */
	public synchronized void rebuildSubCommandAliases()
	{
		subCommandAliasCreated = false;
		subCommands.clear();
		for (CommandInterface ci : subCommandsSet) {
			addSubCommand(ci);
		}
	}

	@Override
	public void setArgOffset(int newOffset)
	{
		argOffset = newOffset;
	}

	/**
	 * Gets the requested subcommand
	 *
	 * @param sender
	 *            the {@link CommandSender}
	 * @param command
	 *            the {@link Command}
	 * @param label
	 *            the used label
	 * @param args
	 *            the provided arguments
	 * @return the Sub-{@link CommandInterface}, or <code>null</code> if not found.
	 */
	protected CommandInterface getSubcommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (args.length <= argOffset) { return null; }
		String arg = args[argOffset].toLowerCase();
		if (subCommands.containsKey(arg)) { return subCommands.get(arg); }
		for (Entry<String, CommandInterface> entry : subCommands.entrySet()) {
			if (entry.getKey().startsWith(arg)) {
				// This speeds up lookup on subsequent calls
				subCommandAliasCreated = true;
				subCommands.put(arg, entry.getValue());
				return entry.getValue();
			}
		}
		return null;
	}

}
