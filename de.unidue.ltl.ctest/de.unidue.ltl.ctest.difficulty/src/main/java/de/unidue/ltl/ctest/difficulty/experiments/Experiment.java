package de.unidue.ltl.ctest.difficulty.experiments;

import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.dkpro.tc.api.features.TcFeatureSet;

public class Experiment {
	
	private String experimentName;
	private List<AnalysisEngineDescription> preprocessingEngines;
	private TcFeatureSet features;
	private boolean isRegression;
	
	public String getExperimentName() {
		return experimentName;
	}
	public void setExperimentName(String name) {
		experimentName = name;
	}
	
	public List<AnalysisEngineDescription> getPreprocessing() {
		return preprocessingEngines;
	}
	public void setPreprocessing(List<AnalysisEngineDescription> engines) {
		this.preprocessingEngines = engines;
	}
	
	public TcFeatureSet getFeatureSet() {
		return features;
	}
	public void setFeatureSet(TcFeatureSet features) {
		this.features = features;
	}
	
	public boolean isRegression() {
		return this.isRegression;
	}
	public void setIsRegression(boolean isRegression) {
		this.isRegression = isRegression;
	}
}
