package de.unidue.ltl.ctest.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

/**
 * A data class, representing a token in a C-Test.
 * <p>
 * A {@code CTestToken} captures both content and difficulty of the token it represents.<br>
 * A token is basically a string of text with some additional properties.
 * It may or may not have a gap or not. 
 * The token's gap index marks the start index of the gap.
 * A gap index may be present, even if the token is not gapped. 
 * The gap type determines the position of the gap (at the start or the end of the token).<br>
 * The error rate is an indicator for the token's difficulty.
 * 
 * @see CTestObject
 * @see GapType
 */
public class CTestToken implements Serializable {
	private static final long serialVersionUID = 3L;
	
	// Core Properties
	private String text;
	private boolean gap;
	private GapType gapType = GapType.POSTFIX;
	private int gapIndex = -1;
	private String prompt;
	private List<String> otherSolutions;

	// Meta Information
	private String id;
	private boolean isValidCandidate = true;
	private boolean lastTokenInSentence;

	// Difficulty Prediction Variables
	private Double errorRate;
	private Double prediction;
	
	/**
	 * Creates an ungapped {@code CTestToken}, based on the given text.
	 * 
	 * @param text the text, representing the token. Should be a single word, not null.
	 */
	public CTestToken(String text) {
		id = UUID.randomUUID().toString();
		gap = false;
		initialize(text, null, null);
	}
	

	/**
	 * Creates a gapped {@code CTestToken}, based on the given text.
	 * 
	 * @param text the text, representing the token. Should be a single word, not null.
	 * @param prompt the visible (ungapped) part of the token. Should be a substring of {@code text}.
	 * @param errorRate the recoreded errorRate for the token, not null.
	 * @param otherSolutions valid alternative solutions, if appended to {@code prompt}. 
	 */
	public CTestToken(String text, String prompt, Double errorRate, String ... otherSolutions) {
		gap = true;
		initialize(text, prompt, errorRate, otherSolutions);
	}

	private void initialize(String text, String prompt, Double errorRate, String ... otherSolutions) {
		this.text = text;
		this.prompt = prompt;
		this.errorRate = errorRate;
		this.otherSolutions = new ArrayList<String>();
		this.otherSolutions.addAll(Arrays.asList(otherSolutions));
	}

	@Override
	public String toString() {
		if (isGap()) {
			StringBuilder sb = new StringBuilder();
			sb.append(text);
			sb.append("\t");
			sb.append(id);
			sb.append("\t");
			sb.append(prompt);
			sb.append("\t");
			sb.append(errorRate);
			sb.append("\t");
			sb.append(gapType);
			sb.append("\t");
			sb.append(gapIndex);
			sb.append("\t");
			sb.append(isValidCandidate);
			sb.append("\t");
			sb.append(StringUtils.join(otherSolutions, "/"));
			
			return sb.toString();			
		}
		else {
			return text;
		}
	}
	
	/**
	 * Returns the entire token.
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the text of the token.
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Indicates, whether the token is gapped.
	 */
	public boolean isGap() {
		return gap;
	}

	/**
	 * Sets the gap status of the token.
	 * 
	 * @param gap the gap status, true means that the token is gapped.
	 */
	public void setGap(boolean gap) {
		this.gap = gap;
	}
	
	/**
	 * Returns the token's gap index. The index marks the beginning of the gap. 
	 */
	public int getGapIndex() {
		return this.gapIndex;
	}
	
	/**
	 * Sets the token's gap index.
	 * 
	 * @param index the index. If it exceeds the token's text length, it is set to -1.
	 */
	public void setGapIndex(int index) {
		if (index > -1 && index < this.text.length()) {
			this.gapIndex = index;
			this.prompt = this.text.substring(0, index);
		}
		else
			this.gapIndex = -1;
	}
	
	/**
	 * Returns the type of gap of the token. 
	 */
	public GapType getGapType() {
		return this.gapType;
	}
	
	/**
	 * Sets the gap type of the token, based on the given string. <br>
	 * If the string does not represent a valid gap type, nothing is changed.
	 * 
	 * @see GapType
	 */
	public void setGapType(String type) {
		if (type.equals(GapType.POSTFIX.toString())) {
			this.gapType = GapType.POSTFIX; 
			return;
		}
		
		if (type.equals(GapType.PREFIX.toString())) {
			this.gapType = GapType.PREFIX; 
			return;
		}
	}
	
	/**
	 * Sets the gap type of the token.
	 * 
	 * @see GapType
	 */
	public void setGapType(GapType type) {
			this.gapType = type;
	}
	
	/**
	 * Returns the prompt of the token.<br> 
	 * The prompt is the visible part of a gapped token, i.e. "invi" in "invi_____" (invisible).
	 */
	public String getPrompt() {
		if (prompt != null)
			return prompt;
		
		if (gapIndex < 0)
			return "";
		
		return text.substring(0, gapIndex);
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
		this.setGapIndex(prompt.length());
	}
	
	/**
	 * Returns the primary solution to the token.
	 */
	public String getPrimarySolution() {
		if (prompt != null)
			return text.substring(prompt.length());
		
		if (gapIndex < 0)
			return text;
		
		return text.substring(gapIndex);
	}
	
	/**
	 * Returns all valid solutions to the token.
	 */
	public List<String> getAllSolutions() {
		List<String> solutions = new ArrayList<>();
		solutions.add(getPrimarySolution());
		solutions.addAll(otherSolutions);
		return solutions;
	}
	
	
	/**
	 * Returns valid solutions to the token, other than the primary solution.
	 */
	public List<String> getOtherSolutions() {
		return otherSolutions;
	}
	
	/**
	 * Indicates whether other solutions, other than the primary solution.
	 */
	public boolean hasOtherSolutions() {
		return !getOtherSolutions().isEmpty();
	}
	
	/**
	 * Sets other solutions.
	 * 
	 * @param otherSolutions a list of solutions. Each solution is a string that could fill the gapped part of the token, not null.
	 */
	public void setOtherSolutions(List<String> otherSolutions) {
		this.otherSolutions = otherSolutions;
	}

	/**
	 * Returns whether or not the token is a valid candidate.
	 */
	public boolean isCandidate() {
		return this.isValidCandidate;
	}	
	
	/**
	 * Sets the candidate status of the token.
	 */
	public void setCandidate(boolean isCandidate) {
		this.isValidCandidate = isCandidate;
	}
	
	/**
	 * Returns the error rate for the token.
	 */
	public Double getErrorRate() {
		return errorRate;
	}

	/**
	 * Sets the error rate.
	 */
	public void setErrorRate(Double errorRate) {
		this.errorRate = errorRate;
	}

	/**
	 * Returns the predicted error rate for the token.
	 */
	public Double getPrediction() {
		return prediction;
	}

	/**
	 * Sets the predicted error rate for the token.
	 */
	public void setPrediction(Double prediction) {
		this.prediction = prediction;
	}

	/**
	 * Indicates whether the token is the last token in a sentence. Usually this means that the token text is punctuation at the ent of a sentence.
	 */
	public boolean isLastTokenInSentence() {
		return lastTokenInSentence;
	}

	/**
	 * Sets the status of the token as last token in the sentence.
	 */
	public void setLastTokenInSentence(boolean lastTokenInSentence) {
		this.lastTokenInSentence = lastTokenInSentence;
	}

	/**
	 * Returns the token's id.
	 * By default, this represents a type 4 UUID.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the token's id.
	 * 
	 * @param id should be a type 4 UUID.
	 */
	public void setId(String id) {
		this.id = id;
	}
}