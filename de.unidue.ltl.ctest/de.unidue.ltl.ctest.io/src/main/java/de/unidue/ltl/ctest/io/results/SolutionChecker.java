package de.unidue.ltl.ctest.io.results;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.unidue.ltl.ctest.core.CTestObject;
import de.unidue.ltl.ctest.core.CTestToken;

//TODO: Find proper place.
//TODO: Create Own DataStructure, to simplify.
/**
 * A class checking whether solutions for C-Tests are correct. 
 * May be used to update the Error Rates of {@code CTestTokens} in a {@code CTestObject}.
 */
public class SolutionChecker {
	
	private CTestObject ctest;
	private List<CTestToken> gappedTokens;
	private List<TokenTestResult> results;
	private List<List<String>> correctResults;
	private List<List<String>> solutions;
	
	public void set(CTestObject ctest) {
		this.ctest = ctest;
		
		this.gappedTokens = ctest.getTokens().stream()
				.filter(token -> token.isGap())
				.collect(Collectors.toList());
		
		this.correctResults = gappedTokens.stream()
				.map(this::getAllSolutions)
				.collect(Collectors.toList());
		
		this.results = gappedTokens.stream()
				.map(token -> new TokenTestResult())
				.collect(Collectors.toList());
		
		this.solutions = new ArrayList<>();
		this.gappedTokens.forEach(token -> this.solutions.add(new ArrayList<String>()));
	}
	
	public List<TokenTestResult> getTestResults() {
		return this.results;
	}
	
	public CTestObject applyTestResults() {
		for (int i = 0; i < this.gappedTokens.size(); i++) {
			CTestToken token = this.gappedTokens.get(i);
			TokenTestResult result = this.results.get(i);
			token.setErrorRate(result.getErrorRate());
		}
		
		return this.ctest;			
	}
	
	//TODO: Move to CTestToken
	private List<String> getAllSolutions(CTestToken token) {
		List<String> solutions = new ArrayList<>();
		
		if (!token.isGap())
			return solutions;
		
		if (token.getGapIndex() >= 0)
			solutions.add(token.getText().substring(token.getGapIndex()));
		
		solutions.addAll(token.getOtherSolutions());
		
		return solutions;
	}
	
	public void addSolution(int i, String solution) {
		if (i >= gappedTokens.size() - 1) 
			return;
		
		this.solutions.get(i).add(solution);
		
		if (isCorrect(i, solution))
			this.results.get(i).addSolved();
		else 
			this.results.get(i).addError();
	}
	
	public void addSolutions(List<String> solutions) {
		if (this.gappedTokens.size() != solutions.size())
			throw new IllegalArgumentException(""
					+ "Gapped Tokens in CTest must be equal to results!"
					+ "results: " + results.size() + ""
					+ "tokens: " + gappedTokens.size());
		
		for (int i = 0; i < this.solutions.size(); i++)
			addSolution(i, solutions.get(i));
	}
	
	private boolean isCorrect(int i, String solution) {	
		return this.correctResults.get(i).contains(solution);
	}
	
}
