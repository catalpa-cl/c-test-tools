package de.unidue.ltl.ctest.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unidue.ltl.ctest.core.CTestObject;
import de.unidue.ltl.ctest.core.CTestToken;
import de.unidue.ltl.ctest.type.Gap;
import de.unidue.ltl.ctest.util.Transformation;

public class TransformationTest {

	private CTestObject ctest;
	private List<CTestToken> tokens;
	private List<CTestToken> gappedTokens;
	
	public TransformationTest() {
		this.ctest = new CTestObject("none");
		this.tokens = new ArrayList<>();
		this.gappedTokens = new ArrayList<>();
		
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
			this.ctest.addToken(token);
		}
		this.tokens.get(4).setLastTokenInSentence(true);
	}
	
	@Test
	public void testToCTestToken() {
		List<String> otherSolutions = new ArrayList<>();
		otherSolutions.add("great");
		otherSolutions.add("splendid");
		
		CTestToken token = new CTestToken("this_should_work");
		CTestToken copy = Transformation.toCTestToken(token.toString());
		
		assertEquals(token.toString(), copy.toString());
		
		token.setId("0");
		token.setGap(true);
		token.setGapIndex(12);
		token.setOtherSolutions(otherSolutions);
		token.setErrorRate(9000.1);
		token.setPrediction(0.0);
		
		copy = Transformation.toCTestToken(token.toString());
		
		assertEquals(token.toString(), copy.toString());
		
		String foo = "foo";
		
		try {
			Transformation.toCTestToken(foo);
		} catch (IllegalArgumentException e) {
			fail("Should have not thrown an IllegalArgumentException");
		}
	}
	
	@Test
	public void testToIOSFormat() {
		List<String> otherSolutions = new ArrayList<>();
		otherSolutions.add("great");
		otherSolutions.add("splendid");
		
		CTestToken token = new CTestToken("this_should_work");
		
		assertEquals("this_should_work", Transformation.toIOSFormat(token));
		
		token.setId("0");
		token.setGap(true);
		token.setGapIndex(12);
		token.setOtherSolutions(otherSolutions);
		token.setErrorRate(9000.1);
		token.setPrediction(0.0);
		
		assertEquals("this_should_{work,great,splendid}", Transformation.toIOSFormat(token));
	}
	
	@Test
	public void testToJCas() throws UIMAException {
		JCas jcas = Transformation.toJCas(this.ctest);
		List<Sentence> sentences = new ArrayList<>(JCasUtil.select(jcas, Sentence.class));
		List<Token> tokens = new ArrayList<>(JCasUtil.select(jcas, Token.class));
		List<Gap> gaps = new ArrayList<> (JCasUtil.select(jcas, Gap.class));
		
		assertEquals(1, sentences.size());
		assertEquals("token0 token1 token2 token3 token4", sentences.get(0).getCoveredText());
		assertEquals(this.tokens.size(), tokens.size());
		assertEquals(this.gappedTokens.size(), gaps.size());
		
		for (int i = 0; i < this.tokens.size(); i++) {
			CTestToken ctoken = this.tokens.get(i);
			Token token = tokens.get(i);
			assertEquals(ctoken.getText(), token.getCoveredText());
		}
		
		for (int i = 0; i < this.gappedTokens.size(); i++) {
			CTestToken ctoken = this.gappedTokens.get(i);
			Gap gap = gaps.get(i);
			assertEquals(ctoken.getText(), gap.getCoveredText());
			assertEquals(ctoken.getPrompt(), gap.getPrefix());
			assertEquals(ctoken.getErrorRate(), new Double(gap.getErrorRate()));
			assertEquals(ctoken.getPrediction(), new Double(gap.getDifficulty()));
		}
	}
	
}
