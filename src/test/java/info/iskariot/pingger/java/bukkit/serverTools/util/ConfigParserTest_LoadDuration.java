package info.iskariot.pingger.java.bukkit.serverTools.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
public class ConfigParserTest_LoadDuration
{

	private static void doSimpleLoadTest(TemporalUnit tu, String tag, int limit)
	{
		for (int i = 1; i <= limit; i++) {
			assertEquals(ConfigParser.loadDuration(i + tag, null), Duration.of(i, tu));
		}
	}

	/**
	 * Test loading of days
	 */
	@Test
	public void test_LoadDuration_Days()
	{
		doSimpleLoadTest(ChronoUnit.DAYS, "d", 60);
	}

	/**
	 * Tests some weird time combinations
	 */
	@Test
	public void test_LoadDuration_Extended()
	{
		assertEquals(ConfigParser.loadDuration("2d15h", null), Duration.ofHours(63));
		assertEquals(ConfigParser.loadDuration("1h3m", null), Duration.ofMinutes(63));
		assertEquals(ConfigParser.loadDuration("1m3s", null), Duration.ofSeconds(63));
		assertEquals(ConfigParser.loadDuration("6s300", null), Duration.ofMillis(6300));
		assertEquals(ConfigParser.loadDuration("1h30s", null), Duration.ofHours(1).plusSeconds(30));
		assertEquals(ConfigParser.loadDuration("1m300", null), Duration.ofMillis(60300));
		assertEquals(ConfigParser.loadDuration("3d-12h", null), Duration.ofHours(60));
		assertEquals(ConfigParser.loadDuration("3h-30m", null), Duration.ofMinutes(150));
		assertEquals(ConfigParser.loadDuration("3m-30s", null), Duration.ofSeconds(150));
		assertEquals(ConfigParser.loadDuration("3s-500", null), Duration.ofMillis(2500));
	}

	/**
	 * Test loading of Hours
	 */
	@Test
	public void test_LoadDuration_Hours()
	{
		doSimpleLoadTest(ChronoUnit.HOURS, "h", 24);
	}

	/**
	 * Test loading of Milliseconds
	 */
	@Test
	public void test_LoadDuration_Millis()
	{
		doSimpleLoadTest(ChronoUnit.MILLIS, "", 1000);
	}

	/**
	 * Test loading of Minutes
	 */
	@Test
	public void test_LoadDuration_Minutes()
	{
		doSimpleLoadTest(ChronoUnit.MINUTES, "m", 60);
	}

	/**
	 * Test loading <code>null</code>
	 */
	@Test
	public void test_LoadDuration_Null()
	{
		for (char c1 : new char[] {
				'n', 'N'
		}) {
			for (char c2 : new char[] {
					'u', 'U'
			}) {
				for (char c3 : new char[] {
						'l', 'L'
				}) {
					for (char c4 : new char[] {
							'l', 'L'
					}) {
						assertNull(ConfigParser.loadDuration("" + c1 + c2 + c3 + c4, null));
					}
				}
			}
		}
	}

	/**
	 * Test loading of Seconds
	 */
	@Test
	public void test_LoadDuration_Seconds()
	{
		doSimpleLoadTest(ChronoUnit.SECONDS, "s", 60);
	}
}
