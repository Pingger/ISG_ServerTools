package info.iskariot.pingger.java.bukkit.serverTools.api.dynmap;

import java.lang.reflect.Method;

import org.bukkit.plugin.Plugin;

import info.iskariot.pingger.java.bukkit.serverTools.api.APIImplementation;

public class DynmapAPI extends APIImplementation
{
	private static Plugin	dynmap					= null;;
	private Method			m_getMarkerAPI			= null;
	private Method			m_getPauseUpdateRenders	= null;
	private Method			m_getPlayerVisibility	= null;
	private Method			m_markerAPIInitialized	= null;
	private Method			m_sendBroadcastToWeb	= null;
	private Method			m_setPauseUpdateRenders	= null;
	private Method			m_setPlayerVisibility	= null;
	private Method			m_triggerRenderOfBlock	= null;
	private Method			m_triggerRenderOfVolume	= null;

	@Override
	public void onDisable()
	{
		dynmap = null;
		log("DynmapAPI-Access disabled");
	}

	@Override
	public void onEnable()
	{
		log("DynmapAPI-Access enabling");
		dynmap = stp.getServer().getPluginManager().getPlugin("dynmap");
		if (dynmap != null) {
			try {
				Class<?> c = dynmap.getClass();
				m_getMarkerAPI = c.getMethod("getMarkerAPI");
				m_markerAPIInitialized = c.getMethod("markerAPIInitialized");
				m_sendBroadcastToWeb = c.getMethod("sendBroadcastToWeb", String.class, String.class);
				m_triggerRenderOfVolume = c
						.getMethod("triggerRenderOfVolume", String.class, int.class, int.class, int.class, int.class, int.class, int.class);
				m_triggerRenderOfBlock = c.getMethod("triggerRenderOfBlock", String.class, int.class, int.class, int.class);
				m_setPauseUpdateRenders = c.getMethod("setPauseUpdateRenders", boolean.class);
				m_getPauseUpdateRenders = c.getMethod("getPauseUpdateRenders");
				m_setPlayerVisibility = c.getMethod("setPlayerVisiblity", String.class, boolean.class);
				m_getPlayerVisibility = c.getMethod("getPlayerVisbility", String.class);
			}
			catch (NoSuchMethodException e) {
				log("Dynmap detected, but API endpoint is missing!");
				dynmap = null;
			}
			catch (SecurityException e) {
				log("Dynmap detected, but API endpoint is protected by SecurityManager!", e);
				dynmap = null;
			}
			log("DynmapAPI-Access enabled");
			return;
		}
		log("DynmapAPI-Access not available! Plugin 'dynmap' not found!");
	}

}
