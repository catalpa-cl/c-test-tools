package de.unidue.ltl.ctest.io.results;

/**
 * A class representing the results for a single {@code CTestToken} in a C-Test. 
 */
public class TokenTestResult {
	private int solved = 0;
	private int total = 0;
	
	public int getSolved() {
		return solved;
	}
	
	public int getTotal() {
		return total;
	}
	
	public double getSolveRate() {
		if (total == 0)
			return 0.0;
		
		return (double) solved / (double) total;
	}	
	
	public double getErrorRate() {
		return 1.0 - getSolveRate();
	}
	
	public void addSolved() {
		solved++;
		total++;
	}
	
	public void addError() {
		total++;
	}
	
	public void addResult(TokenTestResult other) {
		solved += other.solved;
		total += other.total; 
	}
}
