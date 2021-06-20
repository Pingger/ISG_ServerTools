package info.iskariot.pingger.java.bukkit.serverTools.util;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contains Methods to store and load some more exotic Types (like Locations,
 * Durations, ...)
 *
 * @author Pingger
 * @since 0.0.5 (2020-12-14)
 */
public class ConfigParser
{
	private static final Logger l = Logger.getLogger("ISG_ServerTools_ConfigParser");

	/**
	 * Loads a {@link Duration} from the given String. If the String is
	 * <code>null</code> or "null", then the supplied default value is returned.<br>
	 * Format is "#d#h#m#s###", where the parts are, in order, days, hours, minutes,
	 * seconds, milliseconds. The order is important, but 'zero' components may be
	 * omitted.<br>
	 * <br>
	 * <h5>Valid Examples:</h5>
	 * 5d<br>
	 * 2d3h4m5s123<br>
	 * 2d96h (resolves to 6 days)<br>
	 * 3h1800s (resolves to 3 hours and 30 minutes)<br>
	 * 1800000 (resolves to 30 minutes, because the value is treated as
	 * 3h-30m (resolves to 2 hours and 30 minutes)<br>
	 * milliseconds)<br>
	 * <i>blank string</i> resolves to supplied default value<br>
	 *
	 * @param store
	 *            the String to parse
	 * @param def
	 *            the value to return if parsing fails or the store is
	 *            <code>null</code> or "null"
	 * @return either a valid parsed {@link Duration} or <code>def</code>, if the
	 *         value can't be parsed or is <code>null</code> or "null"
	 */
	/*
	 * Note: Nanos are intentionally not supported!
	 */
	public static Duration loadDuration(String store, Duration def)
	{
		if (store == null || store.isBlank()) { return def; }
		String st = store.trim();
		Duration d = Duration.ZERO;
		try {
			if (st.equalsIgnoreCase("null")) { return def; }
			if (st.indexOf('d') > 0) {
				String[] parts = st.split("d", 2);
				d = d.plusDays(Long.parseLong(parts[0].trim()));
				if (parts.length == 1) { return d; }
				st = parts[1].trim();
			}
			if (st.indexOf('h') > 0) {
				String[] parts = st.split("h", 2);
				d = d.plusHours(Long.parseLong(parts[0].trim()));
				if (parts.length == 1) { return d; }
				st = parts[1].trim();
			}
			if (st.indexOf('m') > 0) {
				String[] parts = st.split("m", 2);
				d = d.plusMinutes(Long.parseLong(parts[0].trim()));
				if (parts.length == 1) { return d; }
				st = parts[1].trim();
			}
			if (st.indexOf('s') > 0) {
				String[] parts = st.split("s", 2);
				d = d.plusSeconds(Long.parseLong(parts[0].trim()));
				if (parts.length == 1) { return d; }
				st = parts[1].trim();
			}
			if (!st.isBlank()) {
				d = d.plusMillis(Long.parseLong(st));
			}
			return d;
		}
		catch (NumberFormatException nfexc) {
			l.log(Level.WARNING, "Unable to parse Duration \"" + store + "\"! Using default value: " + storeDuration(def));
			return def;
		}
	}

	/**
	 * Stores the given {@link Duration} in such a way, that it can restored using
	 * the {@link #loadDuration(String, Duration)}-Method
	 *
	 * @param d
	 *            the {@link Duration} to store
	 * @return the resulting String
	 */
	/*
	 * Note: Nanos are intentionally not supported!
	 */
	public static String storeDuration(Duration d)
	{
		if (d == null) { return "null"; }
		StringBuilder store = new StringBuilder();
		if (d.toDaysPart() != 0) {
			store.append(d.toDays() + "d");
		}
		if (d.toHoursPart() != 0) {
			store.append(d.toHoursPart() + "h");
		}
		if (d.toMinutesPart() != 0) {
			store.append(d.toMinutesPart() + "m");
		}
		if (d.toSecondsPart() != 0) {
			store.append(d.toSecondsPart() + "s");
		}
		if (d.toMillisPart() != 0 || store.length() == 0) {
			store.append(d.toMillisPart());
		}
		return store.toString();
	}
}
