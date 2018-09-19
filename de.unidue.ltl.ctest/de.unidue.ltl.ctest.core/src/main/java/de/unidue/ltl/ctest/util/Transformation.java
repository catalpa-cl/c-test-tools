package de.unidue.ltl.ctest.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unidue.ltl.ctest.core.CTestObject;
import de.unidue.ltl.ctest.core.CTestToken;
import de.unidue.ltl.ctest.type.Gap;

public class Transformation {
	
	/**
	 * Transforms the given text to a {@code CTestToken}.
	 * 
	 * @param text the text to be converted. Must be in the same format as the output of {@code CTestToken.toString()}, not null.
	 * @return the CTestToken.
	 * 
	 * @throws IllegalArgumentException if text does not represent a {@code CTestToken}.
	 */
	public static CTestToken toCTestToken (String text) {
		if (text == null)
			throw new IllegalArgumentException("Input text must not be null!");
		
		String[] tokenInfo = text.split("\t");
		
		CTestToken token = new CTestToken(tokenInfo[0]);
		
		if (tokenInfo.length >= 6) {
			token.setGap(true);
			token.setId(tokenInfo[1].trim());
			token.setPrompt(tokenInfo[2].trim());
			token.setGapType(tokenInfo[4].trim());
			try {
				token.setErrorRate(Double.parseDouble(tokenInfo[3].trim()));
				token.setGapIndex(Integer.parseInt(tokenInfo[5].trim()));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Input does not represent a CTestToken. Text: " + text);
			}
		}
		if (tokenInfo.length >= 7) {
			List<String> solutions = new ArrayList<>();
			for (String solution : tokenInfo[6].split("/")) {
				solutions.add(solution.trim());
			}
			token.setOtherSolutions(solutions);
		}
		
		return token;
	}
	
	/**
	 * Transforms the given token to a string in the IOS C-Test Format.
	 */
	public static String toIOSFormat(CTestToken token) {
		if (!token.isGap())
			return token.getText();
		
		String text = token.getText();
		String wordBase = text.substring(0, token.getGapIndex());
		String solution = text.substring(token.getGapIndex());
		String solutions = "{" + solution + "}";
		
		if (token.hasOtherSolutions())
			solutions = new StringBuilder("")
				.append("{")
				.append(solution)
				.append(",")
				.append(String.join(",", token.getOtherSolutions()))
				.append("}")
				.toString();
		
		return wordBase + solutions;
	}
	
	/**
	 * Converts the given ctest into a jcas.
	 * 
	 * @param ctest the ctest to be converted.
	 * @return a jcas.
	 * 
	 * @throws UIMAException if the initial jcas cannot be created.
	 */
	public static JCas toJCas(CTestObject ctest) throws UIMAException {

		JCas jcas = JCasFactory.createJCas();
		
		int offset = 0;
		StringBuilder docText = new StringBuilder();
		
		for (CTestToken ctoken : ctest.getTokens()) {
			String gapText = ctoken.getText();
			docText.append(gapText);
			docText.append(" ");
			int begin = offset;
			int end = offset + gapText.length();
			
			Token token = new Token(jcas, begin, end);
			token.addToIndexes();

	        offset += gapText.length() + 1;

			if (ctoken.isGap()) {
				Gap g = new Gap(jcas, token.getBegin(), token.getEnd());
				g.setSolutions(getSolutionArray(jcas, ctoken.getAllSolutions()));
		        g.setDifficulty(ctoken.getPrediction());
		        g.setPrefix(ctoken.getPrompt());
		        g.addToIndexes();  
			}
		}
		
		jcas.setDocumentLanguage(ctest.getLanguage());
		jcas.setDocumentText(docText.toString());
		
		return jcas;
	}
	
	private static StringArray getSolutionArray(JCas jcas, List<String> solutions) {
		StringArray arr = new StringArray(jcas, solutions.size());
		for (int i = 0; i < solutions.size(); i++) {
			arr.set(i, solutions.get(i));
		}
		return arr;
	}
}
