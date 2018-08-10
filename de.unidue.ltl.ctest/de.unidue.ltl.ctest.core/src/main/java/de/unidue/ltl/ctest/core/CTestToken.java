package de.unidue.ltl.ctest.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;


/**
 * Represents a token in a ctest.
 * A token can either have a gap or not.
 */
public class CTestToken {

	private String text;
	private String id;
	private boolean gap;
	private String prompt;
	private List<String> otherSolutions;
	private Double errorRate;
	private Double prediction;
	private boolean lastTokenInSentence;
	
	public CTestToken(String text) {
		gap = false;
		initialize(text, null, null);
	}
	
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
			sb.append(StringUtils.join(otherSolutions, "/"));
			
			return sb.toString();			
		}
		else {
			return text;
		}
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isGap() {
		return gap;
	}

	public void setGap(boolean gap) {
		this.gap = gap;
	}

	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public List<String> getOtherSolutions() {
		return otherSolutions;
	}

	public void setOtherSolutions(List<String> otherSolutions) {
		this.otherSolutions = otherSolutions;
	}

	public Double getErrorRate() {
		return errorRate;
	}

	public void setErrorRate(Double errorRate) {
		this.errorRate = errorRate;
	}

	public Double getPrediction() {
		return prediction;
	}

	public void setPrediction(Double prediction) {
		this.prediction = prediction;
	}

	public boolean isLastTokenInSentence() {
		return lastTokenInSentence;
	}

	public void setLastTokenInSentence(boolean lastTokenInSentence) {
		this.lastTokenInSentence = lastTokenInSentence;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}