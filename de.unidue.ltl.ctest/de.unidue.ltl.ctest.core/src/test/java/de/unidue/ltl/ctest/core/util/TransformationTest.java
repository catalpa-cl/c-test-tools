package de.unidue.ltl.ctest.core.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

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
	}
	
	@Test
	public void testToJCas() throws UIMAException {
		JCas jcas = Transformation.toJCas(this.ctest);
		List<Token> tokens = new ArrayList<>(JCasUtil.select(jcas, Token.class));
		List<Gap> gaps = new ArrayList<> (JCasUtil.select(jcas, Gap.class));
		
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
