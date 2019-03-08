package de.unidue.ltl.ctest.difficulty.train;

import java.io.IOException;
import java.util.List;

import org.dkpro.lab.reporting.BatchReportBase;

import de.unidue.ltl.ctest.difficulty.experiments.Experiment;
import de.unidue.ltl.ctest.difficulty.experiments.Model;
import de.unidue.ltl.ctest.io.CTestReader;

public interface ModelTrainer {
	public void runTrainTest(Experiment experiment, Class<? extends CTestReader> reader, String trainPath, String testPath);
	
	public void runTrainTest(Experiment experiment, Class<? extends CTestReader> reader, String trainPath, String testPath, List<Class<? extends BatchReportBase>> reports);
	
	public void runCrossValidation(Experiment experiment, Class<? extends CTestReader> reader, String trainPath, int numFolds);
	
	public void runCrossValidation(Experiment experiment, Class<? extends CTestReader> reader, String trainPath, int numFolds, List<Class<? extends BatchReportBase>> reports);
	
	public void saveModel(Experiment experiment, Class<? extends CTestReader> reader, String trainPath, String modelPath) throws IOException;
	
	public Model loadModel(String modelPath) throws IOException;
}
