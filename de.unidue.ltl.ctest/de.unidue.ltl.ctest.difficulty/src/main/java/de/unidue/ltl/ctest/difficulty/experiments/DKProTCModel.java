package de.unidue.ltl.ctest.difficulty.experiments;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.type.TextClassificationOutcome;

import de.unidue.ltl.ctest.core.CTestObject;
import de.unidue.ltl.ctest.util.Transformation;

public class DKProTCModel implements Model {

	private AnalysisEngine estimator;
	private AnalysisEngine preprocessing;
	
	public DKProTCModel(AnalysisEngine estimator) {
		this.setEstimator(estimator);
	}
	
	public AnalysisEngine getEstimator() {
		return estimator;
	}

	public void setEstimator(AnalysisEngine estimator) {
		//TODO: Typecheck for TcAnnotator
		this.estimator = estimator;
	}

	public void setPreprocessing(AnalysisEngine preprocessing) {
		this.preprocessing = preprocessing;
	}
	
	@Override
	public List<Double> predict(CTestObject ctest) {
		try {
			JCas jcas = Transformation.toJCas(ctest);
			if (preprocessing != null) {
				preprocessing.process(jcas);
			}
			estimator.process(jcas);
			return JCasUtil.select(jcas, TextClassificationOutcome.class)
					.stream()
					.map(outcome -> Double.parseDouble(outcome.getOutcome()))
					.collect(Collectors.toList());
		} 
		catch (AnalysisEngineProcessException e) {
			System.out.println("Could not process the JCas with the estimator.");
			e.printStackTrace();
		} 
		catch (UIMAException e) {
			System.out.println("Could not transform given C-Test to JCas format.");
			e.printStackTrace();
		}
		
		return new ArrayList<Double>();
	}
}
