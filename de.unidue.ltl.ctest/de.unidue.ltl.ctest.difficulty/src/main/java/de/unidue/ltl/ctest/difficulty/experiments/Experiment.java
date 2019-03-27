package de.unidue.ltl.ctest.difficulty.experiments;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.dkpro.lab.reporting.BatchReportBase;
import org.dkpro.tc.api.features.TcFeatureSet;

public class Experiment {
	
	private String experimentName = Long.toString(new Date().getTime());
	private boolean isRegression = true;
	private TcFeatureSet features = new TcFeatureSet();
	private List<AnalysisEngineDescription> preprocessingEngines = new ArrayList<>();
	private List<Class<? extends BatchReportBase>> reports = new ArrayList<>();
	
	public String getExperimentName() {
		return experimentName;
	}
	
	public void setExperimentName(String name) {
		experimentName = name;
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
	
	public List<AnalysisEngineDescription> getPreprocessing() {
		return preprocessingEngines;
	}
	
	public void setPreprocessing(List<AnalysisEngineDescription> engines) {
		this.preprocessingEngines = engines;
	}
	
	public List<Class <? extends BatchReportBase>> getReports() {
		return this.reports;
	}
	
	public void setReports(List<Class <? extends BatchReportBase>> reports) {
		this.reports = reports;
	}
}
