package info.iskariot.pingger.java.bukkit.serverTools.util;

import static org.junit.Assert.assertEquals;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

import org.junit.Test;

/**
 * Tests for {@link ConfigParser}
 *
 * @author Pingger
 *
 */
public class ConfigParserTest_StoreDuration
{

	private static void doSimpleStoreTest(TemporalUnit tu, String tag, int limit)
	{
		for (int i = 1; i < limit; i++) {
			assertEquals(ConfigParser.storeDuration(Duration.of(i, tu)), i + tag);
		}
	}

	private static void doSimpleStoreTest(TemporalUnit tu, String tag, int limit, String nextTag)
	{
		doSimpleStoreTest(tu, tag, limit);
		assertEquals(ConfigParser.storeDuration(Duration.of(limit, tu)), 1 + nextTag);
	}

	/**
	 * Test storing of days
	 */
	@Test
	public void test_StoreDuration_Days()
	{
		doSimpleStoreTest(ChronoUnit.DAYS, "d", 60);
	}

	/**
	 * Tests some weird time combinations
	 */
	@Test
	public void test_StoreDuration_Extended()
	{
		assertEquals(ConfigParser.storeDuration(Duration.ofHours(63)), "2d15h");
		assertEquals(ConfigParser.storeDuration(Duration.ofMinutes(63)), "1h3m");
		assertEquals(ConfigParser.storeDuration(Duration.ofSeconds(63)), "1m3s");
		assertEquals(ConfigParser.storeDuration(Duration.ofMillis(6300)), "6s300");
		assertEquals(ConfigParser.storeDuration(Duration.ofHours(1).plusSeconds(30)), "1h30s");
		assertEquals(ConfigParser.storeDuration(Duration.ofMillis(60300)), "1m300");
	}

	/**
	 * Test storing of Hours
	 */
	@Test
	public void test_StoreDuration_Hours()
	{
		doSimpleStoreTest(ChronoUnit.HOURS, "h", 24, "d");
	}

	/**
	 * Test storing of Milliseconds
	 */
	@Test
	public void test_StoreDuration_Millis()
	{
		doSimpleStoreTest(ChronoUnit.MILLIS, "", 1000, "s");
	}

	/**
	 * Test storing of Minutes
	 */
	@Test
	public void test_StoreDuration_Minutes()
	{
		doSimpleStoreTest(ChronoUnit.MINUTES, "m", 60, "h");
	}

	/**
	 * Test storing <code>null</code>
	 */
	@Test
	public void test_StoreDuration_Null()
	{
		assertEquals(ConfigParser.storeDuration(null), "null");
	}

	/**
	 * Test storing of Seconds
	 */
	@Test
	public void test_StoreDuration_Seconds()
	{
		doSimpleStoreTest(ChronoUnit.SECONDS, "s", 60, "m");
	}
}
