package de.unidue.ltl.ctest.core;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unidue.ltl.ctest.type.Gap;
import de.unidue.ltl.ctest.util.Transformation;

//TODO: Rewrite in Version 0.0.2-SNAPSHOT: remove unnecessary properties etc.
/**
 * A data class, representing a C-Test.
 * <p>
 * A {@code CTestObject} captures both the content and the difficulty of a C-Test.
 * It is basically a list of {@code CTestToken}s with some additional getters for convenience.
 * 
 * @see CTestToken
 */
public class CTestObject implements Serializable {	
	public static final String SENT_BOUNDARY = "----";
	public static final String COMMENT = "%%";
	
	public static final double EASY_CUTOFF = 0.3;
	public static final double MEDIUM_CUTOFF = 0.6;
	
	private static final long serialVersionUID = 3L;
	
	public static CTestObject fromTokens(Iterable<CTestToken> tokens) {
		return Transformation.toCTest(tokens);
	}
	
	private List<CTestToken> tokens;
	private String language;
	private int nrOfGaps;
	private String id;
	
	/**
	 * Creates a new {@code CTestObject}.
	 */
	public CTestObject() {
		this.language = "unknown";
		this.tokens = new ArrayList<>();
		this.nrOfGaps = 0;
		this.id = UUID.randomUUID().toString();
	}

	/**
	 * Creates a new {@code CTestObject}.
	 * 
	 * @param language the language of the C-Test. Should be a two-letter language code, not null.
	 */
	public CTestObject(String language) {
		this();
		this.language = language;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(COMMENT + " " + language + "\t" + getGapCount() + "\n");
		if (id != null) {
			sb.append(COMMENT + " " + id + "\n");			
		}
		
		for (CTestToken token : tokens) {
			sb.append(token);
			if (token.isLastTokenInSentence()) {
				sb.append("\n");
				sb.append(CTestObject.SENT_BOUNDARY);
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	/**
	 * Returns the id of the {@code CTestObject}.
	 * Initially this represents a type 4 UUID.
	 */
	public String getId() {
		return id;
	}


	/**
	 * Sets the id of the {@code CTestObject}.
	 *  
	 * @param id should be a type 4 UUID.
	 */
	public void setId(String id) {
		this.id = id;
	}
	

	/**
	 * Returns the language of the {@code CTestObject}.
	 */
	public String getLanguage() {
		return language;
	}
	
	/**
	 * sets the language of the {@code CTestObject}. 
	 */
	public void setLanguage(String language) {
		this.language = language;
	}
	
	/**
	 * Adds a {@code CTestToken} to the {@code CTestObject}.
	 * 
	 * @param token the token, not null.
	 */
	public void addToken(CTestToken token) {
		if (token.isGap()) {
			nrOfGaps++;
		}
		tokens.add(token);
	}
	
	/**
	 * Adds multiple {@code CTestToken}s to the {@code CTestObject}.
	 * 
	 * @param tokens an {@code Iterable} of tokens, not null.
	 */
	public void addTokens(Iterable<CTestToken> tokens) {
		for (CTestToken token: tokens) {
			addToken(token);
		}
	}
	
	/**
	 * Returns a List of all {@code CTestToken}s of the {@code CTestObject}.
	 */
	public List<CTestToken> getTokens() {
		return tokens;
	}
	

	/**
	 * Sets the {@code CTestToken}s of the {@code CTestObject} to the ones given.
	 * 
	 * @param tokens an {@code Iterable} of tokens, not null.
	 */
	public void setTokens(Iterable<CTestToken> tokens) {
		this.nrOfGaps = 0;
		this.tokens = new ArrayList<>();
		this.addTokens(tokens);
	}

	/**
	 * Returns the {@code CTestToken} at the specified index.
	 * 
	 * @param gapIndex the index of the token. Must be a valid index of the list of gapped tokens.
	 * @return  the token, null if the given index is out of bounds.
	 */
	public CTestToken getGappedToken(int gapIndex) {
		if (gapIndex < 0)
			return null;
		
		List<CTestToken> gappedTokens = getGappedTokens();
		if (gapIndex >= gappedTokens.size())
			return null;
		
		return gappedTokens.get(gapIndex);
	}

	/**
	 * Returns a List of all gapped {@code CTestToken}s of the {@code CTestObject}.
	 */
	public List<CTestToken> getGappedTokens() {
		return this.tokens.stream()
				.filter(token -> token.isGap())
				.collect(Collectors.toList());
	}
	
	/**
	 * Returns the number of gapped tokens in the C-Test.
	 */
	public int getGapCount() {
		return getGappedTokens().size();
	}
	
	/**
	 * Initializes the given {@code JCas} with the values from this {@code CTestObject}.
	 *
	 * @deprecated use {@link de.unidue.ltl.ctest.util.Transformation#toJCas(CTestObject)} instead.
	 */
	@Deprecated
	public void initializeJCas(JCas jcas) 
	{
		int offset = 0;
		StringBuilder sb = new StringBuilder();
		
		for (CTestToken token : tokens) {
			String gapText = token.getText();
			sb.append(gapText);
			sb.append(" ");
			int begin = offset;
			int end = offset + gapText.length();
			
			Token t = new Token(jcas, begin, end);
			t.addToIndexes();

	        offset += gapText.length() + 1;

			if (token.isGap()) {
				Gap g = new Gap(jcas, t.getBegin(), t.getEnd());
				g.setSolutions(getSolutionArray(jcas, g, gapText, token.getOtherSolutions()));
		        g.setDifficulty(token.getPrediction());
		        g.setPrefix(token.getPrompt());
		        g.addToIndexes();  
			}
		}
		
		jcas.setDocumentLanguage(language);
		jcas.setDocumentText(sb.toString());
	}
	
	private StringArray getSolutionArray(JCas jcas, Gap g, String gapText, List<String> solutions) {
		StringArray arr = new StringArray(jcas, solutions.size() + 1);
		arr.set(0, gapText);
		for (int i=1; i<solutions.size(); i++) {
			arr.set(i, solutions.get(i));
		}
		return arr;
	}
	
	/**
	 * Intitializes this {@CTestObject} with the C-Test at the given file location. <br>
	 * File must encode the C-Test in the C-Test File Format.
	 * 
	 * @deprecated use {@link de.unidue.ltl.ctest.CTestFileReader.read(File)} instead.
	 */
	@Deprecated
	public void initializeFromFile(File inputFile) 
			throws IOException
	{
		this.setId(inputFile.getName());
		
	    for (String line : FileUtils.readLines(inputFile)) {   
	    	String[] split = line.split("\t");
	    	
	    	if (line.equals(SENT_BOUNDARY)) {
	    		tokens.get(tokens.size()-1).setLastTokenInSentence(true);
	    	}
	    	else if (!line.startsWith(COMMENT)) {
		    	CTestToken token = new CTestToken(split[0]);
	    		if (split.length >=5) {
	    			nrOfGaps++;
	    			
		    		token.setGap(true);
		    		token.setId(split[1]);
		    		token.setPrompt(split[2]);
			    	token.setErrorRate(Double.parseDouble(split[3]));
			    	token.setGapType(split[4]);
			    	token.setGapIndex(Integer.parseInt(split[5]));
		    		
		    		List<String> solutions = new ArrayList<>();
		    		for (int i=6; i<split.length; i++) {
		    			solutions.add(split[i]);
		    		}
		    		token.setOtherSolutions(solutions);
		    	}
		    	else {
			    	token.setGap(false);
		    	}
		    	tokens.add(token);
	    	}
	    }
	}
	
	/**
	 * Returns a List of predictions for all {@code CTestToken}s of the {@code CTestObject}.
	 */
	public List<Double> getPredictions() {
		return getGappedTokens().stream()
				.map(token -> token.getPrediction())
				.collect(Collectors.toList());
	}
	
	/**
	 * Sets the prediction of the {@code CTestToken} at the given index to the specified value. 
	 * A convenience method, equal to {@code CTestObject.getGappedToken(i).setPrediction(v)}.
	 * 
	 * @param prediction the predicted difficulty value, not null.
	 * @param gapIndex the index of the token to modify.
	 * @return a boolean indicating, whether the prediction was successfully set.
	 */
	public boolean setPrediction(Double prediction, int gapIndex) {
		CTestToken token = getGappedToken(gapIndex);
		if (token == null)
			return false;
		
		token.setPrediction(prediction);
		return true;
	}
	
	/**
	 * Returns the average predicted difficulty for all gapped tokens.
	 */
	public double getOverallDifficulty() {
		return getGappedTokens().stream()
				.map(token -> token.getPrediction())
				.collect(Collectors.averagingDouble(val -> val));
	}
	
	// TODO: also handle non-gapped tokens
	/**
	 * Sets the ids of gapped tokens in the C-Test to their position in the C-Test, such that the ith token in the C-Test has the id i.
	 */
	public void reindexGaps() {
		List<CTestToken> tokens = getGappedTokens();
		int max = getGapCount();
		for (int i = 0; i < max; i++) {
			CTestToken token = tokens.get(i);
			token.setId(Integer.toString(i));
		}
	}
	
	/**
	 * Sets the ids of gapped tokens in the C-Test to their position in the C-Test, offset by the given number.
	 */
	public void reindexGaps(int offset) {
		List<CTestToken> tokens = getGappedTokens();
		int max = getGapCount() + offset;
		for (int i = offset; i < max; i++) {
			CTestToken token = tokens.get(i - offset);
			token.setId(Integer.toString(i));
		}
	}
}