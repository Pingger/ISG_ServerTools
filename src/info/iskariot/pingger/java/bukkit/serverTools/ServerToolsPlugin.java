package info.iskariot.pingger.java.bukkit.serverTools;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.logging.Level;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import info.iskariot.pingger.java.bukkit.serverTools.monitor.TPSMonitor;
import info.iskariot.pingger.java.bukkit.serverTools.sleepVote.SleepVote;

/**
 * @author Pingger
 *
 */
public class ServerToolsPlugin extends JavaPlugin implements Listener
{
    private static LinkedList<Module>              loadedModules = new LinkedList<>();
    @SuppressWarnings("unchecked")
    private static final Class<? extends Module>[] modules       = new Class[] {
            TPSMonitor.class,
            SleepVote.class
    };

    /**
     * Builds a Config Key based on the class and requested sub-key
     *
     * @param cl
     *            the class
     * @param key
     *            the sub key
     * @return the resulting full key
     */
    public static String buildKey(Class<?> cl, String key)
    {
        return cl.getCanonicalName() + "." + key;
    }

    /**
     * Ensures, that the given Key is set.
     *
     * @param cl
     *            the class calling
     * @param key
     *            the sub key
     * @param def
     *            the default value if not set
     * @param desc
     *            The Description for this node
     */
    public void ensureConfig(Class<?> cl, String key, boolean def, String desc)
    {
        String k = buildKey(cl, key);
        getConfig().set(k, getConfig().getBoolean(k, def));
        if (desc != null && !desc.trim().isEmpty())
        {
            getConfig().set(k + "_Desc", desc);
        }
    }

    /**
     * Ensures, that the given Key is set.
     *
     * @param cl
     *            the class calling
     * @param key
     *            the sub key
     * @param def
     *            the default value if not set
     * @param desc
     *            The Description for this node
     */
    public void ensureConfig(Class<?> cl, String key, double def, String desc)
    {
        String k = buildKey(cl, key);
        getConfig().set(k, getConfig().getDouble(k, def));
        if (desc != null && !desc.trim().isEmpty())
        {
            getConfig().set(k + "_Desc", desc);
        }
    }

    /**
     * Ensures, that the given Key is set.
     *
     * @param cl
     *            the class calling
     * @param key
     *            the sub key
     * @param def
     *            the default value if not set
     * @param desc
     *            The Description for this node
     */
    public void ensureConfig(Class<?> cl, String key, int def, String desc)
    {
        String k = buildKey(cl, key);
        getConfig().set(k, getConfig().getInt(k, def));
        if (desc != null && !desc.trim().isEmpty())
        {
            getConfig().set(k + "_Desc", desc);
        }
    }

    /**
     * Ensures, that the given Key is set.
     *
     * @param cl
     *            the class calling
     * @param key
     *            the sub key
     * @param def
     *            the default value if not set
     * @param desc
     *            The Description for this node
     */
    public void ensureConfig(Class<?> cl, String key, long def, String desc)
    {
        String k = buildKey(cl, key);
        getConfig().set(k, getConfig().getLong(k, def));
        if (desc != null && !desc.trim().isEmpty())
        {
            getConfig().set(k + "_Desc", desc);
        }
    }

    /**
     * Ensures, that the given Key is set.
     *
     * @param cl
     *            the class calling
     * @param key
     *            the sub key
     * @param def
     *            the default value if not set
     * @param desc
     *            The Description for this node
     */
    public void ensureConfig(Class<?> cl, String key, String def, String desc)
    {
        String k = buildKey(cl, key);
        getConfig().set(k, getConfig().getString(k, def));
        if (desc != null && !desc.trim().isEmpty())
        {
            getConfig().set(k + "_Desc", desc);
        }
    }

    /**
     * Ensures, that the given Key is set.
     *
     * @param cl
     *            the class calling
     * @param key
     *            the sub key
     * @param def
     *            the default value if not set
     * @param desc
     *            The Description for this node
     */
    public void ensureConfig(Class<?> cl, String key, String[] def, String desc)
    {
        String k = buildKey(cl, key);
        if (!getConfig().isList(k))
        {
            getConfig().set(k, def);
        }
        if (desc != null && !desc.trim().isEmpty())
        {
            getConfig().set(k + "_Desc", desc);
        }
    }

    @Override
    public void onDisable()
    {
        while (!loadedModules.isEmpty())
        {
            try
            {
                Module m = loadedModules.removeLast();
                getLogger().info("Disabling: " + m.getClass().getCanonicalName());
                m.onDisable();
            }
            catch (Throwable e)
            {
                getLogger().log(Level.SEVERE, "Module couldn't be disabled!", e);
            }
        }
        getLogger().info("Disabled!");
    }

    @Override
    public void onEnable()
    {
        reloadConfig();
        loadConfigDefaults();
        saveConfig();
        for (Class<? extends Module> mc : modules)
        {
            if (getConfig().getBoolean(buildKey(mc, "enabled"), true))
            {
                try
                {
                    getLogger().info("Enabling: " + mc.getCanonicalName());
                    Module m = mc.getConstructor().newInstance();
                    m.setServerToolsPlugin(this);
                    m.onEnable();
                    loadedModules.add(m);
                }
                catch (Throwable e)
                {
                    getLogger().log(Level.SEVERE, "Module couldn't be loaded!", e);
                }
            }
        }
        getLogger().info("Enabled!");
    }

    @Override
    public void onLoad()
    {
        getLogger().info("Loading Iskariot Gaming's Server Tools Plugin...");
        getLogger().info("Loaded!");
    }

    /**
     * Adds unset values to the Config
     */
    private void loadConfigDefaults()
    {
        SleepVote.loadConfigDefaults(this);
        for (Class<? extends Module> mc : modules)
        {
            try
            {
                Method m = mc.getDeclaredMethod("loadConfigDefaults", getClass());
                m.invoke(null, this);
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }
        }
    }
}
