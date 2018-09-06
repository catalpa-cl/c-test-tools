package de.unidue.ltl.ctest.core;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unidue.ltl.ctest.type.Gap;

//TODO: Implement equals
public class CTestObject implements Serializable {	
	public static final String SENT_BOUNDARY = "----";
	public static final String COMMENT = "%%";
	
	public static final double EASY_CUTOFF = 0.3;
	public static final double MEDIUM_CUTOFF = 0.6;
	
	private static final long serialVersionUID = 1L;
	
	private List<CTestToken> tokens;
	private String language;
	private int nrOfGaps;
	private String id;
	
	public CTestObject(String language) 
	{	
		this.language = language;
		this.nrOfGaps = 0;
		this.tokens = new ArrayList<>();
		this.id = null;
	}
	
	public List<CTestToken> getTokens() {
		return tokens;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(COMMENT + " " + language + "\t" + nrOfGaps + "\n");
		if (id != null) {
			sb.append(COMMENT + " " + id);			
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

	//TODO: Rename to 'getAverageTokenDifficulty'?
	public double getOverallDifficulty() {
		double sumOfPredictions = 0.0;
		for (CTestToken token : tokens) {
			if (token.isGap()) {
				sumOfPredictions += token.getPrediction();				
			}
		}
		return sumOfPredictions / nrOfGaps;
	}
	
	//TODO: Create Transformer class?
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
	
	//TODO: Remove and update References with CTestFileReader.read(inputFile).
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
	
	public void addToken(CTestToken token) {
		if (token.isGap()) {
			nrOfGaps++;
		}
		tokens.add(token);
	}
	
	public void addTokens(Iterable<CTestToken> tokens) {
		for (CTestToken token: tokens) {
			addToken(token);
		}
	}

	public List<Double> getPredictions() {
		List<Double> predictions = new ArrayList<>();
		
		for (CTestToken token : tokens) {
			if (token.isGap()) {
				predictions.add(token.getPrediction());
			}
		}
		return predictions;
	}
	
	public boolean setPrediction(Double prediction, int gapOffset) {
		int counter = 0;
		for (CTestToken token : tokens) {
			if (token.isGap()) {
				if (counter == gapOffset) {
					token.setPrediction(prediction);
					return true;
				}
				counter++;
			}
		}
		return false;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}