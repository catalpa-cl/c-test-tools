package de.unidue.ltl.ctest.difficulty.experiments;

import java.util.List;

import de.unidue.ltl.ctest.core.CTestObject;

public interface Model {
	/**
	 * Predicts the difficulties for each gap in the CTest and returns the difficulty values.
	 */
	public List<Double> predict(CTestObject ctest);
}
