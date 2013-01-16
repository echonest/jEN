package com.echonest.api.v4;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.junit.Assert.*;


import com.echonest.api.v4.examples.SearchSongsExample;
import com.echonest.api.v4.util.Commander;

@RunWith(JUnit4.class)
public class APITests {
	private Commander cmd;
	private String radiohead = "ARH6W4X1187B99274F";
	private String prefix = "music://id.echonest.com/~/AR/";
	private SearchSongsExample sse;

	@Before
	public void setUp() throws EchoNestException {
		cmd = new Commander("test");
		cmd.setTraceSends(false);
		cmd.setTraceRecvs(false);
		sse = new SearchSongsExample();
		Params stdParams = new Params();
		stdParams.add("api_key", "FILDTEOIK2HBORODV");
		cmd.setStandardParams(stdParams);
	}


	@Test
	public void similarTest() throws EchoNestException {
		Params params = new Params();
		params.add("id", radiohead);
		cmd.sendCommand("artist/similar", params);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSimilarTimingTest() throws EchoNestException {
		long start = System.currentTimeMillis();
		Params params = new Params();
		params.add("id", radiohead);
		params.add("results", 10);
		Map results = cmd.sendCommand("artist/similar", params);
		Map response = (Map) results.get("response");
		List artistList = (List) response.get("artists");

		for (int i = 0; i < artistList.size(); i++) {
			Map artist = (Map) artistList.get(i);
			String id = (String) artist.get("id");
			Params p = new Params();
			p.add("id", id);
			cmd.sendCommand("artist/similar", p);
		}

		long delta = System.currentTimeMillis() - start;
		System.out.println("Delta is " + delta);
		assertTrue(delta < 15000);
	}

	@Test(expected = EchoNestException.class)
	public void testBadArgCheck() throws EchoNestException {
		Params params = new Params();
		params.add("crap", "crapitycrap");
		cmd.sendCommand("artist/similar", params);
	}

	@Test
	public void testLongIDCheck() throws EchoNestException {
		Params params = new Params();
		params.add("id", prefix + radiohead);
		cmd.sendCommand("artist/similar", params);
	}

	@Test
	public void testBadApiKey() throws EchoNestException {
		Commander lcmd = new Commander("test");
		Params stdParams = new Params();
		stdParams.add("api_key", "CRAPPYKEY");
		lcmd.setStandardParams(stdParams);

		Params params = new Params();
		params.add("id", radiohead);
		try {
			lcmd.sendCommand("artist/similar", params, false);
			fail();
		} catch (EchoNestException e) {
		    System.out.println("code " + e.getCode());
			assertTrue(e.getCode() == EchoNestException.ERR_MISSING_OR_INVALID_API_KEY);
		}
	}

	@Test
	public void testDupIDsFail() throws EchoNestException {
		Params params = new Params();
		params.add("api_key", "XZTXVRO3VC3FBXS8C");
		params.add("id", radiohead);
		try {
			cmd.sendCommand("artist/similar", params, false);
			fail();
		} catch (EchoNestException e) {
			assertTrue(e.getCode() == EchoNestException.ERR_INVALID_PARAMETER);
		}
	}

	@Test
	public void testDupResultsFail() throws EchoNestException {
		Params params = new Params();
		params.add("id", radiohead);
		params.add("results", 10);
		params.add("results", 20);
		try {
			cmd.sendCommand("artist/similar", params, false);
			fail();
		} catch (EchoNestException e) {
			assertTrue(e.getCode() == EchoNestException.ERR_INVALID_PARAMETER);
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testResultsReturned() throws EchoNestException {
		Params params = new Params();
		params.add("id", radiohead);
		params.add("results", 10);
		Map results = cmd.sendCommand("artist/similar", params, false);
		Map response = (Map) results.get("response");
		List artist = (List) response.get("artists");
		assertTrue(artist.size() == 10);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testResultsReturnedLarge() throws EchoNestException {
		Params params = new Params();
		params.add("id", radiohead);
		params.add("results", 100);
		Map results = cmd.sendCommand("artist/similar", params, false);
		Map response = (Map) results.get("response");
		List artist = (List) response.get("artists");
		assertTrue(artist.size() == 100);
	}

	@Test
	public void testResultsReturnedTooLarge() throws EchoNestException {

		Params params = new Params();
		params.add("id", radiohead);
		params.add("results", 1000);

		try {
			Map results = cmd.sendCommand("artist/similar", params, false);
			fail();
		} catch (EchoNestException e) {
			assertTrue(e.getCode() == EchoNestException.ERR_INVALID_PARAMETER);
		}
	}

	@Test
	public void testBadParameter() throws EchoNestException {

		Params params = new Params();
		params.add("id", radiohead);
		params.add("bad", 1000);

		try {
			Map results = cmd.sendCommand("artist/similar", params, false);
			fail();
		} catch (EchoNestException e) {
			assertTrue(e.getCode() == EchoNestException.ERR_INVALID_PARAMETER);
		}
	}

	@Test
	public void testResultsNegative() throws EchoNestException {
		Params params = new Params();
		params.add("id", radiohead);
		params.add("results", -10);
		try {
			Map results = cmd.sendCommand("artist/similar", params, false);
			fail();
		} catch (EchoNestException e) {
			assertTrue(e.getCode() == EchoNestException.ERR_INVALID_PARAMETER);
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSearchSongs() throws EchoNestException {
		Params params = new Params();
		params.add("artist", "various");
		params.add("results", 100);
		Map results = cmd.sendCommand("song/search", params);
		@SuppressWarnings("unused")
		Map response = (Map) results.get("response");
		List songList = (List) response.get("songs");
		for (int i = 0; i < songList.size(); i++) {
			Map song = (Map) songList.get(i);
			System.out.printf("%s %s\n", song.get("artist_name"), song
					.get("title"));
		}
		System.out.printf("Size %d\n", songList.size());
	}

	@Test
	public void testSearchSongsParamResultsCheck() {
		pcheck("results", "-10", false);
		pcheck("results", "100", true);
		pcheck("results", "200", false);

	}
	
	@Test
	public void testSearchSongsParamMaxTempoCheck() {
		pcheck("max_tempo", "-10", false);
		pcheck("max_tempo", "100", true);
		pcheck("max_tempo", "600", false);
	}
	
	@Test
	public void testSearchSongsParamMinTempoCheck() {
		pcheck("min_tempo", "-10", false);
		pcheck("min_tempo", "100", true);
		pcheck("min_tempo", "600", false);
	}
	
	@Test
	public void testSearchSongsParamMaxDurationCheck() {
		pcheck("max_duration", "-10", false);
		pcheck("max_duration", "100", true);
		pcheck("max_duration", "3700", true);
	}
	
	@Test
	public void testSearchSongsParamModeCheck() {
		pcheck("mode", "1", true);
		pcheck("mode", "0", true);
		pcheck("mode", "2", false);
	}
	
	
	@Test public void testSearchSongsByTempoTest() throws EchoNestException {
		sse.searchSongsByTempo("weezer", 100);
	}
	
	private void pcheck(String name, String value, boolean shouldPass) {
		Params params = new Params();
		params.add("artist", "various");
		params.add(name, value);
		try {
     		Map results = cmd.sendCommand("song/search", params);
     		assertTrue(name + "=" + value, shouldPass);
		} catch (EchoNestException e) {
     		assertTrue(name + "=" + value, !shouldPass);
		}
	}
}
