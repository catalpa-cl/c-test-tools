package de.unidue.ltl.ctest.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class CTestObjectTest {
	
	private CTestObject ctest;
	private List<CTestToken> tokens;
	private List<CTestToken> gappedTokens;
	private List<CTestToken> otherTokens;
	private List<CTestToken> otherGappedTokens;
	
	public CTestObjectTest() {
		this.tokens = new ArrayList<CTestToken>();
		this.gappedTokens = new ArrayList<CTestToken>();
		this.otherTokens = new ArrayList<CTestToken>();
		this.otherGappedTokens = new ArrayList<CTestToken>();
		
		for (int i = 0; i < 5; i++) {
			CTestToken token = new CTestToken("token" + i);
			if (i % 2 == 0) {	
				token.setId(Integer.toString(i));
				token.setGap(true);
				token.setGapIndex(i);
				token.setErrorRate(0.0);
				token.setPrediction(0.0);
				this.gappedTokens.add(token);
			}
			this.tokens.add(token);
		}
		
		for (int i = 0; i < 6; i++) {
			CTestToken token = new CTestToken("otherToken" + i);
			if (i % 2 == 0) {	
				token.setId(Integer.toString(i));
				token.setGap(true);
				token.setGapIndex(i);
				token.setErrorRate(0.0);
				token.setPrediction(0.0);
				this.otherGappedTokens.add(token);
			}
			this.otherTokens.add(token);
		}
	}
	
	@Before
	public void before() throws Exception {
		this.ctest = new CTestObject("UNKNOWN");
	}
	
	@Test
	public void testTokenAddition() {
		assertEquals(0, ctest.getGapCount());
		
		CTestToken token = new CTestToken("not_a_gap");
		ctest.addToken(token);
		assertEquals(0, ctest.getGapCount());
		assertEquals(1, ctest.getTokens().size());
		
		ctest.addToken(gappedTokens.get(0));
		assertEquals(1, ctest.getGapCount());
		assertEquals(2, ctest.getTokens().size());
		
		ctest.addTokens(gappedTokens);
		assertEquals(gappedTokens.size() + 1, ctest.getGapCount());
		assertEquals(gappedTokens.size() + 2, ctest.getTokens().size());
		
	}
	
	@Test
	public void testSettersAndGetters() {
		String lang = "hu";
		ctest.setLanguage(lang);
		assertEquals(lang, ctest.getLanguage());
		
		String id = "xalq";
		ctest.setId(id);
		assertEquals(id, ctest.getId());
		
		ctest.addTokens(tokens);
		
		assertEquals(tokens.size(), ctest.getTokens().size());
		assertEquals(gappedTokens.size(), ctest.getGappedTokens().size());
		assertEquals(gappedTokens.size(), ctest.getGapCount());
		
		for (int i = 0; i < gappedTokens.size(); i++) {
			assertEquals(gappedTokens.get(i).getText(), ctest.getGappedToken(i).getText());
		}
		
		ctest.setTokens(otherTokens);
		assertEquals(otherTokens.size(), ctest.getTokens().size());
		assertEquals(otherGappedTokens.size(), ctest.getGapCount());
		

		for (int i = 0; i < otherTokens.size(); i++) {
			assertEquals(otherTokens.get(i).getText(), ctest.getTokens().get(i).getText());
		}
		
		ctest.addTokens(tokens);
		assertEquals(tokens.size() + otherTokens.size(), ctest.getTokens().size());
		assertEquals(gappedTokens.size() + otherGappedTokens.size(), ctest.getGapCount());
	}
	
	@Test
    public void testPredictions() throws Exception
    {
		ctest.setTokens(gappedTokens);
		
		int tokens = gappedTokens.size();
		List<Double> predictions = new ArrayList<>();
		double p = 1.0;
		for(int i = 0; i < tokens; i++){
			assertTrue(ctest.setPrediction(p, i));
			predictions.add(p);
			p += 1.0;
		}
		
		Double average = p / (tokens - 1);
		assertTrue(average == ctest.getOverallDifficulty());
		
		for (int i = 0; i < tokens; i++)
			assertTrue(predictions.get(i).equals(ctest.getPredictions().get(i)));
    }
}
