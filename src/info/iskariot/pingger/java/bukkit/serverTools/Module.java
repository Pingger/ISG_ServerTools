package info.iskariot.pingger.java.bukkit.serverTools;

/**
 * Abstract class for Modules
 *
 * @author Pingger
 *
 */
public abstract class Module
{
    /**
     * The ServerToolsPlugin, to be set in onEnable
     */
    protected ServerToolsPlugin stp;

    /**
     * Run on plugin disable
     */
    public abstract void onDisable();

    /**
     * Run on plugin enable
     */
    public abstract void onEnable();

    /**
     * Run before plugin enable
     *
     * @param s
     *            the current {@link ServerToolsPlugin}
     */
    public final void setServerToolsPlugin(ServerToolsPlugin s)
    {
        if (s != null)
        {
            stp = s;
        }
    }

    /**
     * Log a given line, if the 'logging' setting is true
     *
     * @param cl
     *            the class calling
     * @param line
     *            the line to log
     */
    protected void log(Class<?> cl, String line)
    {
        if (stp.getConfig().getBoolean(ServerToolsPlugin.buildKey(cl, "logging"), true))
        {
            stp.getLogger().info(line);
        }
    }
}
